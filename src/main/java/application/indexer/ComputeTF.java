package application.indexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class ComputeTF {

    public static HashMap<String, List<Integer>> calculateWordPositions(List<String> words) {
        HashMap<String, List<Integer>> wordPositions = new HashMap<>();
        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i);
            wordPositions.putIfAbsent(word, new ArrayList<>());
            wordPositions.get(word).add(i);
        }
        return wordPositions;
    }

    public static int getMaxTF(HashMap<String, List<Integer>> wordPositions) {
        int maxTF = 0;
        for (List<Integer> positions : wordPositions.values()) {
            maxTF = Math.max(maxTF, positions.size());
        }
        return maxTF;
    }

    // All counts of words
    public static HashMap<String, Integer> getKeywordFreqMap(HashMap<String, List<Integer>> wordPositions) {
        HashMap<String, Integer> keywordFreqMap = new HashMap<>();
        for (Map.Entry<String, List<Integer>> entry : wordPositions.entrySet()) {
            keywordFreqMap.put(entry.getKey(), entry.getValue().size());
        }
        return keywordFreqMap;
    }

//    public static void main(String[] args) {
//        String filePath = "D:\\HKUST23fall\\S2\\CSIT5930 Search Engine and Application\\SE_project\\test.txt"; // Replace with your actual file path
//
//        // get MaxTF
//        try {
//            // Read the content of the text file
//            String content = new String(Files.readAllBytes(Paths.get(filePath)));
//
//            // Simple word splitting by spaces (this could be improved to handle punctuation)
//            List<String> words = Arrays.asList(content.split("\\s+"));
//
//            // Calculate word positions
//            HashMap<String, List<Integer>> wordPositions = calculateWordPositions(words);
//
//            // Get and print the maximum TF
//            int maxTF = getMaxTF(wordPositions);
//            System.out.println("Maximum TF: " + maxTF);
//
//        } catch (IOException e) {
//            System.err.println("Error reading from file: " + e.getMessage());
//            e.printStackTrace();
//        }
//
//        // weight for word
//        try {
//            String content = new String(Files.readAllBytes(Paths.get(filePath)));
//            List<String> words = Arrays.asList(content.split("\\P{L}+"));
//            HashMap<String, List<Integer>> wordPositions = calculateWordPositions(words);
//            HashMap<String, Integer> keywordFreqMap = getKeywordFreqMap(wordPositions);
//
//            System.out.println("Word Frequencies:");
//            keywordFreqMap.forEach((word, count) -> System.out.println(word + ": " + count));
//        } catch (Exception e) {
//            System.err.println("Error reading file: " + e.getMessage());
//            e.printStackTrace();
//        }
//
//    }
}

