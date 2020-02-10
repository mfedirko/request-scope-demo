package com.example.scopedemo.dao;

import com.example.scopedemo.service.ExecutionMetaInfoContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AlphaDAO {
    private static final String ALPHA_WS_URL = "http://www.abcdefghijklmn.edu/alphabet";
    @Autowired
    private ExecutionMetaInfoContext metaInfoContext;

    public void executeAlphaWebServiceCall() {
        metaInfoContext.addStep("Executing a webservice call to Alpha webservice: " + ALPHA_WS_URL);

    }
}
