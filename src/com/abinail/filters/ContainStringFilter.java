package com.abinail.filters;

import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Created by Sergii on 29.01.2017.
 */
public class ContainStringFilter implements Predicate<String>{
    private Pattern pattern;

    public ContainStringFilter(String containString) {
        StringBuilder sb=new StringBuilder(100);

        if(containString!=null && !containString.isEmpty()) {
            String[] subStr = containString.split(" ");
            for (String s : subStr) {
                if(!s.isEmpty()) {
                    if(sb.length()>0)sb.append("|");
                    sb.append(s);
                }
            }
        }
        pattern=Pattern.compile(sb.toString(), Pattern.CASE_INSENSITIVE);
    }

    @Override
    public boolean test(String s) {
        return pattern.matcher(s).find();
    }
}
