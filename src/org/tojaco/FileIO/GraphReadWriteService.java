package org.tojaco.FileIO;

import org.tojaco.Graph.*;
import org.tojaco.GraphElements.Hashtag;
import org.tojaco.GraphElements.TwitterUser;

import java.io.*;
import java.util.*;

// HANDLES reading and writing to/from the graph output file
public class GraphReadWriteService extends FileService {

    private final ArrayList<String> hashtags = new ArrayList<>();

    private final ArrayList<String> mentions = new ArrayList<>();

    public ArrayList<String> getHashtags() {
        return hashtags;
    }

    public ArrayList<String> getMentions() {
        return mentions;
    }

    public <T, E> void writeFileFromGraph(DirectedGraph<T, E> graph, File file, boolean weight) throws IOException {

        createFile(file.getParent(), file.getName());

        Map<Vertex<T>, ArrayList<Arc<E>>> graphHashMap = graph.getGraph();

        StringBuilder sb = new StringBuilder();

        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {

            for (Vertex<T> vertex : graphHashMap.keySet()) {

                sb.append(vertex.getLabel().toString()).append(" [");

                for (int i = 0; i < graphHashMap.get(vertex).size(); i++) {
                    if (i > 0) {
                        sb.append(", ").append(graphHashMap.get(vertex).get(i).getVertex().getLabel());
                    } else {
                        sb.append(graphHashMap.get(vertex).get(i).getVertex().getLabel());
                    }
                    if(weight) {
                        sb.append("(").append(graphHashMap.get(vertex).get(i).getWeight()).append(")");
                    }

                }

                sb.append("]");
                pw.println(sb);
                sb.setLength(0);
                pw.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<String> loadDataFromInputFile(File file) {

        final ArrayList<String> retweets = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] lineContents = line.split("\t");
                try {
                    Long.parseLong(lineContents[0]);

                    String retweetedUserWithText = lineContents[2];
                    String[] tweetText = retweetedUserWithText.split(" "); //lineContents[2] is "RT @RetweetedUser tweetText" if it's a retweet
                    String retweetedUser = tweetText[1];

                    if (isTextRetweet(tweetText)) {
                        String username = retweetedUser.replaceAll(":", ""); //remove : after the retweeted user
                        retweets.add(lineContents[1] + "\t" + username); //adds @User + "\t" + @RetweetedUser and whatever they tweeted
                    }
                    String temp = retweetedUserWithText.trim().replaceAll(" +", " ");
                    //String temp = lineContents[2].replaceAll("  ", " ");

                    String[] mentionSplitter  = temp.split("[^a-zA-Z@]+"); //split the tweet text

                    for (String tweetComponent : mentionSplitter) {

                        if (tweetComponent.startsWith("@") && !(retweetedUser.equals(tweetComponent) )) {
                            mentions.add(lineContents[1] + "\t" + tweetComponent);
                        }
                    }

                    tweetText = temp.split("[^a-zA-Z#]+"); //split the tweet text
                    //tweetText= tweetText.split(" ");
                    for (String tweetComponent : tweetText) {
                        String hashtagInLine = null;
                        if (tweetComponent.startsWith("#")) {
                            hashtagInLine = tweetComponent;
                        }
                        if (hashtagInLine != null) {
                            hashtags.add(lineContents[1] + "\t" + hashtagInLine);
                        }
                    }

                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                } catch (Exception ignored) {
                }
            }
        } catch (IOException | NullPointerException fnfe) {
            fnfe.printStackTrace();
        }
        return retweets;
    }

    private boolean isTextRetweet(String[] lineContents) {
        return lineContents[0].contains("RT") && lineContents[1].startsWith("@");

    }
    public void writeGephiFile(DirectedGraph<TwitterUser, TwitterUser> graph, File file) throws IOException {
        createFile(file.getParent(), file.getName());
        StringBuilder sb = new StringBuilder();
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            sb.append("nodedef>name VARCHAR,stance VARCHAR,focus VARCHAR, politics VARCHAR, acceptorreject VARCHAR, problemorsolution VARCHAR"/*,label VARCHAR,class VARCHAR, visible BOOLEAN," +
                        "labelvisible BOOLEAN,width DOUBLE,height DOUBLE,x DOUBLE,y DOUBLE,color VARCHAR"*/);
            pw.println(sb);
            sb.setLength(0);
            pw.flush();

            for(Vertex<TwitterUser> vertex: graph.getGraph().keySet()){
                sb.append(vertex.getLabel().getUserHandle() + ",");
                sb.append(outputProOrAntiUser(vertex));
                sb.append(outputUserDominantProperty(vertex, "rights", "responsibilities"));
                sb.append(outputUserDominantProperty(vertex, "leftwing", "rightwing"));
                sb.append(outputUserDominantProperty(vertex, "accepting", "rejecting"));
                sb.append(outputUserDominantProperty(vertex, "problem", "solution"));
                pw.println(sb);
                sb.setLength(0);
                pw.flush();
            }

            sb.append("edgedef>node1 VARCHAR,node2 VARCHAR , weight DOUBLE"); //,directed BOOLEAN");
            pw.println(sb);
            sb.setLength(0);
            pw.flush();

            for(Vertex<TwitterUser> vertex: graph.getGraph().keySet()){
                for(Arc<TwitterUser> arc : graph.getGraph().get(vertex)){
                    sb.append(vertex.getLabel().getUserHandle());
                    sb.append("," + arc.getVertex().getLabel().getUserHandle());
                    sb.append(",").append(arc.getWeight());
                    pw.println(sb);
                    sb.setLength(0);
                    pw.flush();
                }
            }
        }

        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void writeGephiHashtagFile(DirectedGraph<Hashtag, TwitterUser> graph, File file) throws IOException {
        createFile(file.getParent(), file.getName());
        StringBuilder sb = new StringBuilder();
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            sb.append("nodedef>name VARCHAR,stance VARCHAR,acceptOrReject VARCHAR"/*,label VARCHAR,class VARCHAR, visible BOOLEAN," +
                        "labelvisible BOOLEAN,width DOUBLE,height DOUBLE,x DOUBLE,y DOUBLE,color VARCHAR"*/);
            pw.println(sb);
            sb.setLength(0);
            pw.flush();

            for(Vertex<Hashtag> vertex: graph.getGraph().keySet()){
                sb.append(vertex.getLabel() + ",");
                if(vertex.getLabel().hasStance()){
                    if(vertex.getLabel().getStance()<0)
                        sb.append("anti");
                    else if(vertex.getLabel().getStance()>0){
                        sb.append("pro");
                    }
                }else {
                    sb.append("neutral");
                }
                int acceptance = 0;
                int rejection = 0;
                for(int i=0; i<vertex.getLabel().getQualities().size(); i++){
                    if(vertex.getLabel().getQualities().get(i).equals("accepting")){
                        acceptance++;
                    } else if(vertex.getLabel().getQualities().get(i).equals("rejecting")){
                        rejection++;
                    }
                }
                if(acceptance>rejection){
                    sb.append(",accepting");
                } else if(rejection>=acceptance && rejection!=0){
                    sb.append(",rejecting");
                } else if(acceptance==0 && rejection ==0){
                    sb.append(",neither");
                }

                pw.println(sb);
                sb.setLength(0);
                pw.flush();
            }

            for(Vertex<Hashtag> vertex: graph.getGraph().keySet()){
                for(Arc<TwitterUser> arc : graph.getGraph().get(vertex)){
                    sb.append(arc.getVertex().getLabel().getUserHandle());
                    sb.append(",");

                    sb.append(outputProOrAntiUser(arc.getVertex()));

                    String toAdd = outputUserDominantProperty(arc.getVertex(),"accepting","rejecting");
                    sb.append(toAdd);
                    pw.println(sb);
                    sb.setLength(0);
                    pw.flush();
                }
            }

            sb.append("edgedef>node1 VARCHAR,node2 VARCHAR,weight DOUBLE"); //,directed BOOLEAN");
            pw.println(sb);
            sb.setLength(0);
            pw.flush();

            for(Vertex<Hashtag> vertex: graph.getGraph().keySet()){
                for(Arc<TwitterUser> arc : graph.getGraph().get(vertex)){
                    sb.append(vertex.getLabel());
                    sb.append("," +arc.getVertex().getLabel().getUserHandle());
                    sb.append("," + arc.getWeight());
                    pw.println(sb);
                    sb.setLength(0);
                    pw.flush();
                }
            }
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeGephiUserHashtagFile(DirectedGraph<TwitterUser, Hashtag> graph, File file) throws IOException {
        createFile(file.getParent(), file.getName());
        StringBuilder sb = new StringBuilder();
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            sb.append("nodedef>name VARCHAR,stance VARCHAR,acceptOrReject VARCHAR"/*,label VARCHAR,class VARCHAR, visible BOOLEAN," +
                        "labelvisible BOOLEAN,width DOUBLE,height DOUBLE,x DOUBLE,y DOUBLE,color VARCHAR"*/);
            pw.println(sb);
            sb.setLength(0);
            pw.flush();

            for(Vertex<TwitterUser> vertex: graph.getGraph().keySet()){
                    sb.append(vertex.getLabel().getUserHandle());
                    sb.append(",");

                    sb.append(outputProOrAntiUser(vertex));

                    String toAdd = outputUserDominantProperty(vertex,"accepting","rejecting");
                    sb.append(toAdd);
                    pw.println(sb);
                    sb.setLength(0);
                    pw.flush();
            }



            for(Vertex<TwitterUser> vertex: graph.getGraph().keySet()) {
                for (Arc<Hashtag> arc : graph.getGraph().get(vertex)) {
                    sb.append(arc.getVertex().getLabel() + ",");
                    if (arc.getVertex().getLabel().hasStance()) {
                        if (vertex.getLabel().getStance() < 0)
                            sb.append("anti");
                        else if (vertex.getLabel().getStance() > 0) {
                            sb.append("pro");
                        }
                    } else {
                        sb.append("neutral");
                    }
                    int acceptance = 0;
                    int rejection = 0;
                    for (int i = 0; i < arc.getVertex().getLabel().getQualities().size(); i++) {
                        if (vertex.getLabel().getQualities().get(i).equals("accepting")) {
                            acceptance++;
                        } else if (arc.getVertex().getLabel().getQualities().get(i).equals("rejecting")) {
                            rejection++;
                        }
                    }
                    if (acceptance > rejection) {
                        sb.append(",accepting");
                    } else if (rejection >= acceptance && rejection != 0) {
                        sb.append(",rejecting");
                    } else if (acceptance == 0 && rejection == 0) {
                        sb.append(",neither");
                    }

                    pw.println(sb);
                    sb.setLength(0);
                    pw.flush();
                }
            }



            sb.append("edgedef>node1 VARCHAR,node2 VARCHAR,weight DOUBLE"); //,directed BOOLEAN");
            pw.println(sb);
            sb.setLength(0);
            pw.flush();

            for(Vertex<TwitterUser> vertex: graph.getGraph().keySet()){
                for(Arc<Hashtag> arc : graph.getGraph().get(vertex)){
                    sb.append(vertex.getLabel());
                    sb.append("," +arc.getVertex().getLabel());
                    sb.append("," + arc.getWeight());
                    pw.println(sb);
                    sb.setLength(0);
                    pw.flush();
                }
            }
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String outputUserDominantProperty(Vertex<TwitterUser> vertex, String propertyOne, String propertyTwo) {

        StringBuilder sb = new StringBuilder();

        int countPropOne = 0, countProTwo = 0;
        for (String quality : vertex.getLabel().getQualities()) {
            if (quality.equals(propertyOne)) {
                countPropOne++;
            } else if (quality.equals(propertyTwo)) {
                countProTwo++;
            }
        }
        if (countPropOne > countProTwo) {
            sb.append(",").append(propertyOne);
        } else if (countProTwo > countPropOne) {
            sb.append(",").append(propertyTwo);
        } else
            sb.append(",neither");
        return sb.toString();
    }

    private String outputProOrAntiUser(Vertex<TwitterUser> vertex){

        StringBuilder sb = new StringBuilder();

        if(vertex.getLabel().hasStance()){
            if(vertex.getLabel().getStance()<0)
                sb.append("anti");
            else if(vertex.getLabel().getStance()>0){
                sb.append("pro");
            }
        }else {
            sb.append("neutral");
        }
        return sb.toString();
    }
}
