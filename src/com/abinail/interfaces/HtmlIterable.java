package com.abinail.interfaces;

import com.abinail.filters.BaseResolver;
import com.abinail.model.Content;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sergii on 24.01.2017.
 */
public interface HtmlIterable<T> extends Iterable<T> {

    void setIn(Content in) throws MalformedURLException;

    default void setAllowed(String allowed) {
    }

    default void setDisalowed(String disallowed) {
    }
}
