package com.xxx.zzz.aall.ioppp.socketlll.threadnnn;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;



public class EventThreadz extends Thread {

    private static final Logger logger = Logger.getLogger(EventThreadz.class.getName());

    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
        @Override
        public Thread newThread(Runnable runnable) {
            thread = new EventThreadz(runnable);
            thread.setName("EventThread");
            thread.setDaemon(Thread.currentThread().isDaemon());
            return thread;
        }
    };

    private static EventThreadz thread;

    private static ExecutorService service;

    private static int counter = 0;


    private EventThreadz(Runnable runnable) {
        super(runnable);
    }


    public static boolean isCurrent() {
        return currentThread() == thread;
    }


    public static void exec(Runnable task) {
        if (isCurrent()) {
            task.run();
        } else {
            nextTick(task);
        }
    }


    public static void nextTick(final Runnable task) {
        ExecutorService executor;
        synchronized (EventThreadz.class) {
          counter++;
          if (service == null) {
              service = Executors.newSingleThreadExecutor(THREAD_FACTORY);
          }
          executor = service;
        }

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    task.run();
                } catch (Throwable t) {
                    logger.log(Level.SEVERE, "Task threw exception", t);
                    throw t;
                } finally {
                    synchronized (EventThreadz.class) {
                        counter--;
                        if (counter == 0) {
                            service.shutdown();
                            service = null;
                            thread = null;
                        }
                    }
                }
            }
        });
    }
}
