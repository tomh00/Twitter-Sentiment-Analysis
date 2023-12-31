package org.tojaco;

import org.tojaco.Graph.*;
import org.tojaco.GraphElements.GraphElements;
import org.tojaco.GraphElements.Hashtag;
import org.tojaco.GraphElements.TwitterUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelUser {

    //by adding list of qualities to each hashtag, and then adding the qualities of the hashtags that user uses, we create model of that user

    public void addSummaryOfHashtag(DirectedGraph<Hashtag, String> hashtagToSummary){
        for(Map.Entry<Vertex<Hashtag>, ArrayList<Arc<String>>> entry: hashtagToSummary.getGraph().entrySet()){
            for(int i=0; i<entry.getValue().size(); i++){
                entry.getKey().getLabel().addQuality(entry.getValue().get(i).getVertex().toString());
            }
            entry.getKey().getLabel().editQualityList();
        }
    }

    public void addSummaryOfHashtagToUserQualities(DirectedGraph<TwitterUser, Hashtag> userToHashtag){

        for(Map.Entry<Vertex<TwitterUser>, ArrayList<Arc<Hashtag>>> entry: userToHashtag.getGraph().entrySet()) {
            for(int i=0; i<entry.getValue().size(); i++){
                entry.getKey().getLabel().addQuality(entry.getValue().get(i).getVertex().getLabel().getQualities());
            }
            if(entry.getKey().getLabel().getStance()<0){
                List<String> proOrAnti = new ArrayList<>();
                proOrAnti.add("anti");
                entry.getKey().getLabel().addQuality(proOrAnti);
            } else if(entry.getKey().getLabel().getStance()>0){
                List<String> proOrAnti = new ArrayList<>();
                proOrAnti.add("pro");
                entry.getKey().getLabel().addQuality(proOrAnti);
            }
        }
    }

    public DirectedGraph<TwitterUser, String> makeUserToQualityGraph(DirectedGraph<TwitterUser, Hashtag> usersToHashtags, GraphElements graphElements) throws IOException {
        DirectedGraph<TwitterUser, String> usersToQualities = new DirectedGraph();
        //to display e.g. rejecting>accepting, I want to show just the one vertex 'rejecting>accepting'
        //rather than the separate vertices 'rejecting(3)' 'accepting(2)', but we can't edit the list directly
        //as we don't want to lose the words on their own and have to split them up if we want to work with them in future

        for(Vertex<TwitterUser> user: usersToHashtags.getGraph().keySet()) {
            List<String> userQualities = new ArrayList<>();
            //to make z score calculation easier, but don't add to actual graph of user qualities
            for(int i=0; i<user.getLabel().getQualities().size();i++){
                userQualities.add(user.getLabel().getQualities().get(i));

            }
            List<String> editedListQualities = editList(userQualities);

            for(int i=0; i<editedListQualities.size();i++){
                VertexCreator<String> vertexCreator = new CreateStringVertex();
                Vertex<String> destVertex = graphElements.getVertex(editedListQualities.get(i), vertexCreator);
                Arc<String> myArc = new Arc<>(destVertex, +1);
                usersToQualities.addArc(user, myArc);
            }
        }
        return usersToQualities;
    }

    public List<String> editList(List<String> listOfQualities) throws IOException {

        listOfQualities.removeIf(str -> str.equals("pro") || str.equals("anti"));

        if(listOfQualities.size()>2) {

            //  Lexicon oppositeQualitiesHashmap = new Lexicon();
            // shouldn't initialise the lexicon more than once...
        HashMap<String, String> opposites = Lexicon.getOppositeQualities();
            for(Map.Entry<String, String> entry : opposites.entrySet()) {
               int changeList = isFocusedOn(entry.getKey(), entry.getValue(), listOfQualities);

               if( changeList==1){
                   listOfQualities.add(entry.getKey() + " > " + entry.getValue());
                   listOfQualities.removeIf(str -> str.equals(entry.getKey()) || str.equals(entry.getValue()));
               }
               else if(changeList==2){
                   listOfQualities.add(entry.getValue() + " > " + entry.getKey());
                   listOfQualities.removeIf(str -> str.equals(entry.getKey()) || str.equals(entry.getValue()));
               }
               else if(changeList==3){
                   listOfQualities.add(entry.getValue() + " = " + entry.getKey());
                   listOfQualities.removeIf(str -> str.equals(entry.getKey()) || str.equals(entry.getValue()));
               }
            }
        }
        return listOfQualities;
    }


    // returns 1 if a user focuses more on property1
    // returns 2 if a user focuses more on property2
    // returns 3 if a user focuses on both properties equally

    public int isFocusedOn(String property1, String property2, List<String> listOfQualities){
        int property1Count = 0, property2Count = 0;
        for(int i=0; i<listOfQualities.size(); i++) {
            if (listOfQualities.get(i).equals(property1)){
                property1Count++;
            }
            else if (listOfQualities.get(i).equals(property2)){
                property2Count++;
            }
        }
        if(property1Count!=0 && property2Count!=0){
            if( property1Count > property2Count ){
                return 1 ; //"property1 > property2" both having at least one of each
            }
            else if (property2Count > property1Count ){
                 return 2 ;
            }
            else if(property1Count == property2Count){
                return 3 ;
            }
        }
        return 0;
    }
}
