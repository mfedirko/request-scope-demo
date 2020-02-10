package com.example.scopedemo.controller;

import com.example.scopedemo.service.ExecutionMetaInfoContext;
import com.example.scopedemo.model.MetaInfo;
import com.example.scopedemo.model.SearchResult;
import com.example.scopedemo.model.SearchResultImpl;
import com.example.scopedemo.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {

    @Autowired
    private List<SearchService> searchServices;

    @Autowired
    private ExecutionMetaInfoContext metaInfo;

    public SearchService getService(String app) {
        return searchServices.stream().filter(s -> s.supports(app)).findFirst().orElse(null);
    }

    @GetMapping("/search")
    public SearchResult getSearchResult(@RequestParam String app) {
//        metaInfo.setRequestDescription("Search for results in application: " + app);
        SearchService service = getService(app);
        SearchResult result;
        if (service != null) {
            result = service.provideSearchResults();
        } else {
            SearchResultImpl searchResult = new SearchResultImpl();
            searchResult.setSuccessful(false);
            result = searchResult;
        }
        result.setMetaInfo(new MetaInfo(metaInfo));
        return result;
    }
}