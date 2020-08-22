//package com.orfangenes.app.config;
//
//import com.orfangenes.app.util.RestCall;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @author Suresh Hewapathirana
// */
//@Configuration
//public class DatabaseApiConfig {
//
//    @Bean
//    RestCall restCall(@Value("${db.api.baseUrl}") String baseUrl,
//                      @Value("${db.api.keyName}") String apiKeyName,
//                      @Value("${db.api.keyValue}") String apiKeyValue,
//                      @Value("${spring.application.name}") String appName){
//        return new RestCall(baseUrl, apiKeyName, apiKeyValue, appName);
//    }
//}
