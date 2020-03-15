package main;




import main.uk.co.rm.trigram.TrigramCreator;
import main.uk.co.rm.trigram.TrigramWriter;
import main.uk.co.rm.trigram.model.TrigramModel;

import java.io.File;
import java.io.FileInputStream;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TomSwitftMilkwood {

    public static void main(String[] args) throws Exception {
        // splitting chunks of 4096 bytes
        int threadChunkSize = 4096;
        FileInputStream fileInputStream = null;
//        String bookFileUrl;
        ExecutorService executor = null;
        TrigramModel trigramModel = new TrigramModel();
        long startBufferLocation = 0;

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter file name with path: (ex: c://work//book.txt)");
        String filePath = scanner.nextLine();
        File bookFile = getFile(filePath);

        Future<TrigramModel> result = null;
        //String root  =  "/Users/rm/Desktop/TomSwiftMilkwood/src/main/resources/";

        try {
            fileInputStream = new FileInputStream(bookFile);
            FileChannel channel = fileInputStream.getChannel();
            long totalBufferSize = channel.size(); //get the total number of bytes in the file
            int noOfThreads = (threadChunkSize < totalBufferSize) ? (int)(totalBufferSize / threadChunkSize) : 1;

            //initialise thread pool
            executor = Executors.newFixedThreadPool(noOfThreads);

            //with each iteration increment startLocation with chunkSize
            //and subtract totalBufferSize with the processed chunk size.
            while (totalBufferSize > 0 ) {
                //launches a new thread
                result = executor.submit(new TrigramCreator(startBufferLocation,
                    threadChunkSize,
                    channel,
                    trigramModel));

                if (totalBufferSize > threadChunkSize) {
                    totalBufferSize -= threadChunkSize;
                    startBufferLocation += threadChunkSize;
                } else {
                    //this would be the last chunk
                    startBufferLocation += totalBufferSize;
                    totalBufferSize = 0;
                }
            }
            //Write trigrams to file
            TrigramModel generatedTrigramModel = result.get();

            File outputFile = new File(bookFile.getParent() + "/Trigram_" + new Date().getTime() + ".txt");

            for (int j = 1; j <= generatedTrigramModel.getThreadChunkWordCountMap().size(); j++) {
                executor.submit(new TrigramWriter(generatedTrigramModel, outputFile, j));
            }

        } catch (Exception e) {
            System.out.println("Unexpected exception happened----" + e.getMessage());
        } finally {
            fileInputStream.close();
            executor.shutdown();
        }
    }

    public static File getFile(String filePath) throws Exception {
        File book = null;
        try {
            book = new File(filePath);
        } catch (Exception e) {
            throw new Exception("Cannot read the file");
        }
        return book;
    }
}
