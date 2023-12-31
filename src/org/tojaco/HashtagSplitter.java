package org.tojaco;

import org.tojaco.Graph.DirectedGraph;
import org.tojaco.Graph.Vertex;
import org.tojaco.GraphElements.Hashtag;

import java.util.*;

public class HashtagSplitter<T, E> {

    public void splitHashtagsByCamelCase(DirectedGraph<Hashtag, E> hashtagToUsers) {
        for (Vertex<Hashtag> hashtag : hashtagToUsers.getGraph().keySet()) {
            String hashtagWord[] = hashtag.toString().split("(?<=[a-z])(?=[A-Z])");
            for(int i=0; i<hashtagWord.length;i++){
                checkIfAOrI(hashtagWord[i], hashtag);
            }
        }
    }

    public void checkIfAOrI(String hashtagWord, Vertex<Hashtag> hashtag){
        if((hashtagWord.startsWith("A") || hashtagWord.startsWith("I")) && (hashtagWord.length())!=1) {

            char secondLetter = hashtagWord.charAt(1);
            char thirdLetter = 'a';
            if(hashtagWord.length()>2) {
                thirdLetter = hashtagWord.charAt(2);
            }
            if(secondLetter>= 'A' && secondLetter <= 'Z' && thirdLetter >= 'a' && thirdLetter <='z' ){
                String IndefArticle = hashtagWord.substring(0,1); //start index is inclusive, end index is exclusive
                String restOfWord = hashtagWord.substring(1);
                hashtag.getLabel().addWord(IndefArticle.replaceAll("[#.,]","").toLowerCase());
                hashtag.getLabel().addWord(restOfWord.replaceAll("[#.,]","").toLowerCase());
            } else{
                hashtag.getLabel().addWord(hashtagWord.replaceAll("[#.,]","").toLowerCase());
            }
        } else{
            hashtag.getLabel().addWord(hashtagWord.replaceAll("[#.,]","").toLowerCase());
        }
    }

    public static void splitHashtagsByLexiconHelper(String hashtag, Set<String> lexiconDictionary,
                                                    Stack<String> tagComponent, List<List<String>> splitStr) {

        hashtag = hashtag.replaceAll("[#]", "").toLowerCase();

        for (int i = 0; i < hashtag.length(); i++) {
            String substring = hashtag.substring(0, i + 1);

            if (lexiconDictionary.contains(substring)) {

                // we use a stack to maintain the order of the words
                tagComponent.push(substring);

                if (i == hashtag.length() - 1) {
                    splitStr.add(new ArrayList<>(tagComponent));
                } else {
                    // recursive call
                    splitHashtagsByLexiconHelper(hashtag.substring(i + 1),
                            lexiconDictionary, tagComponent, splitStr);
                }
                // pop matched word from the stack
                tagComponent.pop();
            }
        }
    }

    public void splitHashtagsByLexicon(DirectedGraph<Hashtag, E> sumHashTagGraph, Set<String> lexiconDictionary) {

        for (Vertex<Hashtag> hashtag : sumHashTagGraph.getGraph().keySet()) {

            List<List<String>> splitStr = new LinkedList<>();

            splitHashtagsByLexiconHelper(hashtag.toString(), lexiconDictionary, new Stack<>(), splitStr);

            // then add the word splits
            for (List<String> listResult : splitStr) {
                for (String word : listResult) {
                    if (!hashtag.getLabel().getWords().contains(word)) {
                        //checkIfAOrI(word, hashtag);
                        hashtag.getLabel().addWord(word);
                    }
                }
            }

            hashtag.getLabel().editListOfWords();
        }
    }
}
