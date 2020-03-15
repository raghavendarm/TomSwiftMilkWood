package main.uk.co.rm.trigram;

import main.uk.co.rm.trigram.model.TrigramModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;


public class TrigramWriter implements Callable {

    private TrigramModel trigramModel;
    private File outputFile;
    private int chunkNo;

    private static final String SPACE = " ";

    public TrigramWriter(TrigramModel trigramModel, File outFile, int chunkNo){
        this.trigramModel = trigramModel;
        outputFile = outFile;
        this.chunkNo = chunkNo;
    }

    @Override
    public TrigramModel call() throws Exception {
        BufferedWriter bookWriter =null;
        try {
            bookWriter = new BufferedWriter(new FileWriter(outputFile, true));
            Integer noOfWords = trigramModel.getThreadChunkWordCountMap().get(chunkNo);
            //Initialise StringBulder per thread/chunk
            StringBuilder sb = new StringBuilder();
            rewriteLine(trigramModel, null, null, bookWriter, noOfWords, sb);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != bookWriter) {
                bookWriter.close();
            }
        }
        return trigramModel;
    }

    //this method will create a cipher chunk from the trigrams
    public TrigramModel rewriteLine(TrigramModel trigramModel, String chunk, String key, BufferedWriter bookWriter,
                            Integer noOfWords, StringBuilder sb ) {

        boolean appendKeyValuePair = (chunk == null) ? true : false;

        ConcurrentHashMap<String, ArrayList<String>> wordMap = trigramModel.getMapOfTrigrams();
        if (null == key || null == wordMap.get(key)) {
            key = getRandomTrigram(wordMap);
            appendKeyValuePair = true;
        }
        String value = getValueAndClearMap(wordMap, key);
        if (appendKeyValuePair) {
            chunk = (key + SPACE + value + SPACE);
        }  else {
            chunk = value + SPACE;
        }
        sb.append(chunk);

        String[] s = sb.toString().split(SPACE);
        int length = s.length;
        if  (length < noOfWords) {
            key = s[length-2] + SPACE + s[length-1];
            // recursively call this method till there are word count matches the chunk of strings word count
            rewriteLine(trigramModel, chunk, key, bookWriter, noOfWords, sb);
        } else {
            //final output per thread/chunk is written to file
            try {
//                System.out.println("writing to file -- " + sb );
                trigramModel.getThreadChunkWordCountMap().remove(chunkNo);
                bookWriter.append(sb);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return trigramModel;
    }

    private String getValueAndClearMap(ConcurrentHashMap<String, ArrayList<String>> wordMap, String key) {
        ArrayList<String> listOfValues = wordMap.get(key);
        String value;
        //return the first element from the value list
        value = listOfValues.get(0);
        //remove entry set in map if the value list has only one element
        if (listOfValues.size()==1) {
            wordMap.remove(key);
        } else { // remove the first element if the value list has multiple elements
            listOfValues.remove(0);
        }
//        System.out.println("value cleared - "+ value +"---Map --" + wordMap);
        return value;
    }

    // this method is for getting a  random trigram key for the first time
    // or if there is no Trigram entry for a given key.
    private String getRandomTrigram(ConcurrentHashMap<String, ArrayList<String>> wordMap) {
        ArrayList<String> keyList = new ArrayList<>(wordMap.keySet());
        String randomKey = keyList.get(new Random().nextInt(keyList.size()));
        //System.out.println("getting random key --"+randomKey);
        return randomKey;
    }
}
