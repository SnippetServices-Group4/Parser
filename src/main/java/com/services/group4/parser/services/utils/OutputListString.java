package com.services.group4.parser.services.utils;

import output.OutputResult;

import java.util.ArrayList;
import java.util.List;

public class OutputListString implements OutputResult<String> {
    private final List<String> result = new ArrayList<>();
    @Override
    public void saveResult(String s) {
        result.add(s);
    }

    @Override
    public String getResult() {
        StringBuilder result = new StringBuilder();
        for (String s : this.result) {
            result.append(s).append("\n");
        }
        return result.toString();
    }

    public List<String> getListString() {
        return result;
    }
}
