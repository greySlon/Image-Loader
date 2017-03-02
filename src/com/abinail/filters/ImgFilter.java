package com.abinail.filters;

import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Created by Sergii on 23.01.2017.
 */
public class ImgFilter implements Predicate<String>{
    private Pattern extPattern=Pattern.compile(".jpg$|.png$|.bmp$", Pattern.CASE_INSENSITIVE);
    @Override
    public boolean test(String s) {
        return extPattern.matcher(s).find();
    }
}
