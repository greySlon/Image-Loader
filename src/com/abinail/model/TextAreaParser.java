package com.abinail.model;

import com.abinail.interfaces.Event;
import com.abinail.interfaces.Notifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Sergii on 02.03.2017.
 */
public class TextAreaParser implements Runnable {
    private ConcurrentLinkedQueue<Link> queue = new ConcurrentLinkedQueue();
    private String text;
    private Notifier linkFoundNotifier = new Notifier();
    private Notifier finishedNotifie = new Notifier();

    public final Event LinkFoundEvent = linkFoundNotifier.getEvent();
    public final Event FinishedEvent = finishedNotifie.getEvent();

    public TextAreaParser(String text) throws IOException {
        this.text = text;
    }

    public Queue getQueue() {
        return queue;
    }

    private void read() throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(text));
        while (true) {
            String str = reader.readLine();
            if (str != null) {
                try {
                    Link link = new Link(str.trim());
                    queue.add(link);
                    linkFoundNotifier.raiseEvent(this, null);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }
        finishedNotifie.raiseEvent(this, null);
    }

    @Override
    public void run() {
        try {
            read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
