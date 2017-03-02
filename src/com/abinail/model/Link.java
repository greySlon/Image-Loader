package com.abinail.model;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Sergii on 23.01.2017.
 */
public class Link implements Comparable<Link> {
    public final URL url;
    private boolean ok;

    public Link(URL url) {
        this.url = url;
    }

    public Link(String name) throws MalformedURLException {
        this.url = new URL(name);
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    @Override
    public int compareTo(Link o) {
        return toString().compareTo(o.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return url.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (obj instanceof Link) {
            return toString().equals(((Link) obj).toString());
        } else
            return false;
    }
}
