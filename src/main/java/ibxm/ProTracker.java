package ibxm;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class ProTracker {
    public ProTracker() {
    }

    public static boolean is_mod(byte[] header_1084_bytes) {
        boolean is_mod = false;
        if (calculate_num_channels(header_1084_bytes) > 0) {
            is_mod = true;
        }

        return is_mod;
    }

    public static Module load_mod(byte[] header_1084_bytes, DataInput data_input) throws IOException {
        int num_channels = calculate_num_channels(header_1084_bytes);
        if (num_channels < 1) {
            throw new IllegalArgumentException("ProTracker: Unrecognised module format!");
        } else {
            Module module = new Module();
            module.song_title = ascii_text(header_1084_bytes, 0, 20);
            module.pal = num_channels == 4;
            module.global_volume = 64;
            module.channel_gain = 12288;
            module.default_speed = 6;
            module.default_tempo = 125;
            module.set_num_channels(num_channels);

            for(int channel_idx = 0; channel_idx < num_channels; ++channel_idx) {
                int panning = 64;
                if ((channel_idx & 3) == 1 || (channel_idx & 3) == 2) {
                    panning = 192;
                }

                module.set_initial_panning(channel_idx, panning);
            }

            int sequence_length = header_1084_bytes[950] & 127;
            int restart_idx = header_1084_bytes[951] & 127;
            if (restart_idx >= sequence_length) {
                restart_idx = 0;
            }

            module.restart_sequence_index = restart_idx;
            module.set_sequence_length(sequence_length);

            for(int sequence_idx = 0; sequence_idx < sequence_length; ++sequence_idx) {
                module.set_sequence(sequence_idx, header_1084_bytes[952 + sequence_idx] & 127);
            }

            int num_patterns = calculate_num_patterns(header_1084_bytes);
            module.set_num_patterns(num_patterns);

            for(int pattern_idx = 0; pattern_idx < num_patterns; ++pattern_idx) {
                module.set_pattern(pattern_idx, read_mod_pattern(data_input, num_channels));
            }

            module.set_num_instruments(31);

            for(int instrument_idx = 1; instrument_idx <= 31; ++instrument_idx) {
                module.set_instrument(instrument_idx, read_mod_instrument(header_1084_bytes, instrument_idx, data_input));
            }

            return module;
        }
    }

    private static int calculate_num_patterns(byte[] module_header) {
        int num_patterns = 0;

        for(int pattern_idx = 0; pattern_idx < 128; ++pattern_idx) {
            int order_entry = module_header[952 + pattern_idx] & 127;
            if (order_entry >= num_patterns) {
                num_patterns = order_entry + 1;
            }
        }

        return num_patterns;
    }

    private static int calculate_num_channels(byte[] module_header) {
        int num_channels;
        switch(module_header[1082] << 8 | module_header[1083]) {
            case 17224:
                num_channels = (module_header[1080] - 48) * 10 + (module_header[1081] - 48);
                break;
            case 18510:
                num_channels = module_header[1080] - 48;
                break;
            case 19233:
            case 19246:
            case 21550:
            case 21556:
                num_channels = 4;
                break;
            default:
                num_channels = 0;
        }

        return num_channels;
    }

    private static Pattern read_mod_pattern(DataInput data_input, int num_channels) throws IOException {
        Pattern pattern = new Pattern();
        pattern.num_rows = 64;
        byte[] input_pattern_data = new byte[64 * num_channels * 4];
        byte[] output_pattern_data = new byte[64 * num_channels * 5];
        data_input.readFully(input_pattern_data);
        int input_idx = 0;

        for(int output_idx = 0; input_idx < input_pattern_data.length; output_idx += 5) {
            int period = (input_pattern_data[input_idx] & 15) << 8;
            period |= input_pattern_data[input_idx + 1] & 255;
            output_pattern_data[output_idx] = to_key(period);
            int instrument = input_pattern_data[input_idx] & 16;
            instrument |= (input_pattern_data[input_idx + 2] & 240) >> 4;
            output_pattern_data[output_idx + 1] = (byte)instrument;
            int effect = input_pattern_data[input_idx + 2] & 15;
            int effect_param = input_pattern_data[input_idx + 3] & 255;
            if (effect == 1 && effect_param == 0) {
                effect = 0;
            }

            if (effect == 2 && effect_param == 0) {
                effect = 0;
            }

            if (effect == 8 && num_channels == 4) {
                effect = 0;
                effect_param = 0;
            }

            if (effect == 10 && effect_param == 0) {
                effect = 0;
            }

            if (effect == 5 && effect_param == 0) {
                effect = 3;
            }

            if (effect == 6 && effect_param == 0) {
                effect = 4;
            }

            output_pattern_data[output_idx + 3] = (byte)effect;
            output_pattern_data[output_idx + 4] = (byte)effect_param;
            input_idx += 4;
        }

        pattern.set_pattern_data(output_pattern_data);
        return pattern;
    }

    private static Instrument read_mod_instrument(byte[] mod_header, int idx, DataInput data_input) throws IOException {
        int header_offset = (idx - 1) * 30 + 20;
        Instrument instrument = new Instrument();
        instrument.name = ascii_text(mod_header, header_offset, 22);
        Sample sample = new Sample();
        int sample_data_length = unsigned_short_be(mod_header, header_offset + 22) << 1;
        int fine_tune = mod_header[header_offset + 24] & 15;
        if (fine_tune > 7) {
            fine_tune -= 16;
        }

        sample.transpose = (fine_tune << 15) / 96;
        sample.volume = mod_header[header_offset + 25] & 127;
        int loop_start = unsigned_short_be(mod_header, header_offset + 26) << 1;
        int loop_length = unsigned_short_be(mod_header, header_offset + 28) << 1;
        if (loop_length < 4) {
            loop_length = 0;
        }

        byte[] raw_sample_data = new byte[sample_data_length];
        short[] sample_data = new short[sample_data_length];

        try {
            data_input.readFully(raw_sample_data);
        } catch (EOFException var14) {
            System.out.println("ProTracker: Instrument " + idx + " has samples missing.");
        }

        for(int sample_idx = 0; sample_idx < raw_sample_data.length; ++sample_idx) {
            sample_data[sample_idx] = (short)(raw_sample_data[sample_idx] << 8);
        }

        sample.set_sample_data(sample_data, loop_start, loop_length, false);
        instrument.set_num_samples(1);
        instrument.set_sample(0, sample);
        return instrument;
    }

    private static byte to_key(int period) {
        int key;
        if (period < 32) {
            key = 0;
        } else {
            int oct = LogTable.log_2(7256) - LogTable.log_2(period);
            if (oct < 0) {
                key = 0;
            } else {
                key = oct * 12;
                key >>= 14;
                key = (key >> 1) + (key & 1);
            }
        }

        return (byte)key;
    }

    private static int unsigned_short_be(byte[] buf, int offset) {
        int value = (buf[offset] & 255) << 8;
        return value | buf[offset + 1] & 0xFF;
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
