package com.abinail.filters;

import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Created by Sergii on 23.01.2017.
 */
public class DocFilter implements Predicate<String>{
    private Pattern pattern=Pattern.compile("pdf$|xls$|doc$", Pattern.CASE_INSENSITIVE);
    @Override
    public boolean test(String s) {
        return pattern.matcher(s).find();
    }
}
