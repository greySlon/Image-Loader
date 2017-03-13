package com.abinail.model;

import com.odessa_flat.interfaces.Listener;
import com.odessa_flat.model.HtmlLoader;
import com.odessa_flat.model.ImgExtractor;
import com.odessa_flat.model.ImgLoader;
import com.odessa_flat.model.TextAreaParser;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Sergii on 02.03.2017.
 */
public class MainModule {
    //events
    private Listener<Integer> linkFoundListener;
    private Listener linkProcessedListener;
    private Listener<Long> imgLoadedSizeListener;
    private Listener<Integer> imgFoundListener;
    private Listener imgProcessedListener;
    private Listener taskEndListener;

    private class Handler {
        private AtomicInteger linkFound = new AtomicInteger(0);
        private AtomicInteger linkProcessed = new AtomicInteger(0);
        private AtomicBoolean allRead = new AtomicBoolean(false);

        private AtomicBoolean linkProcessingStoped = new AtomicBoolean(false);
        private AtomicBoolean emptySource = new AtomicBoolean(false);
        private AtomicInteger imgFound = new AtomicInteger(0);

        private AtomicInteger imgProcessed = new AtomicInteger(0);

        public final Listener linkFoundHandler = (sender, arg) -> {
            linkFound.incrementAndGet();
            checkListFinish();
        };

        public final Listener linkProcessedHandler = (sender, arg) -> {
            linkProcessed.incrementAndGet();
            checkListFinish();
        };
        public final Listener readingFinishedHandler = (sender, arg) -> {
            allRead.set(true);
            checkListFinish();
        };

        //images
        public final Listener<Integer> imgFoundHandler = (sender, count) -> {
            imgFound.getAndAdd(count);
            checkImgFinish();
        };

        public final Listener imgProcessedHandler = (sender, arg) -> {
            imgProcessed.incrementAndGet();
            checkImgFinish();
        };

        public final Listener<Long> ImgLoadedHandler = (sender, size) -> {
            checkImgFinish();
        };
        private Object locker = new Object();
        private Object lockerChkImg = new Object();
        private Object lockerChkLnk = new Object();

        public final Listener emptySourceHandle = (sender, size) -> {
            synchronized (locker) {
                if (linkProcessingStoped.get())
                    emptySource.set(true);
            }
        };

        private void checkImgFinish() {
            synchronized (lockerChkImg) {
                if (linkProcessingStoped.get() && emptySource.get() && imgFound.get() == imgProcessed.get()) {
                    MainModule.this.stopSecond();
                }
            }
        }
        private void checkListFinish() {
            synchronized (lockerChkLnk) {
                if (allRead.get() && linkFound.get() == linkProcessed.get()) {
                    MainModule.this.stopFirst();
                    linkProcessingStoped.set(true);
                }
            }
        }

    }

    private Handler handler;

    public static class ModuleBuilder {
        private Listener<Integer> linkFoundListener;
        private Listener linkProcessedListener;
        private Listener<Long> imgLoadedSizeListener;
        private Listener<Integer> imgFoundListener;
        private Listener imgProcessedListener;

        private Listener taskEndListener;

        public ModuleBuilder setLinkFoundListener(Listener<Integer> linkFoundListener) {
            this.linkFoundListener = linkFoundListener;
            return this;
        }

        public ModuleBuilder setLinkProcessedListener(Listener linkProcessedListener) {
            this.linkProcessedListener = linkProcessedListener;
            return this;
        }

        public ModuleBuilder setImgLoadedSizeListener(Listener<Long> imgLoadedSizeListener) {
            this.imgLoadedSizeListener = imgLoadedSizeListener;
            return this;
        }

        public ModuleBuilder setImgFoundListener(Listener<Integer> imgFoundListener) {
            this.imgFoundListener = imgFoundListener;
            return this;
        }

        public ModuleBuilder setImgProcessedListener(Listener imgProcessedListener) {
            this.imgProcessedListener = imgProcessedListener;
            return this;
        }

        public ModuleBuilder setTaskEndListener(Listener taskEndListener) {
            this.taskEndListener = taskEndListener;
            return this;
        }
        public MainModule build() {
            return new MainModule(this);
        }

    }


    private MainModule(ModuleBuilder builder) {
        linkFoundListener = builder.linkFoundListener;
        linkProcessedListener = builder.linkProcessedListener;
        imgLoadedSizeListener = builder.imgLoadedSizeListener;
        imgFoundListener = builder.imgFoundListener;
        imgProcessedListener = builder.imgProcessedListener;
        taskEndListener = builder.taskEndListener;
    }

    public void start(String textToParse, String textMachesAllowed, File folderToSave) throws IOException {

        handler = new Handler();

        TextAreaParser textAreaParser = new TextAreaParser(textToParse);

        textAreaParser.LinkFoundEvent.addEventListner(handler.linkFoundHandler);
        textAreaParser.LinkFoundEvent.addEventListner(linkFoundListener);
        textAreaParser.FinishedEvent.addEventListner(handler.readingFinishedHandler);

        HtmlLoader htmlLoader = new HtmlLoader(textAreaParser.getQueue());
        htmlLoader.LinkProcessedEvent.addEventListner(handler.linkProcessedHandler);
        htmlLoader.LinkProcessedEvent.addEventListner(linkProcessedListener);

        ImgExtractor imgExtractor = new ImgExtractor.Builder(htmlLoader.getContentQueueOut())
                .setAllowed(textMachesAllowed).enablePassThrough(true).build();
        imgExtractor.ImgFoundEvent.addEventListner(handler.imgFoundHandler);
        imgExtractor.ImgFoundEvent.addEventListner(imgFoundListener);
        imgExtractor.EmptySourceEvent.addEventListner(handler.emptySourceHandle);

        ImgLoader imgLoader = new ImgLoader(imgExtractor.getQueueOut(), folderToSave);
        imgLoader.ImgLoadedEvent.addEventListner(handler.ImgLoadedHandler);
        imgLoader.ImgLoadedEvent.addEventListner(imgLoadedSizeListener);
        imgLoader.ImgProcessedEvent.addEventListner(handler.imgProcessedHandler);
        imgLoader.ImgProcessedEvent.addEventListner(imgProcessedListener);

        t1 = new Thread(textAreaParser);
        t1.start();
        t2 = new Thread(htmlLoader);
        t2.start();
        t3 = new Thread(htmlLoader);
        t3.start();

        ti1 = new Thread(imgExtractor);
        ti1.start();
        ti2 = new Thread(imgLoader);
        ti2.start();
    }

    Thread t1, t2, t3, ti1, ti2;

    public void stopFirst() {
        t1.interrupt();
        t2.interrupt();
        t3.interrupt();
    }

    public void stopSecond() {
        ti1.interrupt();
        ti2.interrupt();
        taskEndListener.eventHandler(this, null);
    }

    public void dispose() {
        stopFirst();
        stopSecond();
    }
}

