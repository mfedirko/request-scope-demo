package com.example.scopedemo.model;

import java.util.List;

public class SearchResultImpl implements SearchResult {

    private List results;
    private boolean successful;
    private MetaInfo metaInfo;

    @Override
    public List getResults() {
        return results;
    }

    @Override
    public boolean isSuccessful() {
        return successful;
    }

    @Override
    public MetaInfo getMetaInfo() {
        return metaInfo;
    }

    public void setResults(List results) {
        this.results = results;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public void setMetaInfo(MetaInfo metaInfo) {
        this.metaInfo = metaInfo;
    }
}
