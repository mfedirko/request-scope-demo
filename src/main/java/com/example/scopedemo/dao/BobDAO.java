package com.example.scopedemo.dao;

import com.example.scopedemo.service.ExecutionMetaInfoContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class BobDAO {
    @Autowired
    private ExecutionMetaInfoContext metaInfoContext;

    public void doBobWork() {
        metaInfoContext.addStep("Querying the Bob database using SQL: SELECT * from bla bla bla");
    }
}
