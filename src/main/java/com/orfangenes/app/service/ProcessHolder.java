package com.orfangenes.app.service;

import com.orfangenes.app.util.BeanGetService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProcessHolder {
    private static final Map<String, Process> processMap = new ConcurrentHashMap<>();

    public static synchronized void addProcess(String analysisId, Process process) {
        processMap.putIfAbsent(analysisId, process);
    }

    public static synchronized void removeProcess(String analysisId) {
        processMap.remove(analysisId);
    }

    public static synchronized void killProcess(String analysisId) {
        Process process = processMap.get(analysisId);
        if (process != null) {
            DatabaseService databaseService = BeanGetService.getBean(DatabaseService.class);
            databaseService.cancelAnalysis(analysisId);
            process.destroy();
            if (process.isAlive()) {
                process.destroyForcibly();
            }
        }
    }
}
