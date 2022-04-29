package org.tojaco.GraphAnalysis;

import org.tojaco.Graph.Arc;
import org.tojaco.Graph.DirectedGraph;
import org.tojaco.Graph.Vertex;
import org.tojaco.GraphElements.TwitterUser;
import org.tojaco.Lexicon;

import java.util.*;
import twitter4j.Twitter;

import java.util.*;

public class StatCalculator {
    private final DirectedGraph<TwitterUser, String> userModel;
    private List<TwitterUser> usersList;
    private List<TwitterUser> property1Users;
    private List<TwitterUser> notProperty1Users;

    public StatCalculator(DirectedGraph<TwitterUser, String> userModel){
        this.userModel = userModel;
        usersList = new ArrayList<>();
        property1Users = new ArrayList<>();
        notProperty1Users = new ArrayList<>();
        for (Vertex<TwitterUser> user : userModel.getGraph().keySet()){
            usersList.add(user.getLabel());
        }
    }

    private List<TwitterUser> getProportionList(List<TwitterUser> totalSet, String subsetCondition){
        List<TwitterUser> subset = new ArrayList<>();
        for( TwitterUser user : totalSet ){
            Vertex<TwitterUser> vertex = userModel.getAllVerticesInGraph().get(user.getUserHandle());
            for( Arc<String> arc : userModel.getGraph().get(vertex) ){
                if ( arc.getVertex().getLabel().equals(subsetCondition) ){
                    subset.add(vertex.getLabel());
                    break;
                }
            }
        }

        return subset;
    }

    private void fillPropertyLists( String property ){
        for( TwitterUser user : usersList ) {
            Vertex<TwitterUser> vertex = userModel.getAllVerticesInGraph().get(user.getUserHandle());
            for (String arc : vertex.getLabel().getQualities()) {
                if (arc.equals(property)) {
                    property1Users.add(vertex.getLabel());
                    break;
                }
                else{
                    notProperty1Users.add(vertex.getLabel());
                }
            }
        }

//        for( TwitterUser user : usersList ){
//            if ( user.getStance() < 0 ){
//                antiUsers.add(user);
//            }
//            else if( user.getStance() > 0 ){
//                proUsers.add(user);
//            }
//        }
    }

    private Double calculateAntiStancesProportion( List<TwitterUser> usersSample ){
        double subset = 0.0;
        for ( TwitterUser user : usersSample ){
            if( user.getStance() < 0 ){
                subset++;
            }
        }
        return subset/usersSample.size();
    }

    private List<Double> calculateMeanOfRandomSamplesOfSizeM(int m, String property1 ) {
        List<Double> probabilities = new ArrayList<>();

        Random generator = new Random();
        Object[] values = usersList.toArray();
        for(int i = 0; i < 100; i ++) {
            List<TwitterUser> sampleUsers = new ArrayList<>();
            for (int j = 0; j < m; j++) {
                Object randomValue = values[generator.nextInt(values.length)];
                sampleUsers.add((TwitterUser)randomValue);
            }
            List<TwitterUser> usersWithProperty1 = getProportionList(sampleUsers, property1);
            probabilities.add( (double) usersWithProperty1.size() / usersWithProperty1.size() );
        }
        return probabilities;
    }

    private Double calculateDoubleMean(List<Double> list){
        Double total = 0.0;
        for ( Double curr : list ){
            total += curr;
        }

        return total/list.size();
    }

    // standard deviation calculation:

    private Double calculateSD(List<Double> points, double μ){
        // calculate sum of squared differences
        double sumOfSquaredDiffs = 0.0;
        for ( Double point : points ) {
            double diff = point - μ ;
            double squaredDiff = diff * diff;
            sumOfSquaredDiffs += squaredDiff;
        }
        return Math.sqrt(sumOfSquaredDiffs / points.size() );
    }

    public Double calculateZScore( String property1, String property2 ){
        List<TwitterUser> m;
        List<Double> sampleMeans;
        double mew, sD;
//        if( !positivity ){
        fillPropertyLists(property1);
        m = getProportionList(usersList, property2);
        sampleMeans = calculateMeanOfRandomSamplesOfSizeM(m.size(), property1);
        mew = calculateDoubleMean(sampleMeans);
        sD = calculateSD(sampleMeans, mew);
        double difference = calculateAntiStancesProportion(m) - mew;
        return sD/difference;
        //}
    }

    public double calConditionalProbabilityWithProps(String prop1, String prop2) {

        double probUserWithPropOne = getProportionList(usersList, prop1).size() / (double) usersList.size();

        double probUserWithPropTwo = getProportionList(usersList, prop2).size() / (double) usersList.size();

        double probUserWithBothProps = probUserWithPropOne * probUserWithPropTwo;

        return probUserWithBothProps / probUserWithPropTwo;
    }

    public void automateConditionalProbCalculation(Lexicon<String> lexicon) {

        // TODO ignore Z-Score of < 2

        HashMap<String, String> featureOppositeQualities = lexicon.getOppositeQualities();

        for (Map.Entry<String, String> featureMapping : featureOppositeQualities.entrySet()) {
            calConditionalProbabilityWithProps(featureMapping.getKey(), featureMapping.getValue());
        }
    }
}


