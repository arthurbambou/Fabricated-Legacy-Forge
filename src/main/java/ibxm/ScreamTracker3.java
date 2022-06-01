package ibxm;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class ScreamTracker3 {
    private static final int[] effect_map = new int[]{
            255, 37, 11, 13, 10, 2, 1, 3, 4, 29, 0, 6, 5, 255, 255, 9, 255, 27, 7, 14, 15, 36, 16, 255, 255, 255, 255, 255, 255, 255, 255, 255
    };
    private static final int[] effect_s_map = new int[]{0, 3, 5, 4, 7, 255, 255, 255, 8, 255, 9, 6, 12, 13, 14, 15};

    public ScreamTracker3() {
    }

    public static boolean is_s3m(byte[] header_96_bytes) {
        String s3m_identifier = ascii_text(header_96_bytes, 44, 4);
        return s3m_identifier.equals("SCRM");
    }

    public static Module load_s3m(byte[] header_96_bytes, DataInput data_input) throws IOException {
        byte[] s3m_file = read_s3m_file(header_96_bytes, data_input);
        Module module = new Module();
        module.song_title = ascii_text(s3m_file, 0, 28);
        int num_pattern_orders = get_num_pattern_orders(s3m_file);
        int num_instruments = get_num_instruments(s3m_file);
        int num_patterns = get_num_patterns(s3m_file);
        int flags = unsigned_short_le(s3m_file, 38);
        int tracker_version = unsigned_short_le(s3m_file, 40);
        if ((flags & 64) == 64 || tracker_version == 4864) {
            module.fast_volume_slides = true;
        }

        boolean signed_samples = false;
        if (unsigned_short_le(s3m_file, 42) == 1) {
            signed_samples = true;
        }

        module.global_volume = s3m_file[48] & 255;
        module.default_speed = s3m_file[49] & 255;
        module.default_tempo = s3m_file[50] & 255;
        int master_volume = s3m_file[51] & 127;
        module.channel_gain = master_volume << 15 >> 7;
        boolean stereo_mode = (s3m_file[51] & 128) == 128;
        boolean default_panning = (s3m_file[53] & 255) == 252;
        int[] channel_map = new int[32];
        int num_channels = 0;

        for(int channel_idx = 0; channel_idx < 32; ++channel_idx) {
            int channel_config = s3m_file[64 + channel_idx] & 255;
            channel_map[channel_idx] = -1;
            if (channel_config < 16) {
                channel_map[channel_idx] = num_channels++;
            }
        }

        module.set_num_channels(num_channels);
        int panning_offset = 96 + num_pattern_orders + num_instruments * 2 + num_patterns * 2;

        for(int var26 = 0; var26 < 32; ++var26) {
            if (channel_map[var26] >= 0) {
                int panning = 7;
                if (stereo_mode) {
                    panning = 12;
                    if ((s3m_file[64 + var26] & 255) < 8) {
                        panning = 3;
                    }
                }

                if (default_panning) {
                    flags = s3m_file[panning_offset + var26] & 255;
                    if ((flags & 32) == 32) {
                        panning = flags & 15;
                    }
                }

                module.set_initial_panning(channel_map[var26], panning * 17);
            }
        }

        int[] sequence = read_s3m_sequence(s3m_file);
        module.set_sequence_length(sequence.length);

        for(int order_idx = 0; order_idx < sequence.length; ++order_idx) {
            module.set_sequence(order_idx, sequence[order_idx]);
        }

        module.set_num_instruments(num_instruments);

        for(int instrument_idx = 0; instrument_idx < num_instruments; ++instrument_idx) {
            Instrument instrument = read_s3m_instrument(s3m_file, instrument_idx, signed_samples);
            module.set_instrument(instrument_idx + 1, instrument);
        }

        module.set_num_patterns(num_patterns);

        for(int pattern_idx = 0; pattern_idx < num_patterns; ++pattern_idx) {
            module.set_pattern(pattern_idx, read_s3m_pattern(s3m_file, pattern_idx, channel_map));
        }

        return module;
    }

    private static int[] read_s3m_sequence(byte[] s3m_file) {
        int num_pattern_orders = get_num_pattern_orders(s3m_file);
        int sequence_length = 0;

        for(int order_idx = 0; order_idx < num_pattern_orders; ++order_idx) {
            int pattern_order = s3m_file[96 + order_idx] & 255;
            if (pattern_order == 255) {
                break;
            }

            if (pattern_order < 254) {
                ++sequence_length;
            }
        }

        int[] sequence = new int[sequence_length];
        int sequence_idx = 0;

        for(int var7 = 0; var7 < num_pattern_orders; ++var7) {
            int pattern_order = s3m_file[96 + var7] & 255;
            if (pattern_order == 255) {
                break;
            }

            if (pattern_order < 254) {
                sequence[sequence_idx] = pattern_order;
                ++sequence_idx;
            }
        }

        return sequence;
    }

    private static Instrument read_s3m_instrument(byte[] s3m_file, int instrument_idx, boolean signed_samples) {
        int instrument_offset = get_instrument_offset(s3m_file, instrument_idx);
        Instrument instrument = new Instrument();
        instrument.name = ascii_text(s3m_file, instrument_offset + 48, 28);
        Sample sample = new Sample();
        if (s3m_file[instrument_offset] == 1) {
            int sample_data_length = get_sample_data_length(s3m_file, instrument_offset);
            int loop_start = unsigned_short_le(s3m_file, instrument_offset + 20);
            int loop_length = unsigned_short_le(s3m_file, instrument_offset + 24) - loop_start;
            sample.volume = s3m_file[instrument_offset + 28] & 255;
            if (s3m_file[instrument_offset + 30] != 0) {
                throw new IllegalArgumentException("ScreamTracker3: Packed samples not supported!");
            }

            if ((s3m_file[instrument_offset + 31] & 1) == 0) {
                loop_length = 0;
            }

            if ((s3m_file[instrument_offset + 31] & 2) != 0) {
                throw new IllegalArgumentException("ScreamTracker3: Stereo samples not supported!");
            }

            boolean sixteen_bit = (s3m_file[instrument_offset + 31] & 4) != 0;
            int c2_rate = unsigned_short_le(s3m_file, instrument_offset + 32);
            sample.transpose = LogTable.log_2(c2_rate) - LogTable.log_2(8363);
            int sample_data_offset = get_sample_data_offset(s3m_file, instrument_offset);
            short[] sample_data;
            if (sixteen_bit) {
                if (signed_samples) {
                    throw new IllegalArgumentException("ScreamTracker3: Signed 16-bit samples not supported!");
                }

                sample_data_length >>= 1;
                sample_data = new short[sample_data_length];

                for(int sample_idx = 0; sample_idx < sample_data_length; ++sample_idx) {
                    int amplitude = s3m_file[sample_data_offset + sample_idx * 2] & 255;
                    amplitude |= (s3m_file[sample_data_offset + sample_idx * 2 + 1] & 255) << 8;
                    sample_data[sample_idx] = (short)(amplitude - 32768);
                }
            } else {
                sample_data = new short[sample_data_length];
                if (signed_samples) {
                    for(int sample_idx = 0; sample_idx < sample_data_length; ++sample_idx) {
                        int amplitude = s3m_file[sample_data_offset + sample_idx] << 8;
                        sample_data[sample_idx] = (short)amplitude;
                    }
                } else {
                    for(int sample_idx = 0; sample_idx < sample_data_length; ++sample_idx) {
                        int amplitude = (s3m_file[sample_data_offset + sample_idx] & 255) << 8;
                        sample_data[sample_idx] = (short)(amplitude - 32768);
                    }
                }
            }

            sample.set_sample_data(sample_data, loop_start, loop_length, false);
        }

        instrument.set_num_samples(1);
        instrument.set_sample(0, sample);
        return instrument;
    }

    private static Pattern read_s3m_pattern(byte[] s3m_file, int pattern_idx, int[] channel_map) {
        int num_channels = 0;

        for(int channel_idx = 0; channel_idx < 32; ++channel_idx) {
            if (channel_map[channel_idx] >= num_channels) {
                num_channels = channel_idx + 1;
            }
        }

        int num_notes = num_channels * 64;
        byte[] pattern_data = new byte[num_notes * 5];
        int row_idx = 0;
        int pattern_offset = get_pattern_offset(s3m_file, pattern_idx) + 2;

        while(row_idx < 64) {
            int token = s3m_file[pattern_offset] & 255;
            ++pattern_offset;
            if (token > 0) {
                int var16 = channel_map[token & 31];
                int note_idx = (num_channels * row_idx + var16) * 5;
                if ((token & 32) == 32) {
                    if (var16 >= 0) {
                        int key = s3m_file[pattern_offset] & 255;
                        if (key == 255) {
                            key = 0;
                        } else if (key == 254) {
                            key = 97;
                        } else {
                            key = ((key & 240) >> 4) * 12 + (key & 15) + 1;

                            while(key > 96) {
                                key -= 12;
                            }
                        }

                        pattern_data[note_idx] = (byte)key;
                        pattern_data[note_idx + 1] = s3m_file[pattern_offset + 1];
                    }

                    pattern_offset += 2;
                }

                if ((token & 64) == 64) {
                    if (var16 >= 0) {
                        int volume_column = (s3m_file[pattern_offset] & 255) + 16;
                        pattern_data[note_idx + 2] = (byte)volume_column;
                    }

                    ++pattern_offset;
                }

                if ((token & 128) == 128) {
                    if (var16 >= 0) {
                        int effect = s3m_file[pattern_offset] & 255;
                        int effect_param = s3m_file[pattern_offset + 1] & 255;
                        effect = effect_map[effect & 31];
                        if (effect == 255) {
                            effect = 0;
                            effect_param = 0;
                        }

                        if (effect == 14) {
                            effect = effect_s_map[(effect_param & 240) >> 4];
                            effect_param &= 15;
                            switch(effect) {
                                case 8:
                                    effect = 8;
                                    effect_param *= 17;
                                    break;
                                case 9:
                                    effect = 8;
                                    if (effect_param > 7) {
                                        effect_param -= 8;
                                    } else {
                                        effect_param += 8;
                                    }

                                    effect_param *= 17;
                                    break;
                                case 255:
                                    effect = 0;
                                    effect_param = 0;
                                    break;
                                default:
                                    effect_param = (effect & 15) << 4 | effect_param & 15;
                                    effect = 14;
                            }
                        }

                        pattern_data[note_idx + 3] = (byte)effect;
                        pattern_data[note_idx + 4] = (byte)effect_param;
                    }

                    pattern_offset += 2;
                }
            } else {
                ++row_idx;
            }
        }

        Pattern pattern = new Pattern();
        pattern.num_rows = 64;
        pattern.set_pattern_data(pattern_data);
        return pattern;
    }

    private static byte[] read_s3m_file(byte[] header_96_bytes, DataInput data_input) throws IOException {
        if (!is_s3m(header_96_bytes)) {
            throw new IllegalArgumentException("ScreamTracker3: Not an S3M file!");
        } else {
            int s3m_file_length = header_96_bytes.length;
            int num_pattern_orders = get_num_pattern_orders(header_96_bytes);
            int num_instruments = get_num_instruments(header_96_bytes);
            int num_patterns = get_num_patterns(header_96_bytes);
            s3m_file_length += num_pattern_orders;
            s3m_file_length += num_instruments * 2;
            s3m_file_length += num_patterns * 2;
            byte[] s3m_file = read_more(header_96_bytes, s3m_file_length, data_input);

            for(int instrument_idx = 0; instrument_idx < num_instruments; ++instrument_idx) {
                int instrument_offset = get_instrument_offset(s3m_file, instrument_idx);
                instrument_offset += 80;
                if (instrument_offset > s3m_file_length) {
                    s3m_file_length = instrument_offset;
                }
            }

            for(int pattern_idx = 0; pattern_idx < num_patterns; ++pattern_idx) {
                int pattern_offset = get_pattern_offset(s3m_file, pattern_idx);
                pattern_offset += 2;
                if (pattern_offset > s3m_file_length) {
                    s3m_file_length = pattern_offset;
                }
            }

            s3m_file = read_more(s3m_file, s3m_file_length, data_input);

            for(int var15 = 0; var15 < num_instruments; ++var15) {
                int instrument_offset = get_instrument_offset(s3m_file, var15);
                int sample_data_offset = get_sample_data_offset(s3m_file, instrument_offset);
                sample_data_offset += get_sample_data_length(s3m_file, instrument_offset);
                if (sample_data_offset > s3m_file_length) {
                    s3m_file_length = sample_data_offset;
                }
            }

            for(int var16 = 0; var16 < num_patterns; ++var16) {
                int pattern_offset = get_pattern_offset(s3m_file, var16);
                pattern_offset += get_pattern_length(s3m_file, pattern_offset);
                pattern_offset += 2;
                if (pattern_offset > s3m_file_length) {
                    s3m_file_length = pattern_offset;
                }
            }

            return read_more(s3m_file, s3m_file_length, data_input);
        }
    }

    private static int get_num_pattern_orders(byte[] s3m_file) {
        return unsigned_short_le(s3m_file, 32);
    }

    private static int get_num_instruments(byte[] s3m_file) {
        return unsigned_short_le(s3m_file, 34);
    }

    private static int get_num_patterns(byte[] s3m_file) {
        return unsigned_short_le(s3m_file, 36);
    }

    private static int get_instrument_offset(byte[] s3m_file, int instrument_idx) {
        int pointer_offset = 96 + get_num_pattern_orders(s3m_file);
        return unsigned_short_le(s3m_file, pointer_offset + instrument_idx * 2) << 4;
    }

    private static int get_sample_data_offset(byte[] s3m_file, int instrument_offset) {
        int sample_data_offset = 0;
        if (s3m_file[instrument_offset] == 1) {
            sample_data_offset = (s3m_file[instrument_offset + 13] & 255) << 20;
            sample_data_offset |= unsigned_short_le(s3m_file, instrument_offset + 14) << 4;
        }

        return sample_data_offset;
    }

    private static int get_sample_data_length(byte[] s3m_file, int instrument_offset) {
        int sample_data_length = 0;
        if (s3m_file[instrument_offset] == 1) {
            sample_data_length = unsigned_short_le(s3m_file, instrument_offset + 16);
            boolean sixteen_bit = (s3m_file[instrument_offset + 31] & 4) != 0;
            if (sixteen_bit) {
                sample_data_length <<= 1;
            }
        }

        return sample_data_length;
    }

    private static int get_pattern_offset(byte[] s3m_file, int pattern_idx) {
        int pointer_offset = 96 + get_num_pattern_orders(s3m_file);
        pointer_offset += get_num_instruments(s3m_file) * 2;
        return unsigned_short_le(s3m_file, pointer_offset + pattern_idx * 2) << 4;
    }

    private static int get_pattern_length(byte[] s3m_file, int pattern_offset) {
        return unsigned_short_le(s3m_file, pattern_offset);
    }

    private static byte[] read_more(byte[] old_data, int new_length, DataInput data_input) throws IOException {
        byte[] new_data = old_data;
        if (new_length > old_data.length) {
            new_data = new byte[new_length];
            System.arraycopy(old_data, 0, new_data, 0, old_data.length);

            try {
                data_input.readFully(new_data, old_data.length, new_data.length - old_data.length);
            } catch (EOFException var5) {
                System.out.println("ScreamTracker3: Module has been truncated!");
            }
        }

        return new_data;
    }

    private static int unsigned_short_le(byte[] buffer, int offset) {
        int value = buffer[offset] & 255;
        return value | (buffer[offset + 1] & 0xFF) << 8;
    }

    private static String ascii_text(byte[] buffer, int offset, int length) {
        byte[] string_buffer = new byte[length];

        for(int idx = 0; idx < length; ++idx) {
            int chr = buffer[offset + idx];
            if (chr < 32) {
                chr = 32;
            }

            string_buffer[idx] = (byte)chr;
        }

        String string;
        try {
            string = new String(string_buffer, 0, length, "ISO-8859-1");
        } catch (UnsupportedEncodingException var8) {
            string = "";
        }

        return string;
    }
}
