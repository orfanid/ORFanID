//package com.orfangenes.app;
//
//import com.orfangenes.app.config.DatabaseApiConfig;
//import com.orfangenes.app.util.RestCall;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringRunner;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//@ContextConfiguration(classes = {DatabaseApiConfig.class})
//public class OrfanidApplicationTests {
//
//  @Autowired
//  RestCall restCall;
//
//  @Test
//  public void contextLoads() {}
//
//  @Test
//  public void getAnalysisTableData(){
//    String result = restCall.sendGetRequestWithRetry("analysis/analyses/table", null , null);
//    System.out.println(result);
//  }
//}
