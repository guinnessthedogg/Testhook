
package com.xxx.zzz.aall.okhttp3ll.internalss;


public abstract class NamedRunnableq implements Runnable {
  protected final String name;

  public NamedRunnableq(String format, Object... args) {
    this.name = Utilaq.format(format, args);
  }

  @Override public final void run() {
    String oldName = Thread.currentThread().getName();
    Thread.currentThread().setName(name);
    try {
      execute();
    } finally {
      Thread.currentThread().setName(oldName);
    }
  }

  protected abstract void execute();
}
