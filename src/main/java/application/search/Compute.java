package application.search;

import application.indexer.IndexerController;
import application.indexer.ForwardIndexer;

import java.util.HashMap;
import java.util.Map;
import java.util.List;


public class Compute {

    // tfidf score of each token
    // pageIdToTf : reverted index <pageIdOfTheToken,FrequencyInThisPage>
    public static void calculateTfIdf(String token, HashMap<Integer,Integer> pageIdToTf, Map<Integer, Map<String, Double>> documentVectors) {
        for(Map.Entry<Integer,Integer> entry:pageIdToTf.entrySet()){
            int pageId = entry.getKey();
            int tfMax = ForwardIndexer.getMaxTFById(pageId);
            double tf = entry.getValue();
            double df = pageIdToTf.size();
            int N = IndexerController.getPageCount();
            double tfIdf = (tf/tfMax) * (Math.log(N/df) / Math.log(2.0));
            documentVectors.computeIfAbsent(pageId, k -> new HashMap<>()).put(token,tfIdf);
        }
    }


    public static double calculateCosineSimilarity(List<String> queryKeywords, Map<String, Double> documentVector,  Map<String ,Double> bodyWeights) {
        double queryMagnitude = Math.sqrt(queryKeywords.size());
        double documentMagnitude = 0.0;
        double dotProduct = 0.0;

        for(String token: bodyWeights.keySet()){
            double weights = bodyWeights.get(token);
            documentMagnitude += weights * weights;
        }
        for(String token: queryKeywords){
            if(documentVector.keySet().contains(token)) {
                dotProduct += documentVector.get(token);
            }
        }
        documentMagnitude = Math.sqrt(documentMagnitude);
        return dotProduct / (queryMagnitude * documentMagnitude);
    }

    //String TF idf



}
