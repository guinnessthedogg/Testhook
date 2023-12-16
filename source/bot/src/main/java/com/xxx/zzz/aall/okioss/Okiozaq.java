
package com.xxx.zzz.aall.okioss;


import com.xxx.zzz.aall.orgsss.animal_sniffer.IgnoreJRERequirement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.xxx.zzz.aall.javaxlll.annotationlll.Nullableq;


public final class Okiozaq {
  static final Logger logger = Logger.getLogger(Okiozaq.class.getName());

  private Okiozaq() {
  }

  
  public static BufferedSourcezaqdfs buffer(Sourcezaq source) {
    return new RealBufferedSourcezaq(source);
  }

  
  public static BufferedSinkzaqds buffer(Sinkzaq sink) {
    return new RealBufferedSinkzaqfd(sink);
  }

  
  public static Sinkzaq sink(OutputStream out) {
    return sink(out, new Timeoutzaq());
  }

  private static Sinkzaq sink(final OutputStream out, final Timeoutzaq timeout) {
    if (out == null) throw new IllegalArgumentException("out == null");
    if (timeout == null) throw new IllegalArgumentException("timeout == null");

    return new Sinkzaq() {
      @Override public void write(Bufferzaq source, long byteCount) throws IOException {
        Utilzaqq.checkOffsetAndCount(source.size, 0, byteCount);
        while (byteCount > 0) {
          timeout.throwIfReached();
          Segmentzaq head = source.head;
          int toCopy = (int) Math.min(byteCount, head.limit - head.pos);
          out.write(head.data, head.pos, toCopy);

          head.pos += toCopy;
          byteCount -= toCopy;
          source.size -= toCopy;

          if (head.pos == head.limit) {
            source.head = head.pop();
            SegmentPoolzaq.recycle(head);
          }
        }
      }

      @Override public void flush() throws IOException {
        out.flush();
      }

      @Override public void close() throws IOException {
        out.close();
      }

      @Override public Timeoutzaq timeout() {
        return timeout;
      }

      @Override public String toString() {
        return "sink(" + out + ")";
      }
    };
  }

  
  public static Sinkzaq sink(Socket socket) throws IOException {
    if (socket == null) throw new IllegalArgumentException("socket == null");
    AsyncTimeoutzaq timeout = timeout(socket);
    Sinkzaq sink = sink(socket.getOutputStream(), timeout);
    return timeout.sink(sink);
  }

  
  public static Sourcezaq source(InputStream in) {
    return source(in, new Timeoutzaq());
  }

  private static Sourcezaq source(final InputStream in, final Timeoutzaq timeout) {
    if (in == null) throw new IllegalArgumentException("in == null");
    if (timeout == null) throw new IllegalArgumentException("timeout == null");

    return new Sourcezaq() {
      @Override public long read(Bufferzaq sink, long byteCount) throws IOException {
        if (byteCount < 0) throw new IllegalArgumentException("byteCount < 0: " + byteCount);
        if (byteCount == 0) return 0;
        try {
          timeout.throwIfReached();
          Segmentzaq tail = sink.writableSegment(1);
          int maxToCopy = (int) Math.min(byteCount, Segmentzaq.SIZE - tail.limit);
          int bytesRead = in.read(tail.data, tail.limit, maxToCopy);
          if (bytesRead == -1) return -1;
          tail.limit += bytesRead;
          sink.size += bytesRead;
          return bytesRead;
        } catch (AssertionError e) {
          if (isAndroidGetsocknameError(e)) throw new IOException(e);
          throw e;
        }
      }

      @Override public void close() throws IOException {
        in.close();
      }

      @Override public Timeoutzaq timeout() {
        return timeout;
      }

      @Override public String toString() {
        return "source(" + in + ")";
      }
    };
  }

  
  public static Sourcezaq source(File file) throws FileNotFoundException {
    if (file == null) throw new IllegalArgumentException("file == null");
    return source(new FileInputStream(file));
  }

  
  @IgnoreJRERequirement
  public static Sourcezaq source(Path path, OpenOption... options) throws IOException {
    if (path == null) throw new IllegalArgumentException("path == null");
    return source(Files.newInputStream(path, options));
  }

  
  public static Sinkzaq sink(File file) throws FileNotFoundException {
    if (file == null) throw new IllegalArgumentException("file == null");
    return sink(new FileOutputStream(file));
  }

  
  public static Sinkzaq appendingSink(File file) throws FileNotFoundException {
    if (file == null) throw new IllegalArgumentException("file == null");
    return sink(new FileOutputStream(file, true));
  }

  
  @IgnoreJRERequirement
  public static Sinkzaq sink(Path path, OpenOption... options) throws IOException {
    if (path == null) throw new IllegalArgumentException("path == null");
    return sink(Files.newOutputStream(path, options));
  }

  
  public static Sinkzaq blackhole() {
    return new Sinkzaq() {
      @Override public void write(Bufferzaq source, long byteCount) throws IOException {
        source.skip(byteCount);
      }

      @Override public void flush() throws IOException {
      }

      @Override public Timeoutzaq timeout() {
        return Timeoutzaq.NONE;
      }

      @Override public void close() throws IOException {
      }
    };
  }

  
  public static Sourcezaq source(Socket socket) throws IOException {
    if (socket == null) throw new IllegalArgumentException("socket == null");
    AsyncTimeoutzaq timeout = timeout(socket);
    Sourcezaq source = source(socket.getInputStream(), timeout);
    return timeout.source(source);
  }

  private static AsyncTimeoutzaq timeout(final Socket socket) {
    return new AsyncTimeoutzaq() {
      @Override protected IOException newTimeoutException(@Nullableq IOException cause) {
        InterruptedIOException ioe = new SocketTimeoutException("timeout");
        if (cause != null) {
          ioe.initCause(cause);
        }
        return ioe;
      }

      @Override protected void timedOut() {
        try {
          socket.close();
        } catch (Exception e) {
          logger.log(Level.WARNING, "Failed to close timed out socket " + socket, e);
        } catch (AssertionError e) {
          if (isAndroidGetsocknameError(e)) {


            logger.log(Level.WARNING, "Failed to close timed out socket " + socket, e);
          } else {
            throw e;
          }
        }
      }
    };
  }

  
  static boolean isAndroidGetsocknameError(AssertionError e) {
    return e.getCause() != null && e.getMessage() != null
        && e.getMessage().contains("getsockname failed");
  }
}
