package main;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleSingleThrdTrigramCipher {

    public static void main(String[] args) {
        String text = "I wish I may I wish I might";
//        String text = "it was in the wind that was what he thought was his will make companion. I think would be a good one and accordingly the ship their situation improved. Slowly so slowly that it beat the band! You’d will make think no one was a low voice. “Don’t take any of the elements and the inventors of the little Frenchman in the enclosed car or cabin completely fitted up in front of the gas in the house and wringing her hands. “I’m sure they’ll fall!” She looked up at them. He dug a mass of black vapor which it had refused to accept any. As for Mr. Swift as if it goes too high I’ll warn you and you can and swallow frequently. That will make the airship was shooting upward again and just before the raid wouldn’t have been instrumental in capturing the scoundrels right out of jail";
        String[] wordArray = text.split(" ");
        Map<String, ArrayList> wordMap = new ConcurrentHashMap();

        // trigram = 2 consecutive words make key, 3rd word is value
        for (int i=0; i<wordArray.length; i++) {
            //iterating through the array till the last but two words as consecutive words make the key.
            if (i+2 == wordArray.length) {
                break;
            }
            createMapEntry(wordMap, wordArray[i]+" "+wordArray[i+1], wordArray[i+2]);
        }
        System.out.println(wordMap);
        rewriteLine(wordMap, null, null);
    }

    //constructing the Trigrams from the input line.
    public static void createMapEntry(Map<String, ArrayList> wordMap, String key, String value) {
        ArrayList<String> valueList = wordMap.get(key);
        if (valueList == null) {
            valueList = new ArrayList<>();
            wordMap.put(key, valueList);
        }
        valueList.add(value);
    }

    //this method will create a cipher line from the trigrams
    public static void rewriteLine(Map<String, ArrayList> wordMap, String line, String key) {
        String randomKey = key;
        boolean appendPair = false;
        if (null == key || null == wordMap.get(key)) {
            randomKey = getRandomKey(wordMap);
            appendPair = true;
        }
        System.out.println("key-" + randomKey);
        String value = getValueAndClearMap(wordMap, randomKey);
        if (line == null) {
            appendPair = true;
            line = new String();
        }
        if (appendPair) {
            line += (randomKey + " " + value + " ");
        }  else{
            line += value + " ";
        }
        System.out.println("line appended ---- "+line);

        // recursively call this method till there are no entries in the trigram map
        if  (wordMap.size() > 0) {
            String[] s = line.split(" ");
            int length = s.length;
            key = s[length-2] + " " + s[length-1];
            rewriteLine(wordMap, line, key);
        } else {
            //final output
            System.out.println("final line --"+line);
        }

    }

    private static String getValueAndClearMap(Map<String, ArrayList> wordMap, String key) {
        ArrayList<String> listOfValues = wordMap.get(key);
        String value = null;
        //return the first element from the value list
        value = listOfValues.get(0);
        //remove entry set in map if the value list has only one element
        if (listOfValues.size()==1) {
            wordMap.remove(key);
        } else { // remove the first element if the value list has multiple elements
            listOfValues.remove(0);
        }
        System.out.println("value cleared - "+ value +"---Map --" + wordMap);
        return value;
    }

    //this method is for getting a  random trigram key for the first time
    // or if there is no Trigram entry for a given key.
    public static String getRandomKey(Map<String, ArrayList> wordMap) {

        Set<String> keys = wordMap.keySet();
        ArrayList<String> keyList = new ArrayList<>(keys);
        Random random = new Random();
        String s = keyList.get(random.nextInt(keyList.size()));
        System.out.println("getting random key --"+s);
        return s;
    }

}
