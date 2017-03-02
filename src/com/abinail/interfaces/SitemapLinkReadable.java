package com.abinail.interfaces;

import com.abinail.model.Link;

import java.net.URL;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Sergii on 25.02.2017.
 */
public abstract class SitemapLinkReadable implements Runnable {
    protected Notifier<String> uniqueEventNotifier = new Notifier<>();
    protected Notifier<Integer> linkFoundEventNotifier = new Notifier<>();

    public final Event<Integer> LinkFoundEvent = linkFoundEventNotifier.getEvent();

    public final Event<String> UniqueEvent = uniqueEventNotifier.getEvent();

    public abstract BlockingQueue<Link> getLinkQueue();
}
