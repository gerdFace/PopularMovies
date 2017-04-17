package com.example.android.popularmovies.helper;

import java.util.ArrayList;
import java.util.Collections;

public class HttpPathListCreator {

    public ArrayList<String> createListForHttpPath(String... params) {
        ArrayList<String> httpPathList = new ArrayList<>();
        Collections.addAll(httpPathList, params);
        return httpPathList;
    }
}
