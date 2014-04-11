/*
 * SoundUtility.java 
 *
 * Created on 1 Nov, 2008 
 */

package org.meta.common;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

/**
 * A utility class for sound related APIs.
 * 
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public final class SoundUtility {

    private static final int SOUND_BUFFER_SIZE = 1024;
    
    /** private constructor .. no instance */
    private SoundUtility() {}
    
    /**
     * The default audio format used with voice communication
     * 
     * @return AudioFormat the default audio format
     */
    public static AudioFormat getDefaultAudioFormat() {
        float sampleRate = 8000.0F;
        // 8000,11025,16000,22050,44100
        int sampleSizeInBits = 8;
        // 8,16 .. low quality, less data :)
        int channels = 1;
        // 1,2
        boolean signed = true;
        // true,false
        boolean bigEndian = false;
        // true,false
        
        return new AudioFormat(sampleRate, sampleSizeInBits,
                               channels, signed, bigEndian);
    }
    
    /**
     * Create a capture thread that captures sound from a microphone (or similar
     * input device) and sends it to an output stream.
     * 
     * @param os the OutputStream to which the captured data is to be written
     * @return the instance of CaptureThread
     */
    public static CaptureThread createCaptureThread(OutputStream os) {
        CaptureThread captureThread = new CaptureThread(os);
        
        captureThread.setPriority(Thread.MIN_PRIORITY);
        captureThread.setName("Sound capture Thread");
        captureThread.start();
        
        return captureThread;
    }
    
    /**
     * Creates a sound output thread that reads from a input stream and sends
     * the output to a sound output device.
     * 
     * @param is the InputStream from which audio data is read in
     * @return the instance of SoundOutputThread
     */
    public static SoundOutputThread createSoundOutputThread(InputStream is) {
       SoundOutputThread soundOutputThread = new SoundOutputThread(is);
       
       soundOutputThread.setPriority(Thread.MIN_PRIORITY);
       soundOutputThread.setName("Sound output Thread");
       soundOutputThread.start();
        
       return soundOutputThread;
    }
    
    /**
     * Captures the data from a sound input device and write it to an output
     * stream.
     */
    public static class CaptureThread extends Thread {
        private OutputStream os;        
        private TargetDataLine targetDataLine;
        
        /** Creates instance of CaptureThread */
        public CaptureThread(OutputStream os) {
            this.os = os;
        }
        
        /**
         * Overriden run() method
         */
        @Override
        public void run() {
            try {
                // get everything set up for capture
                AudioFormat audioFormat = getDefaultAudioFormat();
                DataLine.Info dataLineInfo 
                        = new DataLine.Info(TargetDataLine.class, audioFormat);

                if (!AudioSystem.isLineSupported(dataLineInfo)) {
                   errorString = "No microphone found!";
                   inError = true;
                   System.err.println(errorString);
                   return;
                } // end if

                targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
                targetDataLine.open(audioFormat, targetDataLine.getBufferSize());
                targetDataLine.start();    
                
                stopCapture = false;
                byte[] tempBuffer = new byte[SOUND_BUFFER_SIZE];
                while (!stopCapture) {
                    int cnt = targetDataLine.read(tempBuffer, 
                                                  0, tempBuffer.length);
                    if (cnt > 0) os.write(tempBuffer, 0, cnt);                    
                } // end while            
            } catch(Throwable e) {
                System.err.println("An error occured in CaptureThread: " 
                                   + e.toString());
                e.printStackTrace();
                stopCapture = true;
            } finally {
                try {
                    if (targetDataLine != null) {
                        targetDataLine.stop(); targetDataLine.close();
                        targetDataLine = null;
                    } // end if                    
                } catch(Throwable t) {
                    System.err.println("Oops something went bad [CaptureThread]:" 
                                       + t.toString());
                    t.printStackTrace();
                } // end of try .. catch block
                
                stopCapture = true;
            } // end of try .. catch .. finally block
        }
        
        protected String errorString;

        /**
         * Get the value of errorString
         *
         * @return the value of errorString
         */
        public String getErrorString() {
            return errorString;
        }

        /**
         * Set the value of errorString
         *
         * @param errorString new value of errorString
         */
        public void setErrorString(String errorString) {
            this.errorString = errorString;
        }

        protected boolean inError = false;

        /**
         * Get the value of inError
         *
         * @return the value of inError
         */
        public boolean isInError() {
            return inError;
        }

        /**
         * Set the value of inError
         *
         * @param inError new value of inError
         */
        public void setInError(boolean inError) {
            this.inError = inError;
        }
        
        protected boolean stopCapture;

        /**
         * Get the value of stopCapture
         *
         * @return the value of stopCapture
         */
        public boolean isStopCapture() {
            return stopCapture;
        }

        /**
         * Set the value of stopCapture
         *
         * @param stopCapture new value of stopCapture
         */
        public void setStopCapture(boolean stopCapture) {
            this.stopCapture = stopCapture;
        }
    }
    
    /**
     * Reads from an input stream and directes the sound to a sound output
     * device.
     */
    public static class SoundOutputThread extends Thread {
        private InputStream is;
        private SourceDataLine line;
        
        /** Creates a new instance of SoundOutputThread */
        public SoundOutputThread(InputStream is) {
            this.is = is;
        }
        
        /**
         * Overridden run() method
         */
        @Override
        public void run() {
            try {
                BufferedInputStream ain = new BufferedInputStream(is);

                AudioFormat format = getDefaultAudioFormat();
                
                DataLine.Info info 
                        = new DataLine.Info(SourceDataLine.class, format);
                
                line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(format);

                int framesize = format.getFrameSize();
                byte[] buffer = new byte[4 * SOUND_BUFFER_SIZE * framesize];

                int numbytes = 0;
                boolean started = false;
                
                stopSoundOutput = false;
                while (!stopSoundOutput) {
                    int bytesread = ain.read(buffer, numbytes, 
                                             buffer.length - numbytes);
                    if (bytesread == -1) break;
                    numbytes += bytesread;

                    if (!started) {
                        line.start();
                        started = true;
                    } // end if

                    int bytestowrite = (numbytes / framesize) * framesize;
                    line.write(buffer, 0, bytestowrite);

                    int remaining = numbytes - bytestowrite;
                    if (remaining > 0)
                        System.arraycopy(buffer, bytestowrite, 
                                         buffer, 0, remaining);
                    
                    numbytes = remaining;
                } // end while

                line.drain();                
            } catch (Throwable e) {
                System.err.println("Error in SoundOutputThread: " 
                                   + e.toString());
                e.printStackTrace();
            } finally {
                try {
                    if (line != null) { 
                        line.stop();
                        line.close();
                    } // end if
                } catch(Throwable t) {
                    System.err.println("Oops something went bad [SoundOutputThread]:" 
                                       + t.toString());
                    t.printStackTrace();
                } // end of try .. catch block
                
                stopSoundOutput = true;
            } // end of try .. catch .. finally block
        }
        
        protected String errorString;

        /**
         * Get the value of errorString
         *
         * @return the value of errorString
         */
        public String getErrorString() {
            return errorString;
        }

        /**
         * Set the value of errorString
         *
         * @param errorString new value of errorString
         */
        public void setErrorString(String errorString) {
            this.errorString = errorString;
        }

        protected boolean inError = false;

        /**
         * Get the value of inError
         *
         * @return the value of inError
         */
        public boolean isInError() {
            return inError;
        }

        /**
         * Set the value of inError
         *
         * @param inError new value of inError
         */
        public void setInError(boolean inError) {
            this.inError = inError;
        }
        
        protected boolean stopSoundOutput;

        /**
         * Get the value of stopSoundOutput
         *
         * @return the value of stopSoundOutput
         */
        public boolean isStopSoundOutput() {
            return stopSoundOutput;
        }

        /**
         * Set the value of stopSoundOutput
         *
         * @param stopSoundOutput new value of stopSoundOutput
         */
        public void setStopSoundOutput(boolean stopSoundOutput) {
            this.stopSoundOutput = stopSoundOutput;
        }
    }
}
