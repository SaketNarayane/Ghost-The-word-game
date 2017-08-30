package com.android.saket.ghost;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class TrieNode {

    private HashMap<String, TrieNode> children;
    private boolean isWord;


    public TrieNode() {
        children = new HashMap<>();
        isWord   = false;
    }

    public void add(String s) {

        HashMap<String,TrieNode> tempAdd = this.children;

        char [] keys = s.toCharArray();
        int end = 0;

        for(char key : keys){
            TrieNode tempNode;
            end++;

          //  Log.d("adding","adding: " + key);

            if(tempAdd.containsKey("" + key)){
               tempNode = tempAdd.get("" + key);
            } else {
               tempNode = new TrieNode();
               tempAdd.put("" + key, tempNode);
             }
            tempAdd = tempNode.children;

            if(end == s.length()) tempNode.isWord = true;

        }



    }

    public boolean isWord(String s) {
        HashMap<String,TrieNode> current = this.children;
        TrieNode nextNode                = null;
        int end                          = 0;
        boolean wordExists               = false;

        Log.d("isWord","checking for word: " + s);
        char [] keys = s.toCharArray();

        for(char key: keys){
            end++;
            if(current.containsKey("" + key)){
                nextNode = current.get("" + key);
                current  = nextNode.children;
            } else {
                return false;
            }

            if(end == s.length()){
                wordExists = nextNode.isWord;
            }
        }

        return wordExists;
    }


    public String getAnyWordStartingWith(String s) {
        HashMap<String, TrieNode> current = this.children;
        TrieNode nextNode                 = null;
        Random random                     = new Random();


        if(s.isEmpty()){
            char letter = (char) (random.nextInt(26) + 'a');
            return "" + letter;
        }

        char [] keys = s.toCharArray();

        for(char key: keys){
            if(current.containsKey("" + key)){
                nextNode = current.get("" + key);
                current  = nextNode.children;
            } else {
                return null;
            }
        }

        return getWord(nextNode);
    }

    // same as above with a random selection of paths to follow
    public String getGoodWordStartingWith(String s) {
        HashMap<String, TrieNode> current = this.children;
        TrieNode nextNode                 = null;
        Random random                     = new Random();


        if(s.isEmpty()){
            char letter = (char) (random.nextInt(26) + 'a');
            return "" + letter;
        }

        char [] keys = s.toCharArray();

        for(char key: keys){
            if(current.containsKey("" + key)){
                nextNode = current.get("" + key);
                current  = nextNode.children;
            } else {
                return null;
            }
        }


       return getBetterWord(s, nextNode);
    }

    //----------------------------------------------------------------------------------------------
    //                                  helper functions
    //----------------------------------------------------------------------------------------------
    public String getWord(TrieNode node){
        HashMap<String, TrieNode> keys = node.children;
        String word = "";

        for(String key: keys.keySet()){
            word += key;
        }
        return word;
    }

    public String getBetterWord(String fragment, TrieNode node){
        HashMap<String, TrieNode> keys = node.children;
        String betterKeys   = "";
        for(String key: keys.keySet()){
            if(!isWord(fragment + key)){
                betterKeys = key;
            }
        }
        ArrayList <String> v = new ArrayList<>();
        v.add("a");
        v.add("e");
        v.add("i");
        v.add("o");
        v.add("u");
        Random r = new Random();
        //if(betterKeys == "") betterKeys = getWord(node);
        if(betterKeys == "") betterKeys = v.get(r.nextInt(v.size()));

        return betterKeys;
    }

    //----------------------------------------------------------------------------------------------
    /**
     * AI: Smarter Computer Feature Below
     */
    //----------------------------------------------------------------------------------------------



    public int getLikelySum(HashMap<String, TrieNode> node, int depth){
        int sum   = 0;
        depth     = 1;

        //since we are using hashmaps in order to access children
        // we need to use keys, so iterate through all keys of node
        // this looks bad, but I couldn't surmise another way
        for(String child : node.keySet()){

            TrieNode c = node.get(child);
            if(c.isWord) return depth % sum;

            sum += getLikelySum(c.children, depth + 1);

        }

        return -1;
    }

}
