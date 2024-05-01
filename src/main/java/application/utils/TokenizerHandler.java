package application.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.List;

/**
 * Tokenizer
 */
public class TokenizerHandler {
    private static String rootDir = "C:\\Users\\User\\IdeaProjects\\SearchEngineDemo\\";
    private static StopStem stopStem = new StopStem(rootDir+"stopwords.txt");

    /**
     * Including phrase identification, token stemming and stop words removing
     *
     * @param queryString user input query string
     * @return a list of query tokens
     */
    public static List<String> tokenize(String queryString) {
        List<String> queryTokens = new ArrayList<>();

        // Convert queryString to lowercase
        queryString = queryString.toLowerCase();

        // Regular expression pattern to match words and phrases in double quotes
        Pattern pattern = Pattern.compile("\"[^\"]+\"|\\S+");
        Matcher matcher = pattern.matcher(queryString);

        while (matcher.find()) {
            String token = matcher.group();
            if (token.startsWith("\"") && token.endsWith("\"")) {
                // Remove double quotes from phrases
                token = token.substring(1, token.length() - 1);
                queryTokens.add(token);
            } else {
                // Remove stop words
                if (!stopStem.isStopWord(token)) {
                    // Perform stemming
                    System.out.println("not stopwords "+token);
                    token = stopStem.stem(token);
                    queryTokens.add(token);
                }
            }
        }

        return queryTokens;
    }

    public static String tokenizeSingle(String queryString) {
        // Convert queryString to lowercase
        queryString = queryString.toLowerCase();

        String token = null;
        // Remove stop words
        if (!stopStem.isStopWord(queryString)) {
            // Perform stemming
            token = stopStem.stem(queryString);
        }
        return token;
    }

    public static List<String> stopWords(String queryString){
        List<String> stringList = split(queryString);
        return stringList.stream().filter(s -> !stopStem.isStopWord(s)).toList();
    }

    public static List<String> stem(String queryString){
        List<String> stringList = split(queryString);
        return stringList.stream().map(s -> stopStem.stem(s)).toList();
    }

    private static List<String> split(String queryString) {
        List<String> queryTokens = new ArrayList<>();

        // Convert queryString to lowercase
        queryString = queryString.toLowerCase();

        // Regular expression pattern to match words and phrases in double quotes
        Pattern pattern = Pattern.compile("\"[^\"]+\"|\\S+");
        Matcher matcher = pattern.matcher(queryString);

        while (matcher.find()) {
            String token = matcher.group();
            queryTokens.add(token);
        }
        return queryTokens;
    }

    // Test
    public static void main(String[] args) {
//        System.out.println(split("The analysising of 25 indexing algorithms has not produced consistent retrieval performance. The best indexing technique for retrieving documents is not known"));
//        System.out.println(stem("The analysising of 25 indexing algorithms has not produced consistent retrieval performance. The best indexing technique for retrieving documents is not known"));
//        System.out.println(tokenize("The analysising of 25 indexing algorithms has not produced consistent retrieval performance. The best indexing technique for retrieving documents is not known"));

//        String filePath = "D:\\HKUST23fall\\S2\\CSIT5930 Search Engine and Application\\SE_project\\test.txt"; // Replace with your file path
//        try {
//            String content = new String(Files.readAllBytes(Paths.get(filePath)));
//            List<String> tokens = tokenize(content);
//            System.out.println("Tokens from file: " + tokens);
//        } catch (Exception e) {
//            System.err.println("Error reading from file: " + e.getMessage());
//        }
    }


}
