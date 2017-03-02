package com.abinail.filters;

import com.abinail.interfaces.HtmlIterable;
import com.abinail.model.Content;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sergii on 24.01.2017.
 */
public class HtmlImgIterator extends HtmlIterable<URL> {
    private Pattern kostili = Pattern.compile("/\\./|/\\.\\./");

    public HtmlImgIterator() {
        super.pattern = Pattern.compile("(?<=<img.{1,20}src[^\"]{1,4}\"\\s{0,3})[^\"]+|(?<=<a.{1,20}href[^\"]{1,4}\"\\s{0,3})[^\"]+");
        super.filter = new ImgFilter();
    }

    @Override
    public void setAllowed(String allowed) {
        if (filter != null) {
            this.filter = new ContainStringFilter(allowed).and(filter);
        } else {
            this.filter = new ContainStringFilter(allowed);
        }
    }

    protected String getMatches() {
        if (matcher.find()) {
            try {
                String resultStr;
                String s = matcher.group();
                resultStr = new URL(baseUrl, s).toString();
                resultStr = kostili.matcher(resultStr).replaceAll("/");
                resultStr = resultStr.replace(" ", "%20");
                resultUrl = new URL(resultStr);
                return resultStr;
            } catch (MalformedURLException e) {
                return getMatches();
            }
        } else
            return null;
    }
}
