package application.indexer;

import application.utils.TokenizerHandler;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class IndexerHandler {


    public List<String> stemming(String str) {
        return TokenizerHandler.stem(str);
    }

    public List<String> stop(String str) {
        return TokenizerHandler.stopWords(str);
    }
    public static List<String> tokenizer(String str){ return TokenizerHandler.tokenize((str));}


    // Test
    public static void main(String[] args) {
        IndexerHandler indexerHandler = new IndexerHandler();
        //Test 1
        String str = "The analysising of 25 indexing algorithms has not produced consistent retrieval performance. The best indexing technique for retrieving documents is not known";

        List<String> stop = indexerHandler.stop(str);
        List<String> stemming = indexerHandler.stemming(str);

        System.out.println("Result of stop words:" + stop + "\n");
        System.out.println("Result of stop stemming:" + stemming +"\n");

        List<String> tokenize = tokenizer(str);
        System.out.println("Result of tokenizer:" + tokenize +"\n");

        //Test 2
        String filePath = "D:\\HKUST23fall\\S2\\CSIT5930 Search Engine and Application\\SE_project\\test.txt";
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            List<String> tokens = tokenizer(content);
            System.out.println("Tokens from file: " + tokens +"\n");
        } catch (Exception e) {
            System.err.println("Error reading from file: " + e.getMessage());
        }

    }

}
