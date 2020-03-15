package test.uk.co.rm.trigram;

import main.uk.co.rm.trigram.TrigramWriter;
import main.uk.co.rm.trigram.model.TrigramModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class TrigramWriterTest {

    public TrigramModel trigramModel;

    /**
     * expected trigram map for String "I wish I may I wish I might"
     *
     *         i wish = I, I
     *         wish I = may, might
     *         I may = I
     *         may i = wish
     */
    @Test
    public void rewriteLineTest() throws IOException {
//        TrigramModel trigramModel = setUpData();

        TrigramWriter trigramWriter = new TrigramWriter(trigramModel, null, 1);

        File outputFile = new File("/Users/rm/Downloads/TomSwift/resources/" + "test.txt");

        BufferedWriter bookWriter = new BufferedWriter(new FileWriter(outputFile));
        trigramModel = trigramWriter.rewriteLine(trigramModel, null, null, bookWriter, 8, new StringBuilder());

        //assert word count is used up
        Assert.assertEquals("size matched", 0, trigramModel.getThreadChunkWordCountMap().size());

    }

    @Before
    public void setUpData() {
        String text = "I wish I may I wish I might";
        trigramModel = new TrigramModel();

        ConcurrentHashMap<String, ArrayList<String>> map = new ConcurrentHashMap<String, ArrayList<String>>();
        ArrayList<String> strings0 = new ArrayList<>();
        strings0.add("I");
        strings0.add("I");
        map.put("I wish", strings0);

        ArrayList<String> strings1 = new ArrayList<>();
        strings1.add("may");
        strings1.add("might");
        map.put("wish I", strings1);

        ArrayList<String> strings2 = new ArrayList<>();
        strings2.add("I");
        map.put("I may", strings2);

        ArrayList<String> strings3 = new ArrayList<>();
        strings3.add("wish");
        map.put("may I", strings3);

        trigramModel.setMapOfTrigrams(map);

        ConcurrentHashMap<Integer, Integer> wordCountMap = new ConcurrentHashMap<Integer, Integer> ();
        wordCountMap.put(1, 8);
        trigramModel.setThreadChunkWordCountMap(wordCountMap);
//        return trigramModel;
    }


}
