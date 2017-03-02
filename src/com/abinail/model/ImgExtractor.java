package com.abinail.model;

import com.abinail.filters.HtmlImgIterator;
import com.abinail.interfaces.Event;
import com.abinail.interfaces.Notifier;
import com.abinail.interfaces.HtmlExtractor;
import com.abinail.interfaces.HtmlIterable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Sergii on 25.01.2017.
 */

public class ImgExtractor extends HtmlExtractor<Content, URL> {
    private HtmlIterable<URL> htmlIterable = new HtmlImgIterator();
    private Notifier<Integer> imgFoundEventNotifier = new Notifier();
    private Notifier<URL> emptySourceNotifier = new Notifier();

    public final Event<Integer> ImgFoundEvent = imgFoundEventNotifier.getEvent();
    public final Event EmptySourceEvent = emptySourceNotifier.getEvent();

    public static class Builder {
        private BlockingQueue<Content> sourceQueue;
        private boolean enablePassThrough;
        private String allowed;

        public Builder(BlockingQueue<Content> sourceQueue) {
            this.sourceQueue = sourceQueue;
        }

        public Builder enablePassThrough(boolean enable) {
            this.enablePassThrough = enable;
            return this;
        }

        public Builder setAllowed(String allowed) {
            this.allowed = allowed;
            return this;
        }

        public ImgExtractor build() {
            return new ImgExtractor(this);
        }
    }

    private ImgExtractor(Builder builder) {
        super(builder.sourceQueue);
        htmlIterable.setAllowed(builder.allowed);
        enableQueuePassTrough(builder.enablePassThrough);
    }

    @Override
    protected void extract() throws InterruptedException {
        try {
            Content content = sourceQueue.take();
            htmlIterable.setIn(content);
            int count=0;
            for (URL url : htmlIterable) {
                queueOut.put(url);
                count++;
            }
            imgFoundEventNotifier.raiseEvent(this, count);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if(sourceQueue.size()==0){
            emptySourceNotifier.raiseEvent(this, null);
        }
    }
}