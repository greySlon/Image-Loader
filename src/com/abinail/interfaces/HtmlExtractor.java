package com.abinail.interfaces;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Sergii on 01.02.2017.
 */
abstract public class HtmlExtractor<T, U> implements Runnable {
    protected BlockingQueue<T> sourceQueue;
    protected BlockingQueue<U> queueOut = new ArrayBlockingQueue<U>(100);
    protected BlockingQueue<T> queuePassThrough;

    protected HtmlExtractor(BlockingQueue<T> sourceQueue) {
        this.sourceQueue = sourceQueue;
    }
    protected HtmlExtractor(){}

    abstract protected void extract() throws InterruptedException;

    protected void enableQueuePassTrough(boolean enable) {
        if (enable)
            this.queuePassThrough = new ArrayBlockingQueue<T>(100);
    }

    public BlockingQueue<U> getQueueOut() {
        return queueOut;
    }

    public BlockingQueue<T> getQueuePassThrough() {
        if (queuePassThrough != null) {
            return queuePassThrough;
        } else {
            throw new RuntimeException("No queue created");
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                extract();
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
