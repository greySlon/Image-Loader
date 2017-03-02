package com.abinail.model;

import com.abinail.interfaces.Event;
import com.abinail.interfaces.Notifier;
import com.sun.istack.internal.NotNull;

import java.io.*;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sergii on 25.01.2017.
 */
public class ImgLoader implements Runnable {
    private BlockingQueue<URL> urlQueueIn;
    private File folder;
    private StringBuilder sb = new StringBuilder(300);

    private Notifier<Long> imgLoadedEventNotifier = new Notifier<>();
    private Notifier imgProcessedEventNotifier = new Notifier();

    public final Event<Long> ImgLoadedEvent = imgLoadedEventNotifier.getEvent();
    public final Event ImgProcessedEvent = imgProcessedEventNotifier.getEvent();

    public ImgLoader(BlockingQueue<URL> queueIn, File folder) {
        this.urlQueueIn = queueIn;
        this.folder = folder;
    }

    private Pattern pattern = Pattern.compile("(?<=\\/)[^\\/]+?$", Pattern.CASE_INSENSITIVE);

    @Override
    public void run() {
        while (true) {
            try {
                URL url=urlQueueIn.take();
                String newName = getNewName(url.getPath());
                long fileSize = saveFile(url, newName);
                imgLoadedEventNotifier.raiseEvent(this, fileSize);
            } catch (InterruptedException e) {
                return;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                imgProcessedEventNotifier.raiseEvent(this, null);
            }
        }
    }

    private String getNewName(String startName) throws IOException {
        sb.setLength(0);
        Matcher matcher = pattern.matcher(startName);
        String fName = null;
        if (matcher.find()) {
            fName = matcher.group();
        } else {
            throw new IOException();
        }
        return sb.append(fName.substring(0, fName.length() - 4))
                .append("(").append(startName.hashCode()).append(")")
                .append(fName.substring(fName.length() - 4, fName.length())).toString();
    }

    private long saveFile(URL url, String name) throws IOException {
        File file = new File(folder, name);
        return Files.copy(url.openStream(), file.toPath());
    }
}
