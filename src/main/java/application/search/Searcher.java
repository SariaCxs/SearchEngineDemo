package application.search;

import application.indexer.IndexerController;
import application.model.Posting;
import application.model.SearchResult;
import application.model.Webpage;
import application.utils.Porter;
import application.utils.TokenizerHandler;
import application.spider.Spider;

import java.io.IOException;
import java.util.*;

public class Searcher {
    public int MAX_OUTPUT_NUM = 50;
    public double TITLE_BOOST = 1.5;
    IndexerController indexerController;
    public Porter porter = new Porter();

    public Searcher(IndexerController indexerController) {
        this.indexerController = indexerController;
    }

    public List<SearchResult> search(String query) {
        //tokenize the query
        List<String> queryTokens = TokenizerHandler.tokenize(query);
        //calculateWeights of querytoken in each page: Map<pageId, Map<String, tfxidf/tfmax>>
        Map<Integer, Map<String, Double>> documentVectors = calculateWeights(queryTokens);
        //get all title matched pages: Set<Integer>
        Set<Integer> titleMatched = PagesMatchedInTitle(queryTokens);
        //Calculate ranking score: similarity score + title matched score, return sorted list of pageid and score
        List<HashMap.SimpleEntry<Integer, Double>> topPages = getTopPages(queryTokens, documentVectors, titleMatched);
        //Build return search result to frontend
        return processResults(queryTokens,topPages);
    }

    public Set<Integer> PagesMatchedInTitle(List<String> querytokens) {
        Set<Integer> matchedpages = new HashSet<>();
        for (String token : querytokens) {
            if (!token.contains(" ")) {
                // if the token is single word
                // change token to wordID
                int wordID = indexerController.getWordIdByWordSearch(token);
                // find all pages, whose title contains the word
                Set<Posting> postings = indexerController.invertedIndexer.getPostingTitle(wordID);
                if (postings != null) {
                    for (Posting posting : postings) {
                        matchedpages.add(posting.getDocId());
                    }
                }

            } else {
                // the token is a phrase
                List<String> words = TokenizerHandler.tokenize(token);
                // def the common matched page for all words in the phrase, with hashset
                HashSet<Integer> CommonMatchedPages = new HashSet<>();
                // find all pages contain every words appears in the query token, but this does not mean words form phrases in title
                for (int i = 0; i < words.size(); i++) {
                    // change token to wordID
                    int wordID = indexerController.getWordIdByWordSearch(words.get(i));
                    if (wordID == -1) {
                        break;
                    }
                    // find all pages, whose title contains the words[i]
                    Set<Posting> postings = indexerController.invertedIndexer.getPostingTitle(wordID);
                    // find the common pages contain all words[i]
                    findcommonpageID(CommonMatchedPages, postings);
                }
                // find the effective page, which means the words appears in order and form a phrase
                for (Integer page : CommonMatchedPages) {
                    int validphrase = checkPhraseForm(words, page);
                    if (validphrase > 0) {
                        matchedpages.add(page);
                    }

                }
            }
        }
        return matchedpages;
    }

    public Integer checkPhraseForm(List<String> words, int pageiD) {
        // find whether the later words ever appear in the right place
        int validPhraseNum = 0;
        List<Integer> wordPositionInThisPage = indexerController.getWordPositionInThisPage(words.get(0), pageiD);
        for (int i = 0; i < wordPositionInThisPage.size(); i++) {
            // loop every pos this word has shown in
            int pos = wordPositionInThisPage.get(i);
            boolean formAPhrase = true;
            for (int j = 1; j < words.size(); j++) {
                List<Integer> positionOfJInThisPage = indexerController.getWordPositionInThisPage(words.get(j), pageiD);
                if (!positionOfJInThisPage.contains(pos + j)) {
                    formAPhrase = false;
                    break;
                }
                pos++;
            }
            if (formAPhrase) {
                validPhraseNum++;
            }
        }
        return validPhraseNum;
    }

    public void findcommonpageID(HashSet<Integer> CommonMatchedPages, Set<Posting> postings) {
        if (postings != null) {
            Set<Integer> pageIDs = new HashSet<>();
            for (Posting page : postings) {
                pageIDs.add(page.getDocId());
            }
            if (CommonMatchedPages.isEmpty()) {
                CommonMatchedPages.addAll(pageIDs);
            } else {
                CommonMatchedPages.retainAll(pageIDs);
            }
        }
    }


    public Map<Integer, Map<String, Double>> calculateWeights(List<String> queryTokens) {
        Map<Integer, Map<String, Double>> documentVectors = new HashMap<>();
        for (String token : queryTokens) {
            //process phrases
            if (token.contains(" ")) {
                List<String> words = TokenizerHandler.tokenize(token);
                //find all page that contains all the tokens - > a set of page id
                HashSet<Integer> intersectPages = findIntersection(words);
                //get posting of each word with the page id
                //use positions in posting list to check whether a valid phrase
                if (!intersectPages.isEmpty()) {
                    HashMap<Integer, Integer> pageIdTF = buildPostingForPhrase(words, intersectPages);
                    Compute.calculateTfIdf(token, pageIdTF, documentVectors);
                }
            } else {
                //get posting lists
                Map<Integer, Integer> postings = getPostingList(token);
                for(Integer pageId:postings.keySet()){
                    Map<String ,Double> bodyWeights = indexerController.forwardIndexer.getBodyKeywordWights(pageId);
                    documentVectors.computeIfAbsent(pageId, k -> new HashMap<>()).put(token,bodyWeights.get(token));
                }
            }
        }
        return documentVectors;
    }

    public HashSet<Integer> findIntersection(List<String> words) {
        HashSet<Integer> results = new HashSet<>();
        for (String word : words) {
            int wordId = indexerController.getWordIdByWordSearch(word);
            Set<Posting> postings = indexerController.invertedIndexer.getPostingBody(wordId);
            if (postings != null) {
                Set<Integer> pageIds = new HashSet<>();
                for (Posting posting : postings) {
                    pageIds.add(posting.getDocId());
                }
                if (results.isEmpty()) {
                    results.addAll(pageIds);
                } else {
                    results.retainAll(pageIds);
                }
                if (results.isEmpty()) {
                    return new HashSet<>();
                }
            } else {
                return new HashSet<>();
            }
        }
        return results;
    }

    //pageIDTF
    public HashMap<Integer, Integer> buildPostingForPhrase(List<String> words, HashSet<Integer> pageIds) {
        HashMap<Integer, Integer> postings = new HashMap<>();

        for (Integer pageId : pageIds) {

            //get keywordlist of the pageId
            Map<String, List<Integer>> positions = indexerController.forwardIndexer.getBodyKeywordList(pageId);
//            for (Map.Entry<String, List<Integer>> obj : positions.entrySet()) {
//                System.out.println(obj.getKey());
//                System.out.println(obj.getValue());
//            }
            List<Integer> positionsOfFirst = positions.get(words.get(0));
            int phraseCount = 0;
            //input the position of previous word, the next word
            for (Integer pos : positionsOfFirst) {
                int curPos = pos;
                boolean isphrase = true;
                for (int i = 1; i < words.size(); i++) {
                    if (isNextword(curPos, words.get(i), pageId)) {
                        curPos += 1;
                    } else {
                        isphrase = false;
                        break;
                    }
                }
                if (isphrase) {
                    phraseCount += 1;
                }
            }
            if (phraseCount != 0) {
                postings.put(pageId, phraseCount);
            }
        }
        return postings;
    }

    //check whether the next positions contains the next word
    public Boolean isNextword(int previousPos, String nextWord, int pageId) {
        Map<String, List<Integer>> positions = indexerController.forwardIndexer.getBodyKeywordList(pageId);
        List<Integer> postions = positions.get(nextWord);
        return postions.contains(previousPos + 1);
    }


    public Map<Integer, Integer> getPostingList(String token) {
        Map<Integer, Integer> results = new HashMap<>();
        int wordId = indexerController.getWordIdByWordSearch(token);
        if (wordId != -1) {
            Set<Posting> postings = indexerController.invertedIndexer.getPostingBody(wordId);
            if (postings != null) {
                for (Posting posting : postings) {
                    int pageId = posting.getDocId();
                    int frequency = posting.getFrequency();
                    results.put(pageId, frequency);
                }
                return results;
            }
        }
        return null;
    }

    public HashMap<Integer, Integer> getPageIdAndTF(Map<Integer, Integer> postings) {
        HashMap<Integer, Integer> pageIdTF = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : postings.entrySet()) {
            int pageId = entry.getKey();
            int frequency = entry.getValue();
            pageIdTF.put(pageId, frequency);
        }
        return pageIdTF;
    }

    public List<HashMap.SimpleEntry<Integer, Double>> getTopPages(List<String> queryTokens, Map<Integer, Map<String, Double>> documentVectors, Set<Integer> titleMatched) {
        PriorityQueue<HashMap.SimpleEntry<Integer, Double>> ranking = new PriorityQueue<>(MAX_OUTPUT_NUM,
                (a, b) -> Double.compare(a.getValue(), b.getValue())
        );
//        double minMatchingScore = Double.MAX_VALUE;
        Set<Integer> visitedId = new HashSet<>();
        for (Map.Entry<Integer, Map<String, Double>> entry : documentVectors.entrySet()) {
            int pageId = entry.getKey();
            System.out.println(pageId);
            System.out.println(indexerController.getWebpageById(pageId).getUrl());

            visitedId.add(pageId);
            Map<String, Double> weights = entry.getValue();
            System.out.println(weights);
//            Map<String ,Double> bodyWeights = indexerController.forwardIndexer.getBodyKeywordWights(pageId);
            double cosineSimilarity = Compute.calculateCosineSimilarity(queryTokens, weights);
            double pageRankValue = indexerController.getPageRankValue(pageId);
            double score = (0.3 * cosineSimilarity + 0.7 * pageRankValue + 1);
//            System.out.println(cosineSimilarity);
//            System.out.println(pageRankValue);
            if (titleMatched.contains(pageId)) {
                score *= TITLE_BOOST;
            }
            AbstractMap.SimpleEntry<Integer, Double> newEntry = new HashMap.SimpleEntry<>(pageId, score);
            ranking.add(newEntry);
            if (ranking.size() > MAX_OUTPUT_NUM) {
                ranking.poll();
            }

        }
        for (Integer pageId : titleMatched) {
            if (visitedId.contains(pageId)) {
                continue;
            }
            double score = TITLE_BOOST;
            AbstractMap.SimpleEntry<Integer, Double> newEntry = new HashMap.SimpleEntry<>(pageId, score);
            ranking.add(newEntry);
            if (ranking.size() > MAX_OUTPUT_NUM) {
                ranking.poll();
            }
        }
        //convert priorityqueue to list
        List<HashMap.SimpleEntry<Integer, Double>> rankingList = new ArrayList<>();
        while (!ranking.isEmpty()) {
            rankingList.add(ranking.poll());
        }
        rankingList.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()));
        return rankingList;
    }

    public String stemLowerCase(String text) {
        return porter.stripAffixes(text.toLowerCase());
    }


    public String getSummary(List<String> query, Webpage webpage) {
        //process phrase cases
        List<String> queryTokens = new ArrayList<>();
        for(String s: query){
            if (s.contains(" ")){
                List<String> newS = TokenizerHandler.tokenize(s);
                queryTokens.addAll(newS);
            }
            else{
                queryTokens.add(s);
            }
        }
        int WINDOW_SIZE = 25;
        String text = webpage.getText();
//        List<String> textToken = TokenizerHandler.tokenize(text);
        String[] textToken = text.split("\\s+");
        System.out.println(webpage.getUrl());
//        for(String s:textToken){
//            System.out.println(s);
//        }
        int textLen = textToken.length;
        int startIdx = 0;
        int endIdx = 0;
        int end = Math.min(textLen, WINDOW_SIZE);
        int maxMatch = 0, currMatch = 0;
        boolean[] isMatch = new boolean[end];

        for (int i = 0; i < textLen; i++) {
            if (i < end) {
                if (queryTokens.contains(stemLowerCase(textToken[i]))) {
                    currMatch += 1;
                    isMatch[i] = true;
                }
            } else {
                int pos = i % end;
                //reset ismatch
                if (isMatch[pos]) {
                    currMatch -= 1;
                    isMatch[pos] = false;
                }
                if (queryTokens.contains(stemLowerCase(textToken[i]))) {
                    currMatch += 1;
                    isMatch[pos] = true;
                }

            }
            if (currMatch > maxMatch) {
                maxMatch = currMatch;
                if (maxMatch == 1) {
                    startIdx = i;
                    endIdx = i + WINDOW_SIZE;
                } else {
                    startIdx = (i >= end) ? i - WINDOW_SIZE: 0;
                    endIdx = i;
                }
            }

        }
        if(endIdx == 0){
            return "[no query term in the text]";
        }
        //return summary
        StringBuilder summaryHTMLSb = new StringBuilder();
        for (int pos = startIdx; pos <= Math.min(textLen - 1, endIdx); ++pos) {
            if (queryTokens.contains(stemLowerCase(textToken[pos]))) {
                summaryHTMLSb.append("<b>");
                summaryHTMLSb.append(textToken[pos]);
                summaryHTMLSb.append("</b> ");
            } else {
                summaryHTMLSb.append(textToken[pos]);
                summaryHTMLSb.append(" ");
            }
        }

        if (endIdx < textLen - 1) {
            summaryHTMLSb.append("...");
        }
        return summaryHTMLSb.toString();
    }



    private List<SearchResult> processResults(List<String> queryTokens, List<HashMap.SimpleEntry<Integer,Double>> topPages) {
        List<SearchResult> results = new ArrayList<>();
        for(HashMap.SimpleEntry<Integer,Double> pageEntry: topPages){
            SearchResult searchResult = new SearchResult();
            int pageId = pageEntry.getKey();
            Webpage webpage= indexerController.getWebpageById(pageId);
            searchResult.setTitle(webpage.getTitle());
            searchResult.setUrl(webpage.getUrl());
            searchResult.setSize(webpage.getPageSize());
            searchResult.setLastModifiedDate(webpage.getLastModifiedDate());
            searchResult.setTop5Keywords(indexerController.getTop5Keywords(pageId));
            searchResult.setChildLinks(indexerController.getChildLinksByPageId(pageId));
            searchResult.setParentLinks(indexerController.getParentLinksByPageId(pageId));
            searchResult.setScore(String.format("%.4f", pageEntry.getValue()));
            String summary = getSummary(queryTokens, webpage);
//            System.out.println("summary "+summary);
            searchResult.setSummary(summary);
            results.add(searchResult);
        }
        return results;
    }


    public static void main(String[] args) throws IOException {
        //test searcher
        IndexerController indexerController1 = Spider.indexer;

        Searcher searchController = new Searcher(indexerController1);
        List<SearchResult> searchResults = searchController.search("movie");
        for(SearchResult searchResult:searchResults){
            System.out.println(searchResult);
        }
        indexerController1.close();
    }

}
