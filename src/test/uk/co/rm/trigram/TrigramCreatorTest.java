package test.uk.co.rm.trigram;

import main.uk.co.rm.trigram.TrigramCreator;
import main.uk.co.rm.trigram.model.TrigramModel;
import org.junit.Assert;
import org.junit.Test;

public class TrigramCreatorTest {

    /**
     * expected trigram map for String "I wish I may I wish I might"
     *
     *         i wish = I, I
     *         wish I = may, might
     *         I may = I
     *         may i = wish
     */
    @Test
    public void readChunkTest() {
        String text = "I wish I may I wish I might";
        TrigramModel trigramModel = new TrigramModel();

        TrigramCreator trigramCreator = new TrigramCreator(0,0, null, trigramModel);

        trigramModel = trigramCreator.readChunk(text);

        Assert.assertEquals("size matched", 4, trigramModel.getMapOfTrigrams().size());
    }
}
