package main.uk.co.rm.trigram;

import main.uk.co.rm.trigram.model.TrigramModel;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

public class TrigramCreator implements Callable {

    private static final String SPACE = " ";

    private FileChannel fileChannel;
    private long startLocation;
    private int size;
    private TrigramModel trigramModel;
    private ConcurrentHashMap<String, ArrayList<String>> wordMap = new ConcurrentHashMap<>();

    public TrigramCreator(long startLocation, int size, FileChannel fileChannel, TrigramModel trigramModel) {
        this.startLocation = startLocation;
        this.size = size;
        this.fileChannel = fileChannel;
        this.trigramModel = trigramModel;
    }

    @Override
    public TrigramModel call() {
        //allocate memory
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        String stringChunk;
        try {
            //Each thread will read a chunk of file
            fileChannel.read(byteBuffer, startLocation);
            //convert chunk to String
            stringChunk = new String(byteBuffer.array(), StandardCharsets.UTF_8);

            if (!stringChunk.trim().isEmpty()) {
                trigramModel = readChunk(stringChunk);
            }
//             System.out.println("Done Reading the chunk: " + trigramModel.getMapOfTrigrams());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                byteBuffer.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return trigramModel;
    }

    public TrigramModel readChunk(String text) {
        String[] wordArray = text.split(SPACE);
        // trigram = 2 consecutive words make key, 3rd word is value
        for (int i=0; i<wordArray.length-2; i++) {
            //iterating through the array till the last but two words as consecutive words make the key.
            if (wordArray.length < 3) {
                break;
            }
            createMapEntry(this.wordMap, wordArray[i]+ SPACE +wordArray[i+1], wordArray[i+2]);
        }
        updateTrigramDataModel(trigramModel, wordMap);
        return trigramModel;
    }

    //constructing the Trigrams from the input line.
    private  void createMapEntry(ConcurrentHashMap<String, ArrayList<String>>  wordMap, String key, String value) {
        if (null == wordMap) {
            wordMap = new ConcurrentHashMap<>();
        }
        ArrayList<String> valueList = wordMap.get(key);
        if (valueList == null) {
            valueList = new ArrayList<String>();
        }
        valueList.add(value);
        wordMap.put(key, valueList);
    }

    private void updateTrigramDataModel(TrigramModel trigramModel, ConcurrentHashMap wordMap) {

        ConcurrentHashMap map = trigramModel.getMapOfTrigrams();
        wordMap.forEach((k,v) -> {
            ArrayList<String> valueList = (ArrayList)map.get(k);
            if (null == valueList) {
                map.put(k,v);
            } else {
                valueList.addAll((ArrayList)v);
            }
        });

        // for each chunk create a map entry in chunk map so that while we rewrite we can match the no of words.
        ConcurrentHashMap<Integer, Integer> threadChunkWordCountMap = trigramModel.getThreadChunkWordCountMap();
        if (null == threadChunkWordCountMap) {
            threadChunkWordCountMap = new ConcurrentHashMap<Integer, Integer>();
            threadChunkWordCountMap.put(1, wordMap.size());
        } else if (wordMap.size() > 0) {
            int nextKey = threadChunkWordCountMap.size() + 1;
            threadChunkWordCountMap.put(nextKey, wordMap.size());
        }
    }
}
