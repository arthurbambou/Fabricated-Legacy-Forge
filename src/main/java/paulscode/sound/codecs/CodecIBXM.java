package paulscode.sound.codecs;

import ibxm.FastTracker2;
import ibxm.IBXM;
import ibxm.Module;
import ibxm.ProTracker;
import ibxm.ScreamTracker3;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import javax.sound.sampled.AudioFormat;
import paulscode.sound.ICodec;
import paulscode.sound.SoundBuffer;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemLogger;

public class CodecIBXM implements ICodec {
    private static final boolean GET = false;
    private static final boolean SET = true;
    private static final boolean XXX = false;
    private boolean endOfStream = false;
    private boolean initialized = false;
    private AudioFormat myAudioFormat = null;
    private boolean reverseBytes = false;
    private IBXM ibxm;
    private Module module;
    private int songDuration;
    private int playPosition;
    private SoundSystemLogger logger = SoundSystemConfig.getLogger();

    public CodecIBXM() {
    }

    public void reverseByteOrder(boolean b) {
        this.reverseBytes = b;
    }

    public boolean initialize(URL url) {
        this.initialized(true, false);
        this.cleanup();
        if (url == null) {
            this.errorMessage("url null in method 'initialize'");
            this.cleanup();
            return false;
        } else {
            InputStream is = null;

            try {
                is = url.openStream();
            } catch (IOException var8) {
                this.errorMessage("Unable to open stream in method 'initialize'");
                this.printStackTrace(var8);
                return false;
            }

            if (this.ibxm == null) {
                this.ibxm = new IBXM(48000);
            }

            if (this.myAudioFormat == null) {
                this.myAudioFormat = new AudioFormat(48000.0F, 16, 2, true, true);
            }

            try {
                this.setModule(loadModule(is));
            } catch (IllegalArgumentException var9) {
                this.errorMessage("Illegal argument in method 'initialize'");
                this.printStackTrace(var9);
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException var6) {
                    }
                }

                return false;
            } catch (IOException var10) {
                this.errorMessage("Error loading module in method 'initialize'");
                this.printStackTrace(var10);
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException var5) {
                    }
                }

                return false;
            }

            if (is != null) {
                try {
                    is.close();
                } catch (IOException var7) {
                }
            }

            this.endOfStream(true, false);
            this.initialized(true, true);
            return true;
        }
    }

    public boolean initialized() {
        return this.initialized(false, false);
    }

    public SoundBuffer read() {
        if (this.endOfStream(false, false)) {
            return null;
        } else if (this.module == null) {
            this.errorMessage("Module null in method 'read'");
            return null;
        } else if (this.myAudioFormat == null) {
            this.errorMessage("Audio Format null in method 'read'");
            return null;
        } else {
            int bufferFrameSize = SoundSystemConfig.getStreamingBufferSize() / 4;
            int frames = this.songDuration - this.playPosition;
            if (frames > bufferFrameSize) {
                frames = bufferFrameSize;
            }

            if (frames <= 0) {
                this.endOfStream(true, true);
                return null;
            } else {
                byte[] outputBuffer = new byte[frames * 4];
                this.ibxm.get_audio(outputBuffer, frames);
                this.playPosition += frames;
                if (this.playPosition >= this.songDuration) {
                    this.endOfStream(true, true);
                }

                if (this.reverseBytes) {
                    reverseBytes(outputBuffer, 0, frames * 4);
                }

                return new SoundBuffer(outputBuffer, this.myAudioFormat);
            }
        }
    }

    public SoundBuffer readAll() {
        if (this.module == null) {
            this.errorMessage("Module null in method 'readAll'");
            return null;
        } else if (this.myAudioFormat == null) {
            this.errorMessage("Audio Format null in method 'readAll'");
            return null;
        } else {
            int bufferFrameSize = SoundSystemConfig.getFileChunkSize() / 4;
            byte[] outputBuffer = new byte[bufferFrameSize * 4];
            byte[] fullBuffer = null;
            int totalBytes = 0;

            while(!this.endOfStream(false, false) && totalBytes < SoundSystemConfig.getMaxFileSize()) {
                int frames = this.songDuration - this.playPosition;
                if (frames > bufferFrameSize) {
                    frames = bufferFrameSize;
                }

                this.ibxm.get_audio(outputBuffer, frames);
                totalBytes += frames * 4;
                fullBuffer = appendByteArrays(fullBuffer, outputBuffer, frames * 4);
                this.playPosition += frames;
                if (this.playPosition >= this.songDuration) {
                    this.endOfStream(true, true);
                }
            }

            if (this.reverseBytes) {
                reverseBytes(fullBuffer, 0, totalBytes);
            }

            return new SoundBuffer(fullBuffer, this.myAudioFormat);
        }
    }

    public boolean endOfStream() {
        return this.endOfStream(false, false);
    }

    public void cleanup() {
        this.playPosition = 0;
    }

    public AudioFormat getAudioFormat() {
        return this.myAudioFormat;
    }

    private static Module loadModule(InputStream input) throws IllegalArgumentException, IOException {
        DataInputStream data_input_stream = new DataInputStream(input);
        byte[] xm_header = new byte[60];
        data_input_stream.readFully(xm_header);
        if (FastTracker2.is_xm(xm_header)) {
            return FastTracker2.load_xm(xm_header, data_input_stream);
        } else {
            byte[] s3m_header = new byte[96];
            System.arraycopy(xm_header, 0, s3m_header, 0, 60);
            data_input_stream.readFully(s3m_header, 60, 36);
            if (ScreamTracker3.is_s3m(s3m_header)) {
                return ScreamTracker3.load_s3m(s3m_header, data_input_stream);
            } else {
                byte[] mod_header = new byte[1084];
                System.arraycopy(s3m_header, 0, mod_header, 0, 96);
                data_input_stream.readFully(mod_header, 96, 988);
                return ProTracker.load_mod(mod_header, data_input_stream);
            }
        }
    }

    private void setModule(Module m) {
        if (m != null) {
            this.module = m;
        }

        this.ibxm.set_module(this.module);
        this.songDuration = this.ibxm.calculate_song_duration();
    }

    private synchronized boolean initialized(boolean action, boolean value) {
        if (action) {
            this.initialized = value;
        }

        return this.initialized;
    }

    private synchronized boolean endOfStream(boolean action, boolean value) {
        if (action) {
            this.endOfStream = value;
        }

        return this.endOfStream;
    }

    private static byte[] trimArray(byte[] array, int maxLength) {
        byte[] trimmedArray = null;
        if (array != null && array.length > maxLength) {
            trimmedArray = new byte[maxLength];
            System.arraycopy(array, 0, trimmedArray, 0, maxLength);
        }

        return trimmedArray;
    }

    public static void reverseBytes(byte[] buffer) {
        reverseBytes(buffer, 0, buffer.length);
    }

    public static void reverseBytes(byte[] buffer, int offset, int size) {
        for(int i = offset; i < offset + size; i += 2) {
            byte b = buffer[i];
            buffer[i] = buffer[i + 1];
            buffer[i + 1] = b;
        }

    }

    private static byte[] convertAudioBytes(byte[] audio_bytes, boolean two_bytes_data) {
        ByteBuffer dest = ByteBuffer.allocateDirect(audio_bytes.length);
        dest.order(ByteOrder.nativeOrder());
        ByteBuffer src = ByteBuffer.wrap(audio_bytes);
        src.order(ByteOrder.LITTLE_ENDIAN);
        if (two_bytes_data) {
            ShortBuffer dest_short = dest.asShortBuffer();
            ShortBuffer src_short = src.asShortBuffer();

            while(src_short.hasRemaining()) {
                dest_short.put(src_short.get());
            }
        } else {
            while(src.hasRemaining()) {
                dest.put(src.get());
            }
        }

        dest.rewind();
        if (!dest.hasArray()) {
            byte[] arrayBackedBuffer = new byte[dest.capacity()];
            dest.get(arrayBackedBuffer);
            dest.clear();
            return arrayBackedBuffer;
        } else {
            return dest.array();
        }
    }

    private static byte[] appendByteArrays(byte[] arrayOne, byte[] arrayTwo, int length) {
        if (arrayOne == null && arrayTwo == null) {
            return null;
        } else {
            byte[] newArray;
            if (arrayOne == null) {
                newArray = new byte[length];
                System.arraycopy(arrayTwo, 0, newArray, 0, length);
                byte[] var6 = null;
            } else if (arrayTwo == null) {
                newArray = new byte[arrayOne.length];
                System.arraycopy(arrayOne, 0, newArray, 0, arrayOne.length);
                byte[] var4 = null;
            } else {
                newArray = new byte[arrayOne.length + length];
                System.arraycopy(arrayOne, 0, newArray, 0, arrayOne.length);
                System.arraycopy(arrayTwo, 0, newArray, arrayOne.length, length);
                byte[] var5 = null;
                byte[] var7 = null;
            }

            return newArray;
        }
    }

    private void errorMessage(String message) {
        this.logger.errorMessage("CodecWav", message, 0);
    }

    private void printStackTrace(Exception e) {
        this.logger.printStackTrace(e, 1);
    }
}
