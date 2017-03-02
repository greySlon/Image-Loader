package com.abinail.filters;

import com.abinail.model.Content;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sergii on 23.01.2017.
 */
public class BaseResolver {
    private Pattern pattern = Pattern.compile("(?<=<base.{1,20}href[^\"]{1,4}\"\\s{0,3})[^\"]+");

    public URL getBaseUrl(Content in) {
        String content = in.content;
        URL baseUrl = in.url;

        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            String s = matcher.group();
            try {
                baseUrl = new URL(baseUrl, s);
            } catch (MalformedURLException e1) {
            }
        }
        return baseUrl;
    }
}
