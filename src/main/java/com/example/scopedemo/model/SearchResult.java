package com.example.scopedemo.model;

import java.util.List;

public interface SearchResult extends ProvidesInfo {
    List getResults();
    boolean isSuccessful();

}
