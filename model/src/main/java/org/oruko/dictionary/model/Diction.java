package org.oruko.dictionary.model;

//import sun.misc.IOUtils;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Represents the text to speech rendition of {@link org.oruko.dictionary.model.NameEntry}
 * Created by dadepo on 2/11/15.
 */
@Entity
public class Diction {

    @Id
    private String name;

    @Column
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] audioStream;

    /**
     * Public constructor for {@link Diction}
     */
    public Diction() {
    }

    /**
     * Public constructor
     * @param name the name
     * @param audioStream the {@link AudioInputStream}
     * @throws IOException if cannot transform the audio stream into byte array
     */
    public Diction(String name, AudioInputStream audioStream) throws IOException {
        this.name = name;
        this.audioStream = readFully(audioStream, -1, true);
    }

    /**
     * Returns the name
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the Audio represented as {@link AudioInputStream}
     * @return the audio for the diction
     */
    public AudioInputStream getAudioStream() {
        AudioFormat format = new AudioFormat(44100.0f,16,1,true,false);
        InputStream inputstream = new ByteArrayInputStream(audioStream);
        return new AudioInputStream(inputstream, format, audioStream.length);
    }

    /**
     * Returns the tonal mark as a string. i.e d-r-m
     * @return the tonal mark as a string
     */
    public String toString() {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    /**
            * Read up to {@code length} of bytes from {@code in}
     * until EOF is detected.
            * @param is input stream, must not be null
            * @param length number of bytes to read, -1 or Integer.MAX_VALUE means
     *        read as much as possible
     * @param readAll if true, an EOFException will be thrown if not enough
     *        bytes are read. Ignored when length is -1 or Integer.MAX_VALUE
     * @return bytes read
     * @throws IOException Any IO error or a premature EOF is detected
     */
    public static byte[] readFully(InputStream is, int length, boolean readAll)
            throws IOException {
        byte[] output = {};
        if (length == -1) length = Integer.MAX_VALUE;
        int pos = 0;
        while (pos < length) {
            int bytesToRead;
            if (pos >= output.length) { // Only expand when there's no room
                bytesToRead = Math.min(length - pos, output.length + 1024);
                if (output.length < pos + bytesToRead) {
                    output = Arrays.copyOf(output, pos + bytesToRead);
                }
            } else {
                bytesToRead = output.length - pos;
            }
            int cc = is.read(output, pos, bytesToRead);
            if (cc < 0) {
                if (readAll && length != Integer.MAX_VALUE) {
                    throw new EOFException("Detect premature EOF");
                } else {
                    if (output.length != pos) {
                        output = Arrays.copyOf(output, pos);
                    }
                    break;
                }
            }
            pos += cc;
        }
        return output;
    }

}
