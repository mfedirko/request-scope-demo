package com.example.scopedemo.service;

import com.example.scopedemo.model.SearchResult;

public interface SearchService {
    boolean supports(String app);
    SearchResult provideSearchResults();
}
