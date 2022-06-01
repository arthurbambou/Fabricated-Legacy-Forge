package ibxm;

public class IBXM {
    public static final String VERSION = "ibxm alpha 51 (c)2008 mumart@gmail.com";
    public static final int FP_SHIFT = 15;
    public static final int FP_ONE = 32768;
    public static final int FP_MASK = 32767;
    private int sampling_rate;
    private int resampling_quality;
    private int volume_ramp_length;
    private int tick_length_samples;
    private int current_tick_samples;
    private int[] mixing_buffer;
    private int[] volume_ramp_buffer;
    private Module module;
    private Channel[] channels;
    private int[] global_volume;
    private int[] note;
    private int current_sequence_index;
    private int next_sequence_index;
    private int current_row;
    private int next_row;
    private int tick_counter;
    private int ticks_per_row;
    private int pattern_loop_count;
    private int pattern_loop_channel;

    public IBXM(int sample_rate) {
        if (sample_rate < 8000) {
            sample_rate = 8000;
        }

        this.sampling_rate = sample_rate;
        this.volume_ramp_length = this.sampling_rate >> 10;
        this.volume_ramp_buffer = new int[this.volume_ramp_length * 2];
        this.mixing_buffer = new int[this.sampling_rate / 6];
        this.global_volume = new int[1];
        this.note = new int[5];
        this.set_module(new Module());
        this.set_resampling_quality(1);
    }

    public void set_module(Module m) {
        this.module = m;
        this.channels = new Channel[this.module.get_num_channels()];

        for(int channel_idx = 0; channel_idx < this.channels.length; ++channel_idx) {
            this.channels[channel_idx] = new Channel(this.module, this.sampling_rate, this.global_volume);
        }

        this.set_sequence_index(0, 0);
    }

    public void set_resampling_quality(int quality) {
        this.resampling_quality = quality;
    }

    public int calculate_song_duration() {
        this.set_sequence_index(0, 0);
        this.next_tick();
        int song_duration = this.tick_length_samples;

        while(!this.next_tick()) {
            song_duration += this.tick_length_samples;
        }

        this.set_sequence_index(0, 0);
        return song_duration;
    }

    public void set_sequence_index(int sequence_index, int row) {
        this.global_volume[0] = 64;

        for(int channel_idx = 0; channel_idx < this.channels.length; ++channel_idx) {
            this.channels[channel_idx].reset();
            this.channels[channel_idx].set_panning(this.module.get_initial_panning(channel_idx));
        }

        this.set_global_volume(this.module.global_volume);
        this.set_speed(6);
        this.set_speed(this.module.default_speed);
        this.set_tempo(125);
        this.set_tempo(this.module.default_tempo);
        this.pattern_loop_count = -1;
        this.next_sequence_index = sequence_index;
        this.next_row = row;
        this.tick_counter = 0;
        this.current_tick_samples = this.tick_length_samples;
        this.clear_vol_ramp_buffer();
    }

    public void seek(int sample_position) {
        this.set_sequence_index(0, 0);
        this.next_tick();

        while(sample_position > this.tick_length_samples) {
            sample_position -= this.tick_length_samples;
            this.next_tick();
        }

        this.mix_tick();
        this.current_tick_samples = sample_position;
    }

    public void get_audio(byte[] output_buffer, int frames) {
        int output_idx = 0;

        while(frames > 0) {
            int count = this.tick_length_samples - this.current_tick_samples;
            if (count > frames) {
                count = frames;
            }

            int mix_idx = this.current_tick_samples << 1;

            for(int mix_end = mix_idx + (count << 1) - 1; mix_idx <= mix_end; ++mix_idx) {
                int amplitude = this.mixing_buffer[mix_idx];
                if (amplitude > 32767) {
                    amplitude = 32767;
                }

                if (amplitude < -32768) {
                    amplitude = -32768;
                }

                output_buffer[output_idx] = (byte)(amplitude >> 8);
                output_buffer[output_idx + 1] = (byte)(amplitude & 0xFF);
                output_idx += 2;
            }

            this.current_tick_samples = mix_idx >> 1;
            frames -= count;
            if (frames > 0) {
                this.next_tick();
                this.mix_tick();
                this.current_tick_samples = 0;
            }
        }

    }

    private void mix_tick() {
        int mix_idx = 0;

        for(int mix_len = this.tick_length_samples + this.volume_ramp_length << 1; mix_idx < mix_len; ++mix_idx) {
            this.mixing_buffer[mix_idx] = 0;
        }

        for(int channel_idx = 0; channel_idx < this.channels.length; ++channel_idx) {
            int var4 = this.tick_length_samples + this.volume_ramp_length;
            this.channels[channel_idx].resample(this.mixing_buffer, 0, var4, this.resampling_quality);
        }

        this.volume_ramp();
    }

    private boolean next_tick() {
        for(int channel_idx = 0; channel_idx < this.channels.length; ++channel_idx) {
            this.channels[channel_idx].update_sample_idx(this.tick_length_samples);
        }

        --this.tick_counter;
        boolean song_end;
        if (this.tick_counter <= 0) {
            this.tick_counter = this.ticks_per_row;
            song_end = this.next_row();
        } else {
            for(int var3 = 0; var3 < this.channels.length; ++var3) {
                this.channels[var3].tick();
            }

            song_end = false;
        }

        return song_end;
    }

    private boolean next_row() {
        boolean song_end = false;
        if (this.next_sequence_index < 0) {
            this.next_sequence_index = 0;
            this.next_row = 0;
        }

        if (this.next_sequence_index >= this.module.get_sequence_length()) {
            song_end = true;
            this.next_sequence_index = this.module.restart_sequence_index;
            if (this.next_sequence_index < 0) {
                this.next_sequence_index = 0;
            }

            if (this.next_sequence_index >= this.module.get_sequence_length()) {
                this.next_sequence_index = 0;
            }

            this.next_row = 0;
        }

        if (this.next_sequence_index < this.current_sequence_index) {
            song_end = true;
        }

        if (this.next_sequence_index == this.current_sequence_index && this.next_row <= this.current_row && this.pattern_loop_count < 0) {
            song_end = true;
        }

        this.current_sequence_index = this.next_sequence_index;
        Pattern pattern = this.module.get_pattern_from_sequence(this.current_sequence_index);
        if (this.next_row < 0 || this.next_row >= pattern.num_rows) {
            this.next_row = 0;
        }

        this.current_row = this.next_row++;
        if (this.next_row >= pattern.num_rows) {
            this.next_sequence_index = this.current_sequence_index + 1;
            this.next_row = 0;
        }

        for(int channel_idx = 0; channel_idx < this.channels.length; ++channel_idx) {
            pattern.get_note(this.note, this.current_row * this.channels.length + channel_idx);
            int effect = this.note[3];
            int effect_param = this.note[4];
            this.channels[channel_idx].row(this.note[0], this.note[1], this.note[2], effect, effect_param);
            switch(effect) {
                case 11:
                    if (this.pattern_loop_count < 0) {
                        this.next_sequence_index = effect_param;
                        this.next_row = 0;
                    }
                    break;
                case 13:
                    if (this.pattern_loop_count < 0) {
                        this.next_sequence_index = this.current_sequence_index + 1;
                        this.next_row = (effect_param >> 4) * 10 + (effect_param & 15);
                    }
                    break;
                case 14:
                    switch(effect_param & 240) {
                        case 96:
                            if ((effect_param & 15) == 0) {
                                this.channels[channel_idx].pattern_loop_row = this.current_row;
                            }

                            if (this.channels[channel_idx].pattern_loop_row < this.current_row) {
                                if (this.pattern_loop_count < 0) {
                                    this.pattern_loop_count = effect_param & 15;
                                    this.pattern_loop_channel = channel_idx;
                                }

                                if (this.pattern_loop_channel == channel_idx) {
                                    if (this.pattern_loop_count == 0) {
                                        this.channels[channel_idx].pattern_loop_row = this.current_row + 1;
                                    } else {
                                        this.next_row = this.channels[channel_idx].pattern_loop_row;
                                        this.next_sequence_index = this.current_sequence_index;
                                    }

                                    --this.pattern_loop_count;
                                }
                            }
                            continue;
                        case 224:
                            this.tick_counter += this.ticks_per_row * (effect_param & 15);
                        default:
                            continue;
                    }
                case 15:
                    if (effect_param < 32) {
                        this.set_speed(effect_param);
                        this.tick_counter = this.ticks_per_row;
                    } else {
                        this.set_tempo(effect_param);
                    }
                    break;
                case 37:
                    this.set_speed(effect_param);
                    this.tick_counter = this.ticks_per_row;
            }
        }

        return song_end;
    }

    private void set_global_volume(int volume) {
        if (volume < 0) {
            volume = 0;
        }

        if (volume > 64) {
            volume = 64;
        }

        this.global_volume[0] = volume;
    }

    private void set_speed(int speed) {
        if (speed > 0 && speed < 256) {
            this.ticks_per_row = speed;
        }

    }

    private void set_tempo(int bpm) {
        if (bpm > 31 && bpm < 256) {
            this.tick_length_samples = this.sampling_rate * 5 / (bpm * 2);
        }

    }

    private void volume_ramp() {
        int sample = 0;
        int volume_ramp_delta = 32768 / this.volume_ramp_length;
        int volume = 0;
        int ramp_idx = 0;
        int next_idx = 2 * this.tick_length_samples;

        for(int ramp_end = this.volume_ramp_length * 2 - 1; ramp_idx <= ramp_end; ramp_idx += 2) {
            sample = this.volume_ramp_buffer[ramp_idx] * (32768 - volume) >> 15;
            this.mixing_buffer[ramp_idx] = sample + (this.mixing_buffer[ramp_idx] * volume >> 15);
            this.volume_ramp_buffer[ramp_idx] = this.mixing_buffer[next_idx + ramp_idx];
            sample = this.volume_ramp_buffer[ramp_idx + 1] * (32768 - volume) >> 15;
            this.mixing_buffer[ramp_idx + 1] = sample + (this.mixing_buffer[ramp_idx + 1] * volume >> 15);
            this.volume_ramp_buffer[ramp_idx + 1] = this.mixing_buffer[next_idx + ramp_idx + 1];
            volume += volume_ramp_delta;
        }

    }

    private void clear_vol_ramp_buffer() {
        int ramp_idx = 0;

        for(int ramp_end = this.volume_ramp_length * 2 - 1; ramp_idx <= ramp_end; ++ramp_idx) {
            this.volume_ramp_buffer[ramp_idx] = 0;
        }

    }
}
