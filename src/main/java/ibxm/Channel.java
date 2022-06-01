package ibxm;

public class Channel {
    public int pattern_loop_row;
    private Module module;
    private Instrument instrument;
    private Sample sample;
    private int[] global_volume;
    private int[] current_note;
    private boolean linear_periods;
    private boolean fast_volume_slides;
    private boolean key_on;
    private boolean silent;
    private int sample_idx;
    private int sample_frac;
    private int step;
    private int left_gain;
    private int right_gain;
    private int volume;
    private int panning;
    private int fine_tune;
    private int period;
    private int porta_period;
    private int key_add;
    private int tremolo_speed;
    private int tremolo_depth;
    private int tremolo_tick;
    private int tremolo_wave;
    private int tremolo_add;
    private int vibrato_speed;
    private int vibrato_depth;
    private int vibrato_tick;
    private int vibrato_wave;
    private int vibrato_add;
    private int volume_slide_param;
    private int portamento_param;
    private int retrig_param;
    private int volume_envelope_tick;
    private int panning_envelope_tick;
    private int effect_tick;
    private int trigger_tick;
    private int fade_out_volume;
    private int random_seed;
    private int log_2_sampling_rate;
    private static final int LOG_2_29024 = LogTable.log_2(29024);
    private static final int LOG_2_8287 = LogTable.log_2(8287);
    private static final int LOG_2_8363 = LogTable.log_2(8363);
    private static final int LOG_2_1712 = LogTable.log_2(1712);
    private static final int[] sine_table = new int[]{
            0, 24, 49, 74, 97, 120, 141, 161, 180, 197, 212, 224, 235, 244, 250, 253, 255, 253, 250, 244, 235, 224, 212, 197, 180, 161, 141, 120, 97, 74, 49, 24
    };

    public Channel(Module mod, int sampling_rate, int[] global_vol) {
        this.module = mod;
        this.global_volume = global_vol;
        this.linear_periods = this.module.linear_periods;
        this.fast_volume_slides = this.module.fast_volume_slides;
        this.current_note = new int[5];
        this.log_2_sampling_rate = LogTable.log_2(sampling_rate);
    }

    public void reset() {
        this.tremolo_speed = 0;
        this.tremolo_depth = 0;
        this.tremolo_wave = 0;
        this.vibrato_speed = 0;
        this.vibrato_depth = 0;
        this.vibrato_wave = 0;
        this.volume_slide_param = 0;
        this.portamento_param = 0;
        this.retrig_param = 0;
        this.random_seed = 11256099;
        this.instrument = this.module.get_instrument(0);
        this.row(48, 256, 0, 0, 0);
    }

    public void resample(int[] mixing_buffer, int frame_offset, int frames, int quality) {
        if (!this.silent) {
            switch(quality) {
                case 1:
                    this.sample
                            .resample_linear(this.sample_idx, this.sample_frac, this.step, this.left_gain, this.right_gain, mixing_buffer, frame_offset, frames);
                    break;
                case 2:
                    this.sample
                            .resample_sinc(this.sample_idx, this.sample_frac, this.step, this.left_gain, this.right_gain, mixing_buffer, frame_offset, frames);
                    break;
                default:
                    this.sample
                            .resample_nearest(this.sample_idx, this.sample_frac, this.step, this.left_gain, this.right_gain, mixing_buffer, frame_offset, frames);
            }
        }

    }

    public void update_sample_idx(int samples) {
        this.sample_frac += this.step * samples;
        this.sample_idx += this.sample_frac >> 15;
        this.sample_frac &= 32767;
    }

    public void set_volume(int vol) {
        if (vol < 0) {
            vol = 0;
        }

        if (vol > 64) {
            vol = 64;
        }

        this.volume = vol;
    }

    public void set_panning(int pan) {
        if (pan < 0) {
            pan = 0;
        }

        if (pan > 255) {
            pan = 255;
        }

        this.panning = pan;
    }

    public void row(int key, int inst_idx, int volume_column, int effect, int effect_param) {
        effect &= 255;
        if (effect >= 48) {
            effect = 0;
        }

        if (effect == 0 && effect_param != 0) {
            effect = 64;
        }

        if (effect == 14) {
            effect = 48 + ((effect_param & 240) >> 4);
            effect_param &= 15;
        }

        if (effect == 33) {
            effect = 64 + ((effect_param & 240) >> 4);
            effect_param &= 15;
        }

        this.current_note[0] = key;
        this.current_note[1] = inst_idx;
        this.current_note[2] = volume_column;
        this.current_note[3] = effect;
        this.current_note[4] = effect_param;
        this.effect_tick = 0;
        ++this.trigger_tick;
        this.update_envelopes();
        this.key_add = 0;
        this.vibrato_add = 0;
        this.tremolo_add = 0;
        if (effect != 61 || effect_param <= 0) {
            this.trigger(key, inst_idx, volume_column, effect);
            switch(volume_column & 240) {
                case 0:
                case 96:
                case 112:
                case 208:
                case 224:
                    break;
                case 128:
                    this.set_volume(this.volume - (volume_column & 15));
                    break;
                case 144:
                    this.set_volume(this.volume + (volume_column & 15));
                    break;
                case 160:
                    this.set_vibrato_speed(volume_column & 15);
                    break;
                case 176:
                    this.set_vibrato_depth(volume_column & 15);
                    this.vibrato();
                    break;
                case 192:
                    this.set_panning((volume_column & 15) << 4);
                    break;
                case 240:
                    this.set_portamento_param(volume_column & 15);
                    break;
                default:
                    this.set_volume(volume_column - 16);
            }
        }

        if (this.instrument.vibrato_depth > 0) {
            this.auto_vibrato();
        }

        switch(effect) {
            case 1:
                this.set_portamento_param(effect_param);
                this.portamento_up();
                break;
            case 2:
                this.set_portamento_param(effect_param);
                this.portamento_down();
                break;
            case 3:
                this.set_portamento_param(effect_param);
                break;
            case 4:
                this.set_vibrato_speed((effect_param & 240) >> 4);
                this.set_vibrato_depth(effect_param & 15);
                this.vibrato();
                break;
            case 5:
                this.set_volume_slide_param(effect_param);
                this.volume_slide();
                break;
            case 6:
                this.set_volume_slide_param(effect_param);
                this.vibrato();
                this.volume_slide();
                break;
            case 7:
                this.set_tremolo_speed((effect_param & 240) >> 4);
                this.set_tremolo_depth(effect_param & 15);
                this.tremolo();
                break;
            case 8:
                this.set_panning(effect_param);
                break;
            case 9:
                this.set_sample_index(effect_param << 8);
                break;
            case 10:
                this.set_volume_slide_param(effect_param);
                this.volume_slide();
            case 11:
            case 13:
            case 14:
            case 15:
            case 18:
            case 19:
            case 22:
            case 23:
            case 24:
            case 26:
            case 28:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 48:
            case 51:
            case 53:
            case 54:
            case 56:
            case 61:
            case 62:
            case 63:
            case 64:
            default:
                break;
            case 12:
                this.set_volume(effect_param);
                break;
            case 16:
                this.set_global_volume(effect_param);
                break;
            case 17:
                this.set_volume_slide_param(effect_param);
                break;
            case 20:
                if (effect_param == 0) {
                    this.key_on = false;
                }
                break;
            case 21:
                this.set_envelope_tick(effect_param);
                break;
            case 25:
                this.set_volume_slide_param(effect_param);
                break;
            case 27:
                this.set_retrig_param(effect_param);
                this.retrig_volume_slide();
                break;
            case 29:
                this.set_retrig_param(effect_param);
                this.tremor();
                break;
            case 36:
                this.set_vibrato_speed((effect_param & 240) >> 4);
                this.set_vibrato_depth(effect_param & 15);
                this.fine_vibrato();
                break;
            case 49:
                this.set_portamento_param(240 | effect_param);
                this.portamento_up();
                break;
            case 50:
                this.set_portamento_param(240 | effect_param);
                this.portamento_down();
                break;
            case 52:
                this.set_vibrato_wave(effect_param);
                break;
            case 55:
                this.set_tremolo_wave(effect_param);
                break;
            case 57:
                this.set_retrig_param(effect_param);
                break;
            case 58:
                this.set_volume_slide_param(effect_param << 4 | 15);
                this.volume_slide();
                break;
            case 59:
                this.set_volume_slide_param(240 | effect_param);
                this.volume_slide();
                break;
            case 60:
                if (effect_param == 0) {
                    this.set_volume(0);
                }
                break;
            case 65:
                this.set_portamento_param(224 | effect_param);
                this.portamento_up();
                break;
            case 66:
                this.set_portamento_param(224 | effect_param);
                this.portamento_down();
        }

        this.calculate_amplitude();
        this.calculate_frequency();
    }

    public void tick() {
        int volume_column = this.current_note[2];
        int effect = this.current_note[3];
        int effect_param = this.current_note[4];
        ++this.effect_tick;
        if (effect == 61 && effect_param == this.effect_tick) {
            this.row(this.current_note[0], this.current_note[1], volume_column, 0, 0);
        } else {
            ++this.trigger_tick;
            ++this.vibrato_tick;
            ++this.tremolo_tick;
            this.update_envelopes();
            this.key_add = 0;
            this.vibrato_add = 0;
            this.tremolo_add = 0;
            if (this.instrument.vibrato_depth > 0) {
                this.auto_vibrato();
            }

            switch(volume_column & 240) {
                case 96:
                    this.set_volume(this.volume - (volume_column & 15));
                    break;
                case 112:
                    this.set_volume(this.volume + (volume_column & 15));
                    break;
                case 176:
                    this.vibrato();
                    break;
                case 208:
                    this.set_panning(this.panning - (volume_column & 15));
                    break;
                case 224:
                    this.set_panning(this.panning + (volume_column & 15));
                    break;
                case 240:
                    this.tone_portamento();
            }

            switch(effect) {
                case 1:
                    this.portamento_up();
                    break;
                case 2:
                    this.portamento_down();
                    break;
                case 3:
                    this.tone_portamento();
                    break;
                case 4:
                    this.vibrato();
                    break;
                case 5:
                    this.tone_portamento();
                    this.volume_slide();
                    break;
                case 6:
                    this.vibrato();
                    this.volume_slide();
                    break;
                case 7:
                    this.tremolo();
                case 8:
                case 9:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                case 18:
                case 19:
                case 21:
                case 22:
                case 23:
                case 24:
                case 26:
                case 28:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 58:
                case 59:
                case 61:
                case 62:
                case 63:
                default:
                    break;
                case 10:
                    this.volume_slide();
                    break;
                case 17:
                    this.global_volume_slide();
                    break;
                case 20:
                    if (this.effect_tick == effect_param) {
                        this.key_on = false;
                    }
                    break;
                case 25:
                    this.panning_slide();
                    break;
                case 27:
                    this.retrig_volume_slide();
                    break;
                case 29:
                    this.tremor();
                    break;
                case 36:
                    this.fine_vibrato();
                    break;
                case 57:
                    this.retrig_volume_slide();
                    break;
                case 60:
                    if (this.effect_tick == effect_param) {
                        this.set_volume(0);
                    }
                    break;
                case 64:
                    switch(this.effect_tick % 3) {
                        case 1:
                            this.key_add = (effect_param & 240) >> 4;
                            break;
                        case 2:
                            this.key_add = effect_param & 15;
                    }
            }
        }

        this.calculate_amplitude();
        this.calculate_frequency();
    }

    private void set_vibrato_speed(int speed) {
        if (speed > 0) {
            this.vibrato_speed = speed;
        }

    }

    private void set_vibrato_depth(int depth) {
        if (depth > 0) {
            this.vibrato_depth = depth;
        }

    }

    private void set_vibrato_wave(int wave) {
        if (wave < 0 || wave > 7) {
            wave = 0;
        }

        this.vibrato_wave = wave;
    }

    private void set_tremolo_speed(int speed) {
        if (speed > 0) {
            this.tremolo_speed = speed;
        }

    }

    private void set_tremolo_depth(int depth) {
        if (depth > 0) {
            this.tremolo_depth = depth;
        }

    }

    private void set_tremolo_wave(int wave) {
        if (wave < 0 || wave > 7) {
            wave = 0;
        }

        this.tremolo_wave = wave;
    }

    private void vibrato() {
        int vibrato_phase = this.vibrato_tick * this.vibrato_speed;
        this.vibrato_add += this.waveform(vibrato_phase, this.vibrato_wave) * this.vibrato_depth >> 5;
    }

    private void fine_vibrato() {
        int vibrato_phase = this.vibrato_tick * this.vibrato_speed;
        this.vibrato_add += this.waveform(vibrato_phase, this.vibrato_wave) * this.vibrato_depth >> 7;
    }

    private void tremolo() {
        int tremolo_phase = this.tremolo_tick * this.tremolo_speed;
        this.tremolo_add += this.waveform(tremolo_phase, this.tremolo_wave) * this.tremolo_depth >> 6;
    }

    private void set_portamento_param(int param) {
        if (param != 0) {
            this.portamento_param = param;
        }

    }

    private void tone_portamento() {
        if (this.porta_period < this.period) {
            int new_period = this.period - (this.portamento_param << 2);
            if (new_period < this.porta_period) {
                new_period = this.porta_period;
            }

            this.set_period(new_period);
        }

        if (this.porta_period > this.period) {
            int new_period = this.period + (this.portamento_param << 2);
            if (new_period > this.porta_period) {
                new_period = this.porta_period;
            }

            this.set_period(new_period);
        }

    }

    private void portamento_up() {
        if ((this.portamento_param & 240) == 224) {
            if (this.effect_tick == 0) {
                this.set_period(this.period - (this.portamento_param & 15));
            }
        } else if ((this.portamento_param & 240) == 240) {
            if (this.effect_tick == 0) {
                this.set_period(this.period - ((this.portamento_param & 15) << 2));
            }
        } else if (this.effect_tick > 0) {
            this.set_period(this.period - (this.portamento_param << 2));
        }

    }

    private void portamento_down() {
        if ((this.portamento_param & 240) == 224) {
            if (this.effect_tick == 0) {
                this.set_period(this.period + (this.portamento_param & 15));
            }
        } else if ((this.portamento_param & 240) == 240) {
            if (this.effect_tick == 0) {
                this.set_period(this.period + ((this.portamento_param & 15) << 2));
            }
        } else if (this.effect_tick > 0) {
            this.set_period(this.period + (this.portamento_param << 2));
        }

    }

    private void set_period(int p) {
        if (p < 32) {
            p = 32;
        }

        if (p > 32768) {
            p = 32768;
        }

        this.period = p;
    }

    private void set_global_volume(int vol) {
        if (vol < 0) {
            vol = 0;
        }

        if (vol > 64) {
            vol = 64;
        }

        this.global_volume[0] = vol;
    }

    private void set_volume_slide_param(int param) {
        if (param != 0) {
            this.volume_slide_param = param;
        }

    }

    private void global_volume_slide() {
        int up = (this.volume_slide_param & 240) >> 4;
        int down = this.volume_slide_param & 15;
        this.set_global_volume(this.global_volume[0] + up - down);
    }

    private void volume_slide() {
        int up = (this.volume_slide_param & 240) >> 4;
        int down = this.volume_slide_param & 15;
        if (down == 15 && up > 0) {
            if (this.effect_tick == 0) {
                this.set_volume(this.volume + up);
            }
        } else if (up == 15 && down > 0) {
            if (this.effect_tick == 0) {
                this.set_volume(this.volume - down);
            }
        } else if (this.effect_tick > 0 || this.fast_volume_slides) {
            this.set_volume(this.volume + up - down);
        }

    }

    private void panning_slide() {
        int left = (this.volume_slide_param & 240) >> 4;
        int right = this.volume_slide_param & 15;
        this.set_panning(this.panning - left + right);
    }

    private void set_retrig_param(int param) {
        if (param != 0) {
            this.retrig_param = param;
        }

    }

    private void tremor() {
        int on_ticks = ((this.retrig_param & 240) >> 4) + 1;
        int cycle_length = on_ticks + (this.retrig_param & 15) + 1;
        int cycle_index = this.trigger_tick % cycle_length;
        if (cycle_index >= on_ticks) {
            this.tremolo_add = -64;
        }

    }

    private void retrig_volume_slide() {
        int retrig_volume = (this.retrig_param & 240) >> 4;
        int retrig_tick = this.retrig_param & 15;
        if (retrig_tick > 0 && this.trigger_tick % retrig_tick == 0) {
            this.set_sample_index(0);
            switch(retrig_volume) {
                case 1:
                    this.set_volume(this.volume - 1);
                    break;
                case 2:
                    this.set_volume(this.volume - 2);
                    break;
                case 3:
                    this.set_volume(this.volume - 4);
                    break;
                case 4:
                    this.set_volume(this.volume - 8);
                    break;
                case 5:
                    this.set_volume(this.volume - 16);
                    break;
                case 6:
                    this.set_volume(this.volume - this.volume / 3);
                    break;
                case 7:
                    this.set_volume(this.volume / 2);
                case 8:
                default:
                    break;
                case 9:
                    this.set_volume(this.volume + 1);
                    break;
                case 10:
                    this.set_volume(this.volume + 2);
                    break;
                case 11:
                    this.set_volume(this.volume + 4);
                    break;
                case 12:
                    this.set_volume(this.volume + 8);
                    break;
                case 13:
                    this.set_volume(this.volume + 16);
                    break;
                case 14:
                    this.set_volume(this.volume + this.volume / 2);
                    break;
                case 15:
                    this.set_volume(this.volume * 2);
            }
        }

    }

    private void set_sample_index(int index) {
        if (index < 0) {
            index = 0;
        }

        this.sample_idx = index;
        this.sample_frac = 0;
    }

    private void set_envelope_tick(int tick) {
        this.volume_envelope_tick = tick;
        this.panning_envelope_tick = tick;
    }

    private void trigger(int key, int instrument_idx, int volume_column, int effect) {
        if (instrument_idx > 0) {
            this.instrument = this.module.get_instrument(instrument_idx);
            this.sample = this.instrument.get_sample_from_key(key);
            this.set_volume(this.sample.volume);
            if (this.sample.set_panning) {
                this.set_panning(this.sample.panning);
            }

            this.set_envelope_tick(0);
            this.fade_out_volume = 32768;
            this.key_on = true;
        }

        if (key > 0) {
            if (key < 97) {
                this.porta_period = this.key_to_period(key);
                if (effect != 3 && effect != 5 && (volume_column & 240) != 240) {
                    this.trigger_tick = 0;
                    if (this.vibrato_wave < 4) {
                        this.vibrato_tick = 0;
                    }

                    if (this.tremolo_wave < 4) {
                        this.tremolo_tick = 0;
                    }

                    this.set_period(this.porta_period);
                    this.set_sample_index(0);
                }
            } else {
                this.key_on = false;
            }
        }

    }

    private void update_envelopes() {
        if (this.instrument.volume_envelope_active) {
            if (!this.key_on) {
                this.fade_out_volume -= this.instrument.volume_fade_out & 65535;
                if (this.fade_out_volume < 0) {
                    this.fade_out_volume = 0;
                }
            }

            Envelope envelope = this.instrument.get_volume_envelope();
            this.volume_envelope_tick = envelope.next_tick(this.volume_envelope_tick, this.key_on);
        }

        if (this.instrument.panning_envelope_active) {
            Envelope envelope = this.instrument.get_panning_envelope();
            this.panning_envelope_tick = envelope.next_tick(this.panning_envelope_tick, this.key_on);
        }

    }

    private void auto_vibrato() {
        int sweep = this.instrument.vibrato_sweep & 0xFF;
        int depth = this.instrument.vibrato_depth & 15;
        int rate = this.instrument.vibrato_rate & 63;
        if (this.trigger_tick < sweep) {
            depth = depth * this.trigger_tick / sweep;
        }

        this.vibrato_add += this.waveform(this.trigger_tick * rate, 0) * depth >> 9;
    }

    private int waveform(int phase, int wform) {
        int amplitude = 0;
        switch(wform & 3) {
            case 0:
                if ((phase & 32) == 0) {
                    amplitude = sine_table[phase & 31];
                } else {
                    amplitude = -sine_table[phase & 31];
                }
                break;
            case 1:
                if ((phase & 32) == 0) {
                    amplitude = (phase & 31) << 3;
                } else {
                    amplitude = ((phase & 31) << 3) - 255;
                }
                break;
            case 2:
                if ((phase & 32) == 0) {
                    amplitude = 255;
                } else {
                    amplitude = -255;
                }
                break;
            case 3:
                amplitude = (this.random_seed >> 15) - 255;
                this.random_seed = this.random_seed * 65 + 17 & 16777215;
        }

        return amplitude;
    }

    private int key_to_period(int key) {
        int octave = (key << 15) / 12 + this.sample.transpose;
        int period_out;
        if (this.linear_periods) {
            period_out = 7744 - (octave * 768 >> 15);
        } else {
            int log_2_period = LOG_2_29024 - octave;
            period_out = LogTable.raise_2(log_2_period);
            period_out >>= 14;
            period_out = (period_out >> 1) + (period_out & 1);
        }

        return period_out;
    }

    private void calculate_amplitude() {
        int envelope_volume = 0;
        if (this.instrument.volume_envelope_active) {
            Envelope envelope = this.instrument.get_volume_envelope();
            envelope_volume = envelope.calculate_ampl(this.volume_envelope_tick);
        } else if (this.key_on) {
            envelope_volume = 64;
        }

        int tremolo_volume = this.volume + this.tremolo_add;
        if (tremolo_volume < 0) {
            tremolo_volume = 0;
        }

        if (tremolo_volume > 64) {
            tremolo_volume = 64;
        }

        int amplitude = tremolo_volume << 9;
        amplitude = amplitude * envelope_volume >> 6;
        amplitude = amplitude * this.fade_out_volume >> 15;
        amplitude = amplitude * this.global_volume[0] >> 6;
        amplitude = amplitude * this.module.channel_gain >> 15;
        this.silent = this.sample.has_finished(this.sample_idx);
        if (amplitude <= 0) {
            this.silent = true;
        } else {
            int envelope_panning = 32;
            if (this.instrument.panning_envelope_active) {
                Envelope envelope = this.instrument.get_panning_envelope();
                envelope_panning = envelope.calculate_ampl(this.panning_envelope_tick);
            }

            int mixer_panning = (this.panning & 0xFF) << 7;
            int panning_range = 32768 - mixer_panning;
            if (panning_range > mixer_panning) {
                panning_range = mixer_panning;
            }

            mixer_panning += panning_range * (envelope_panning - 32) >> 5;
            this.left_gain = amplitude * (32768 - mixer_panning) >> 15;
            this.right_gain = amplitude * mixer_panning >> 15;
        }

    }

    private void calculate_frequency() {
        int vibrato_period = this.period + this.vibrato_add;
        if (vibrato_period < 32) {
            vibrato_period = 32;
        }

        if (vibrato_period > 32768) {
            vibrato_period = 32768;
        }

        int log_2_freq;
        if (this.linear_periods) {
            log_2_freq = LOG_2_8363 + (4608 - vibrato_period << 15) / 768;
        } else {
            log_2_freq = this.module.pal ? LOG_2_8287 : LOG_2_8363;
            log_2_freq = log_2_freq + LOG_2_1712 - LogTable.log_2(vibrato_period);
        }

        log_2_freq += (this.key_add << 15) / 12;
        this.step = LogTable.raise_2(log_2_freq - this.log_2_sampling_rate);
    }
}
