package org.tojaco.GraphAnalysis;

import org.tojaco.Graph.Arc;
import org.tojaco.Graph.DirectedGraph;
import org.tojaco.Graph.Vertex;
import org.tojaco.Graph.VertexCreator;
import org.tojaco.GraphElements.Hashtag;
import org.tojaco.GraphElements.TwitterUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatCalculator {
    DirectedGraph<TwitterUser, String> userModel;
    DirectedGraph<Hashtag, String> hashtagSummaries;

    StatCalculator(DirectedGraph<TwitterUser, String> userModel, DirectedGraph<Hashtag, String> hashtagSummaries){
        this.userModel = userModel;
        this.hashtagSummaries = hashtagSummaries;
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

    private Double calculateAntiStancesProportion( List<TwitterUser> usersSample ){
        double subset = 0.0;
        for ( TwitterUser user : usersSample ){
            if( user.getStance() < 0 ){
                subset++;
            }
        }
        return subset/usersSample.size();
    }

    private Double calculateMeanOfRandomSamplesOfSizeM(int m ) {
        List<Double> probabilities = new ArrayList<>();

        for(int i = 0; i < 100; i ++) {
            List<TwitterUser> sampleUsers = new ArrayList<>();
            for (Vertex<TwitterUser> vertex : userModel.getGraph().keySet()) {
                sampleUsers.add(vertex.getLabel());

            }
        }
        return 0.0;
    }

    public double calculateConditionalProbability(DirectedGraph<TwitterUser, String> userModel, String prop1, String prop2) {
        double propOneGivenPropTwo;

        int totalPropOne = 0;
        int totalPropTwo = 0;
        int totalProp = 0;

        for (Map.Entry<Vertex<TwitterUser>, ArrayList<Arc<String>>> user : userModel.getGraph().entrySet()) {

            totalProp += user.getValue().size();

            for (Arc<String> arc : user.getValue()) {

                if (arc.toString().contains(prop1)) {
                    totalPropOne++;
                }

                if (arc.toString().contains(prop2)) {
                    totalPropTwo++;
                }
            }
        }

        return 0.00;
    }

}
