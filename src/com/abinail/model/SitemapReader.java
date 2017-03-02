package com.abinail.model;

import com.abinail.interfaces.Event;
import com.abinail.interfaces.Notifier;
import com.abinail.interfaces.SitemapLinkReadable;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Sergii on 25.02.2017.
 */
public class SitemapReader extends SitemapLinkReadable {
    private Element root;
    private BlockingQueue<Link> queueOut = new ArrayBlockingQueue<Link>(100);

    public SitemapReader(URL sitemapUrl) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(sitemapUrl.openStream());
        this.root = doc.getDocumentElement();
    }

    @Override
    public BlockingQueue<Link> getLinkQueue() {
        return this.queueOut;
    }

    @Override
    public void run() {
        try {
            int count = 0;
            NodeList nodeList = root.getElementsByTagName("loc");
            linkFoundEventNotifier.raiseEvent(this, nodeList.getLength());
            for (int i = 0; i < nodeList.getLength(); i++) {
                try {
                    Node node = nodeList.item(i);
                    String text = node.getTextContent().trim();
                    Link link = new Link(text);
                    this.queueOut.put(link);
                    uniqueEventNotifier.raiseEvent(this, text);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(count);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
