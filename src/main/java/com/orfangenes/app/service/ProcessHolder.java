package com.orfangenes.app.service;

import com.orfangenes.app.util.BeanGetService;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
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
            try {
                process.destroy();
                if (process.isAlive()) {
                    process.destroyForcibly();
                }
            } finally {
                processMap.remove(analysisId);
            }
            try {
                DatabaseService databaseService = BeanGetService.getBean(DatabaseService.class);
                databaseService.cancelAnalysis(analysisId);
            } catch (Exception e) {
                log.warn("Process was killed, but database cancellation failed for {}", analysisId, e);
            }
        }
    }
}
