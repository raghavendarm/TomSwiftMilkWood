package main.uk.co.rm.trigram.model;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class TrigramModel {

    private ConcurrentHashMap<String, ArrayList<String>> mapOfTrigrams;

    private ConcurrentHashMap<Integer, Integer> threadChunkWordCountMap;

    public TrigramModel() {
        mapOfTrigrams = new ConcurrentHashMap<>();
        threadChunkWordCountMap = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<String, ArrayList<String>> getMapOfTrigrams() {
        return mapOfTrigrams;
    }

    public void setMapOfTrigrams(ConcurrentHashMap<String, ArrayList<String>> mapOfTrigrams) {
        this.mapOfTrigrams = mapOfTrigrams;
    }

    public ConcurrentHashMap<Integer, Integer> getThreadChunkWordCountMap() {
        return threadChunkWordCountMap;
    }

    public void setThreadChunkWordCountMap(ConcurrentHashMap<Integer, Integer> threadChunkWordCountMap) {
        this.threadChunkWordCountMap = threadChunkWordCountMap;
    }
}
