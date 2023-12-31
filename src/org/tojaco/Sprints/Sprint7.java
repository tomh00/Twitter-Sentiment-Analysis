package org.tojaco.Sprints;

import org.tojaco.*;
import org.tojaco.FileIO.GraphReadWriteService;
import org.tojaco.Graph.*;
import org.tojaco.GraphAnalysis.GraphAnalyser;
import org.tojaco.GraphAnalysis.StanceAnalysis;
import org.tojaco.GraphAnalysis.StatCalculator;
import org.tojaco.GraphElements.GraphElements;
import org.tojaco.GraphElements.Hashtag;
import org.tojaco.GraphElements.TwitterUser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class Sprint7 {

    public void sprint7(File dataFile) throws IOException {

        final ArrayList<String> getRetweets = new ArrayList<>();

        GraphElements graphElements = new GraphElements();

        GraphReadWriteService rfs = new GraphReadWriteService();
        HashtagSummarizer hashtagSummarizer = new HashtagSummarizer<>();

        FindGraphElements<TwitterUser,TwitterUser> findGraphElements = new FindGraphElements<>(new CreateUserVertex(), new CreateUserVertex());

        if (dataFile.exists()) {
            getRetweets.addAll(rfs.loadDataFromInputFile(dataFile));
        }

        final ArrayList<String> getHashtags = new ArrayList<>(rfs.getHashtags());

        DirectedGraph<TwitterUser, TwitterUser> rtGraph = findGraphElements.createGraph(graphElements, getRetweets, 0, 1);
        DirectedGraph<TwitterUser, TwitterUser> retweetedGraph = findGraphElements.createGraph(graphElements, getRetweets, 1, 0);

        rfs.writeFileFromGraph(rtGraph,
                new File(Configuration.getGRAPH_DIRECTORY(),
                        Configuration.getRTGRAPH_OUTPUT_FILE()), true);

        rfs.writeFileFromGraph(retweetedGraph,
                new File(Configuration.getGRAPH_DIRECTORY(),
                        Configuration.getRTWEETEDGRAPH_OUTPUT_FILE()), true);

        System.out.println("Retweet graph / retweeted graph added successfully to Graph directory!");

        System.out.println("Now generating mentions / mentioned graphs. Standby.");
        // calling this will both generate and output the mention / mentioned graphs
        MentionGraph mentionGraph = new MentionGraph(rfs);

        System.out.println("Mentions / Mentioned Graphs added successfully to Graph directory!");

        FindEvangelists findEvangelists = new FindEvangelists();
        Map<Vertex<TwitterUser>, Integer> retweetHashMap = findEvangelists.findTotalRetweets(retweetedGraph);

        AssignStances assignStances = new AssignStances();
        File StanceFile = new File(Configuration.getSTANCE_FILE());
        assignStances.determineProAntiVaxEvangelists(graphElements, rtGraph, StanceFile);

        // initial setup for calculating stances
        GraphAnalyser graphAnalyser = new GraphAnalyser<>();

        for (int i = 0; i < 10; i++) {
            graphAnalyser.assignUserStances(rtGraph);
            graphAnalyser.assignUserStances(retweetedGraph);
        }

        System.out.println("Now calculating hashtag graphs...");
        FindGraphElements<TwitterUser, Hashtag> userHashTagFGE = new FindGraphElements<>(new CreateUserVertex(), new CreateHashtagVertex());
        DirectedGraph<TwitterUser, Hashtag> userToHashTag = userHashTagFGE.createGraph(graphElements, getHashtags, 0, 1);

        rfs.writeFileFromGraph(userToHashTag,
                new File(Configuration.getGRAPH_DIRECTORY(),
                        Configuration.getUSERS_TO_HASHTAGS()), true);

        FindGraphElements<Hashtag,TwitterUser> hashTagUsersFGE = new FindGraphElements<>(new CreateHashtagVertex(), new CreateUserVertex());

        DirectedGraph<Hashtag, TwitterUser> hashtagToUsers = hashTagUsersFGE.createGraph(graphElements, getHashtags, 1, 0);

        rfs.writeFileFromGraph(hashtagToUsers,
                new File(Configuration.getGRAPH_DIRECTORY(),
                        Configuration.getHASHTAGS_TO_USERS()), true);

        //3a and 3b
        for (int i = 0; i < 3; i++) { //theres no change in coverage from 3 to 4, but theres a change in coverage from 2 to 3
            graphAnalyser.assignUserStances(userToHashTag);
            graphAnalyser.assignUserStances(hashtagToUsers);

        }
        //by running this again we get more coverage
        for (int i = 0; i < 5; i++) { //by upping this to 10 there's no change in coverage
            graphAnalyser.assignUserStances(rtGraph);
            graphAnalyser.assignUserStances(retweetedGraph);

        }
        StanceAnalysis analyse = new StanceAnalysis<>();
        analyse.assignStancesByHashtags(hashtagToUsers, graphElements, rtGraph);

        HashtagSplitter hashtagSplitter = new HashtagSplitter<>();
        hashtagSplitter.splitHashtagsByCamelCase(hashtagToUsers);

        Lexicon<String> lexicon = new Lexicon<>();

        DirectedGraph<String,String> lexiconGraph = lexicon.getLexiconGraph();

        DirectedGraph<Hashtag,String> sumHashTagGraph = hashtagSummarizer.summarizeHashtag(hashtagToUsers, lexiconGraph, lexicon.getGraphElementsLexicon());

        hashtagSplitter.splitHashtagsByLexicon(hashtagToUsers, lexicon.getLexiconDictionary());

        rfs.writeFileFromGraph(lexiconGraph, new File(Configuration.getGRAPH_DIRECTORY(), Configuration.getLEXICON_FILE()), false);

        GraphElements hashTagGE = new GraphElements();

        DirectedGraph<Hashtag,String> hashtagToWordGraph = hashtagSummarizer.hashtagMadeUpOf(hashtagToUsers, hashTagGE);

        rfs.writeFileFromGraph(hashtagToWordGraph, new File(Configuration.getGRAPH_DIRECTORY(), Configuration.getHASHTAG_TO_WORDS()), false);

        hashtagSummarizer.assignGistOfTags(sumHashTagGraph);

        rfs.writeFileFromGraph(sumHashTagGraph, new File(Configuration.getGRAPH_DIRECTORY(),
                Configuration.getHASHTAG_SUMMARY_FILE()), true);


        System.out.println("Now calculating User to Qualities graph...");

        ModelUser modelUser = new ModelUser();
        modelUser.addSummaryOfHashtag(sumHashTagGraph);
        modelUser.addSummaryOfHashtagToUserQualities(userToHashTag);

        DirectedGraph<TwitterUser,String> usersToQualities = modelUser.makeUserToQualityGraph(userToHashTag, graphElements);

        rfs.writeFileFromGraph(usersToQualities, new File(Configuration.getGRAPH_DIRECTORY(),
                Configuration.getUSER_QUALITIES()), true);

        System.out.println("User to Qualities graph added successfully to Graph directory!");
        StatCalculator statCalculator = new StatCalculator(usersToQualities);

        System.out.println("Now automating collection of significant conditional probabilities");
        statCalculator.automateConditionalProbCalculation(lexicon);
        statCalculator.outputSignificantConditionalProbabilities();
    }
}
