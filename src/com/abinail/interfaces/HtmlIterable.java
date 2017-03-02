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
public abstract class HtmlIterable<T> implements Iterable<T> {
    protected T resultUrl;
    protected Predicate<String> filter;
    protected BaseResolver baseResolver;
    protected Matcher matcher;
    protected URL baseUrl;
    protected Pattern pattern;

    public void setIn(Content in) throws MalformedURLException {
        if (baseResolver == null) {
            baseResolver = new BaseResolver();
        }
        matcher = pattern.matcher(in.content);
        baseUrl = baseResolver.getBaseUrl(in);
    }

    public void setAllowed(String allowed) {
    }

    public void setDisalowed(String disallowed) {
    }

    @Override
    public Iterator iterator() {
        return new Iterator() {
            @Override
            public boolean hasNext() {
                String resultStr = getMatches();
                if (filter == null) {
                    return resultStr != null;
                } else {
                    return resultStr != null && (filter.test(resultStr) || this.hasNext());
                }
            }

            @Override
            public T next() {
                return resultUrl;
            }
        };
    }

    protected abstract String getMatches();
}
