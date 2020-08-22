//package com.orfangenes.app.service;
//
////import com.orfangenes.app.config.DatabaseApiConfig;
//import com.orfangenes.app.util.RestCall;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Import;
//import org.springframework.stereotype.Service;
//
///**
// * @author Suresh Hewapathirana
// */
//@Service
//@Import({DatabaseApiConfig.class})
//public class DatabaseService {
//
//    @Autowired
//    RestCall restCall;
//
//    public String getResults() {
//        return restCall.sendGetRequestWithRetry("analysis/analyses/table", null, null);
//    }
//}
