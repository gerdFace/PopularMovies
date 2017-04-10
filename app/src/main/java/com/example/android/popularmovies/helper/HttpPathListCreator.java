package com.example.android.popularmovies.helper;

import java.util.ArrayList;

public class HttpPathListCreator {

    public ArrayList<String> createListForHttpPath(String... params) {
        ArrayList<String> httpPathList = new ArrayList<>();
        for (String newEntry : params) {
            httpPathList.add(newEntry);
        }
        return httpPathList;
    }
}
