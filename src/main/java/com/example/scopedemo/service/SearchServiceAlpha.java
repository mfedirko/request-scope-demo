package com.example.scopedemo.service;

import com.example.scopedemo.dao.AlphaDAO;
import com.example.scopedemo.model.SearchResult;
import com.example.scopedemo.model.SearchResultImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/*
 * Simulates some type of search for application 'Alpha'
 */
@Service
public class SearchServiceAlpha implements SearchService {
    @Autowired
    private ExecutionMetaInfoContext metaInfo;
    @Autowired
    private AlphaDAO alphaDAO;


    @Override
    public boolean supports(String app) {
        return "Alpha".equals(app);
    }


    @Override
    public SearchResult provideSearchResults() {
        SearchResultImpl result = new SearchResultImpl();
        result.setResults(Arrays.asList("OK Alpha"));
        result.setSuccessful(true);
        buildSearchToDAO();
        alphaDAO.executeAlphaWebServiceCall();
        return result;
    }

    private void buildSearchToDAO() {
        metaInfo.addStep("Build a request for Alpha repository: ");
    }
}
