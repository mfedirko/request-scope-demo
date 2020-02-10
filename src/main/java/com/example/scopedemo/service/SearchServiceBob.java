package com.example.scopedemo.service;

import com.example.scopedemo.concurrent.ContextInheritingThreadPoolExecutor;
import com.example.scopedemo.dao.BobDAO;
import com.example.scopedemo.model.SearchResult;
import com.example.scopedemo.model.SearchResultImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.*;

/*
 * Simulates some type of search for application 'Bob'
 */
@Service
public class SearchServiceBob implements SearchService{
    @Autowired
    private ExecutionMetaInfoContext metaInfo;

    @Autowired
    private BobDAO bobDAO;


    @Override
    public boolean supports(String app) {
        return "Bob".equals(app);
    }

    @Override
    public SearchResult provideSearchResults() {
        ContextInheritingThreadPoolExecutor executorService = new ContextInheritingThreadPoolExecutor();
        SearchResultImpl result = new SearchResultImpl();
        result.setResults(Arrays.asList("OK Bob"));
        result.setSuccessful(true);
//        buildSearchToDAO();

        concurrentStep1(executorService);
        concurrentStep2(executorService);
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        bobDAO.doBobWork();
        return result;
    }

    private void buildSearchToDAO() {
        metaInfo.addStep("Build a search request for Bob DAO repository");
    }

    private void concurrentStep1(ExecutorService executor) {

        executor.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                metaInfo.newSubProcess("Bob-A");
                metaInfo.addStep("Some concurrent step 1");
                metaInfo.addStep("Some concurrent step 1 continue");
                metaInfo.addStep("Some concurrent step 1 end");
                return new Object();
            }
        });
    }
    private void concurrentStep2(ExecutorService executor) {

        executor.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                metaInfo.newSubProcess("Bob B");
                metaInfo.addStep("Some concurrent step two");
                metaInfo.addStep("Some concurrent step two continue");
                metaInfo.addStep("Some concurrent step two end");
                return new Object();
            }
        });
    }
}
