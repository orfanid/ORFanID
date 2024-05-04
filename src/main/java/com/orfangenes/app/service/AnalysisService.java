package com.orfangenes.app.service;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class AnalysisService {

    @Autowired
    DatabaseService databaseService;

    public void deleteAnalysis(String id) throws IOException {
        databaseService.deleteAnalysis(id);
        FileUtils.deleteDirectory(new File("/mnt/data/orfanid/orfanid/out/"+id));
    }
}
