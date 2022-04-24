package org.tojaco.GraphElements;

import java.util.ArrayList;
import java.util.List;


public class Hashtag implements Stanceable {
    private final String tag;
    private final List<String> words = new ArrayList<String>();
    private int stance;
    private boolean hasStance;

    public List<String> getWords() {
        return words;
    }
    public void addWord(String wordInHashtag){
        words.add(wordInHashtag);
    }

    public void editListOfWords(){
        //for example the word country should not be split into co un and try, we need to remove the words that make up that word
        for(int i = 0; i<words.size();i++){
            String word = words.get(i).toLowerCase();
            String hashtag = null;
            if(word.length()!=1) //don't want to do it for just the letter A or I etc
                hashtag = tag.toLowerCase().substring(tag.length()-word.length());
            if(word.equals(hashtag) && i<words.size()-1){
                //remove all other words from the list
                for(int j = words.size()-1; j>i; j--){
                    words.remove(j);
                }
            }
        }

//        for(int i = 0; i<words.size();i++) {
//            String word = words.get(i);
//            if (word.length() != 1) {//don't want to do it for just the letter A or I etc
//                word.toLowerCase();
//                String hashtag = tag.toLowerCase().substring(tag.length() - word.length());
//                if (word.equals(hashtag) && i < words.size() - 2) {
//                    //remove all other words from the list
//                    for (int j = words.size() - 1; j > i; j--) {
//                        words.remove(j);
//                    }
//                    break;
//                }
//            }
//        }
    }

    public int getStance() { return stance; }
    public void setStance(int stance){
        this.stance = stance;
        hasStance = true;

        if(stance==0){
            hasStance = false;
        }
    }
    public boolean hasStance(){
        return hasStance;
    }

    public Hashtag(String tag){
        this.tag = tag;
    }

    public String toString(){
        return tag.toString();
    }

}
