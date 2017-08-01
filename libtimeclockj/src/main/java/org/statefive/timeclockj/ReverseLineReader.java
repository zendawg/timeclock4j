/*
 * Copyright (C) 2011 State Five
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.statefive.timeclockj;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Originally taken from http://stackoverflow.com/questions/6011345/read-a-file-line-by-line-in-reverse-order
 * 
 * Utility class for small hand-held devices that cannot wait for the file
 * to be parsed and need to access the latest clock line <i>whilst</i>
 * the file is being parsed.
 * 
 * @author WhiteFang34 (StackOverflow)
 */
public class ReverseLineReader {

  private static final int BUFFER_SIZE = 8192;
  private final FileChannel channel;
  private long filePos;
  private ByteBuffer buf;
  private int bufPos;
  private byte lastLineBreak = '\n';
  private ByteArrayOutputStream baos = new ByteArrayOutputStream();

  /**
   * 
   * @param file
   * @throws IOException 
   */
  public ReverseLineReader(File file) throws IOException {
    RandomAccessFile raf = new RandomAccessFile(file, "r");
    channel = raf.getChannel();
    filePos = raf.length();
  }

  /**
   * 
   * @return
   * @throws IOException 
   */
  public String readLine() throws IOException {
    while (true) {
      if (bufPos < 0) {
        if (filePos == 0) {
          if (baos == null) {
            return null;
          }
          String line = bufToString();
          baos = null;
          return line;
        }

        long start = Math.max(filePos - BUFFER_SIZE, 0);
        long end = filePos;
        long len = end - start;

        buf = channel.map(FileChannel.MapMode.READ_ONLY, start, len);
        bufPos = (int) len;
        filePos = start;
      }

      while (bufPos-- > 0) {
        byte c = buf.get(bufPos);
        if (c == '\r' || c == '\n') {
          if (c != lastLineBreak) {
            lastLineBreak = c;
            continue;
          }
          lastLineBreak = c;
          return bufToString();
        }
        baos.write(c);
      }
    }
  }

  /**
   * 
   * @return
   * @throws UnsupportedEncodingException 
   */
  private String bufToString() throws UnsupportedEncodingException {
    if (baos.size() == 0) {
      return "";
    }

    byte[] bytes = baos.toByteArray();
    for (int i = 0; i < bytes.length / 2; i++) {
      byte t = bytes[i];
      bytes[i] = bytes[bytes.length - i - 1];
      bytes[bytes.length - i - 1] = t;
    }

    baos.reset();

    return new String(bytes);
  }
}