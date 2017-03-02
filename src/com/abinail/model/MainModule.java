package com.abinail.model;

import com.abinail.interfaces.Listener;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Sergii on 02.03.2017.
 */
public class MainModule {
    private TextAreaParser textAreaParser;
    //events
    private Listener<Integer> linkFoundListener;
    private Listener linkProcessedListener;
    private Listener<Long> imgLoadedSizeListener;
    private Listener<Integer> imgFoundListener;
    private Listener imgProcessedListener;
    private Listener taskEndListener;

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

    private Handler handler;
    private ExecutorService exec;
    private ExecutorService exec2;


    private MainModule(ModuleBuilder builder) {
        linkFoundListener = builder.linkFoundListener;
        linkProcessedListener = builder.linkProcessedListener;
        imgLoadedSizeListener = builder.imgLoadedSizeListener;
        imgFoundListener = builder.imgFoundListener;
        imgProcessedListener = builder.imgProcessedListener;
        taskEndListener = builder.taskEndListener;
        handler = new Handler(this);
    }

    public void start(String textToParse, String textMachesAllowed, File folderToSave) throws IOException {
        handler.reset();
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

        exec = Executors.newFixedThreadPool(4);
        exec.submit(textAreaParser);
        for (int i = 0; i < 4; i++) {
            exec.submit(htmlLoader);
        }

        exec2 = Executors.newFixedThreadPool(2);
        exec2.submit(imgExtractor);
        exec2.submit(imgLoader);
    }

    public void stopFirst() {
        exec.shutdownNow();
    }

    public void stopSecond() {
        exec2.shutdownNow();
        taskEndListener.eventHandler(this, null);
    }

    public void dispose() {
        stopFirst();
        stopSecond();
    }
}

class Handler {
    private MainModule controller;
    private int linkFound;
    private int linkProcessed;
    private boolean allRead;
    private boolean linkProcessingStoped;

    private boolean emptySource;
    private int imgFound;
    private int imgProcessed;

    public void reset() {
        linkFound = linkProcessed = imgFound = imgProcessed = 0;
        allRead = linkProcessingStoped = emptySource = false;
        checkListFinish();
        checkImgFinish();
    }

    public Handler(MainModule controller) {
        this.controller = controller;
    }

    public final Listener linkFoundHandler = (sender, arg) -> {
        Platform.runLater(() -> {
            linkFound++;
            checkListFinish();
        });
    };

    public final Listener linkProcessedHandler = (sender, arg) -> {
        Platform.runLater(() -> {
            linkProcessed++;
            checkListFinish();
        });
    };

    public final Listener readingFinishedHandler = (sender, arg) -> {
        Platform.runLater(() -> {
            allRead = true;
            checkListFinish();
        });
    };
    //images
    public final Listener<Integer> imgFoundHandler = (sender, count) -> {
        Platform.runLater(() -> {
            imgFound += count;
            checkImgFinish();
        });
    };

    public final Listener imgProcessedHandler = (sender, arg) -> {
        Platform.runLater(() -> {
            imgProcessed++;
            checkImgFinish();
        });
    };

    public final Listener<Long> ImgLoadedHandler = (sender, size) -> {
        Platform.runLater(() -> {
            checkImgFinish();
        });
    };

    public final Listener emptySourceHandle = (sender, size) -> {
        Platform.runLater(() -> {
            if (linkProcessingStoped) {
                emptySource = true;
            }
        });
    };

    private void checkImgFinish() {
        if (linkProcessingStoped && emptySource && imgFound == imgProcessed) {
            controller.stopSecond();
        }
    }

    private void checkListFinish() {
        if (allRead && linkFound == linkProcessed) {
            controller.stopFirst();
            this.linkProcessingStoped = true;
        }
    }
}
