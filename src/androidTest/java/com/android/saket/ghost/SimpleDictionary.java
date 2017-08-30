package com.android.saket.ghost;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleDictionary implements GhostDictionary {
    private ArrayList<String> words;

    private ArrayList<String> even;
    private ArrayList<String> odd;

    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        words = new ArrayList<>();
        even  = new ArrayList<>();
        odd   = new ArrayList<>();
        String line = null;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH) {
                words.add(line.trim());
            }

        }

        populateEvenOdd();
    }

    @Override
    public boolean isWord(String word) {
        return words.contains(word);
    }

    @Override
    public String getAnyWordStartingWith(String prefix) {

        if(prefix.isEmpty()){
            Random random = new Random();
            return words.get(random.nextInt(words.size()));
        }

        int word = binarySearch(words, prefix);

        if(word >= words.size() || word == -1) {
            return null;
        }


        return words.get(word);
    }

    @Override
    public String getGoodWordStartingWith(String prefix) {
        int word = -1;

        if(prefix.isEmpty()){
            Random random = new Random();
            return words.get(random.nextInt(words.size()));
        }

        if(prefix.length()%2 == 0 ){
            word = binarySearch(even, prefix);
            if(word == -1) return null;
            return even.get(word);

        }

        if(prefix.length()%2 == 1){
            word = binarySearch(odd, prefix);
            if(word == -1) return null;
            return odd.get(word);
        }

        return null;
    }

    //                              helper functions
    // ----------------------------------------------------------------------------------------

    private int binarySearch(ArrayList<String> list, String value) {
        Pattern pattern = Pattern.compile(value);

        int low = 0;
        int high = list.size() - 1;

        while (low <= high) {

            int mid = (low + high) / 2;

            String middle = list.get(mid);
            Matcher m     = pattern.matcher(middle);

            if(m.find()) return mid;

            int cmp = middle.compareTo(value);

            if (cmp < 0)
                low = mid + 1;
            else if (cmp > 0)
                high = mid - 1;
            else
                return mid;
        }

        return -1;
    }

    private void populateEvenOdd(){

        for(int i = 0; i < words.size(); i++){
            if(words.get(i).length()%2 == 0){
                even.add(words.get(i));
            } else {
                odd.add(words.get(i));
            }
        }
    }
}
