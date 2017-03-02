package com.abinail.filters;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Sergii on 24.01.2017.
 */
public class QueryParamReplacer {
    private List<Pattern> patternList = new LinkedList<>();

    public QueryParamReplacer(String queryParamsToRemove) {
        if (queryParamsToRemove != null && !queryParamsToRemove.isEmpty()) {
            String[] queries = queryParamsToRemove.split(" ");

            for (String param : queries) {
                if (!param.isEmpty()) {
                    String patternString = "(?<=\\?)" + param + "=[^\\s\\&]*(\\&){0,1}";
                    patternList.add(Pattern.compile(patternString, Pattern.CASE_INSENSITIVE));
                    patternString = "(?<=\\?)" + param + "$";
                    patternList.add(Pattern.compile(patternString, Pattern.CASE_INSENSITIVE));
                    patternString = "(?<=\\?)" + param + "\\&";
                    patternList.add(Pattern.compile(patternString, Pattern.CASE_INSENSITIVE));
                    //
                    patternString = "\\&" + param + "=[^\\s\\&]*";
                    patternList.add(Pattern.compile(patternString, Pattern.CASE_INSENSITIVE));
                    patternString = "\\&" + param + "$";
                    patternList.add(Pattern.compile(patternString, Pattern.CASE_INSENSITIVE));
                    patternString = "\\&" + param + "(?=\\&)";
                    patternList.add(Pattern.compile(patternString, Pattern.CASE_INSENSITIVE));

                }
            }
        }
    }

    public String removeParam(String s) {
        for (Pattern pattern : patternList) {
            s = pattern.matcher(s).replaceAll("");
        }
        return s;
    }
}
