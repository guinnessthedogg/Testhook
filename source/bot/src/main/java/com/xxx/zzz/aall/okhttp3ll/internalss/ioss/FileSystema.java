
package com.xxx.zzz.aall.okhttp3ll.internalss.ioss;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.xxx.zzz.aall.okioss.Okiozaq;
import com.xxx.zzz.aall.okioss.Sinkzaq;
import com.xxx.zzz.aall.okioss.Sourcezaq;


public interface FileSystema {
  
  FileSystema SYSTEM = new FileSystema() {
    @Override public Sourcezaq source(File file) throws FileNotFoundException {
      return Okiozaq.source(file);
    }

    @Override public Sinkzaq sink(File file) throws FileNotFoundException {
      try {
        return Okiozaq.sink(file);
      } catch (FileNotFoundException e) {

        file.getParentFile().mkdirs();
        return Okiozaq.sink(file);
      }
    }

    @Override public Sinkzaq appendingSink(File file) throws FileNotFoundException {
      try {
        return Okiozaq.appendingSink(file);
      } catch (FileNotFoundException e) {

        file.getParentFile().mkdirs();
        return Okiozaq.appendingSink(file);
      }
    }

    @Override public void delete(File file) throws IOException {

      if (!file.delete() && file.exists()) {
        throw new IOException("failed to delete " + file);
      }
    }

    @Override public boolean exists(File file) {
      return file.exists();
    }

    @Override public long size(File file) {
      return file.length();
    }

    @Override public void rename(File from, File to) throws IOException {
      delete(to);
      if (!from.renameTo(to)) {
        throw new IOException("failed to rename " + from + " to " + to);
      }
    }

    @Override public void deleteContents(File directory) throws IOException {
      File[] files = directory.listFiles();
      if (files == null) {
        throw new IOException("not a readable directory: " + directory);
      }
      for (File file : files) {
        if (file.isDirectory()) {
          deleteContents(file);
        }
        if (!file.delete()) {
          throw new IOException("failed to delete " + file);
        }
      }
    }
  };

  
  Sourcezaq source(File file) throws FileNotFoundException;

  
  Sinkzaq sink(File file) throws FileNotFoundException;

  
  Sinkzaq appendingSink(File file) throws FileNotFoundException;

  
  void delete(File file) throws IOException;

  
  boolean exists(File file);

  
  long size(File file);

  
  void rename(File from, File to) throws IOException;

  
  void deleteContents(File directory) throws IOException;
}
