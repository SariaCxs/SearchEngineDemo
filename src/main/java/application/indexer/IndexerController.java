package application.indexer;

import application.model.Posting;
import application.model.Webpage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.*;
import java.util.List;
import java.util.Map;

public class IndexerController {

    public  int wordCount = 0;
    private final String idDB = "IdDB";

    public static final String WORD_TO_WORD_ID = "word_to_word_id";
    public static final String URL_TO_PAGE_ID = "url_to_page_id";
    public static final String PAGE_ID_TO_WEBPAGE = "page_id_to_web_page";
    public static final String COUNT_INFO = "count_info";
    public static final String WEBPAGE_TO_PAGE_RANK_VALUE = "webpage_to_pagerank_value";

    public ForwardIndexer forwardIndexer = new ForwardIndexer();
    public LinkIndexer linkIndexer = new LinkIndexer();
    public InvertedIndexer invertedIndexer = new InvertedIndexer();
    public static IndexDB indexDB;

    public IndexerController() {
        ArrayList<String> indexNames = new ArrayList<>();
        indexNames.add(WORD_TO_WORD_ID);
        indexNames.add(URL_TO_PAGE_ID);
        indexNames.add(PAGE_ID_TO_WEBPAGE);
        indexNames.add(COUNT_INFO);
        indexNames.add(WEBPAGE_TO_PAGE_RANK_VALUE);
        indexDB = new IndexDB(idDB, indexNames);

    }

    public int getPageIdByUrl(String url) {
        Integer pageId = (Integer) indexDB.getEntry(URL_TO_PAGE_ID,url);
        if(pageId == null){
            int newPageId = getPageCount();
            updatedPageCount(1);
            indexDB.addEntry(URL_TO_PAGE_ID,url,newPageId);
            return newPageId;
        }else {
            return pageId;
        }
    }

    public int getWordIdByWord(String word){
        Integer wordId = (Integer) indexDB.getEntry(WORD_TO_WORD_ID, word);
        if (wordId == null){
            int newWordId = wordCount;
            wordCount += 1;
            indexDB.addEntry(WORD_TO_WORD_ID,word, newWordId);
            return newWordId;
        }else{
            return wordId;
        }
    }

    public Webpage getWebpageByURL(String URL){
        int pageId = getPageIdByUrl(URL);
        return getWebpageById(pageId);
    }

    public void indexPage(Webpage webpage){
        String pageBody = webpage.getText();
        String title = webpage.getTitle();
        //stemming and stopword removing
        List<String> pageBodyWords = IndexerHandler.tokenizer(pageBody);
        List<String> pageTitleWords = IndexerHandler.tokenizer(title);
        //get word positions in this page for all words
        HashMap<String, List<Integer>> wordPositionsInBody =  ComputeTF.calculateWordPositions(pageBodyWords);
        HashMap<String, List<Integer>> wordPositionsInTitle = ComputeTF.calculateWordPositions(pageTitleWords);
        //get the max tf of this page
        int maxTf = ComputeTF.getMaxTF(wordPositionsInBody);
        HashMap<Integer, List<Integer>> wordIdPositionInBody = word2wordID(wordPositionsInBody);
        HashMap<Integer, List<Integer>> wordIdPositionInTitle = word2wordID(wordPositionsInTitle);
        //generate pageId
        int pageId = getPageIdByUrl(webpage.getUrl());
        indexDB.addEntry(PAGE_ID_TO_WEBPAGE,pageId,webpage);
        //generate forward index for webpage:
        forwardIndexer.addMaxTF(pageId, maxTf);
        forwardIndexer.addKeywordListBody(pageId, wordPositionsInBody);
        forwardIndexer.addKeywordListTitle(pageId,wordPositionsInTitle);
        //generate body inverted index
        addInvertIndex(pageId, wordIdPositionInBody, InvertedIndexer.WORD_ID_TO_POSTING_BODY);
        //generate title inverted index
        addInvertIndex(pageId, wordIdPositionInTitle, InvertedIndexer.WORD_ID_TO_POSTING_TITLE);
    }

    public HashMap<Integer, List<Integer>> word2wordID(HashMap<String, List<Integer>> wordPositions){
        HashMap<Integer, List<Integer>> wordIdPostions = new HashMap<>();
        for (Map.Entry<String, List<Integer>> entry : wordPositions.entrySet()) {
            String word = entry.getKey();
            int wordId = getWordIdByWord(word);
            wordIdPostions.put(wordId, entry.getValue());
        }
        return wordIdPostions;
    }

    public void addInvertIndex(int pageId, HashMap<Integer, List<Integer>> wordIdPositionInBody, String type){
        for (Map.Entry<Integer, List<Integer>> entry : wordIdPositionInBody.entrySet()) {
            int wordId = entry.getKey();
            List<Integer> positions = entry.getValue();
            Posting posting = new Posting(pageId, positions.size(), positions);
            if (type.equals( InvertedIndexer.WORD_ID_TO_POSTING_BODY)){
                invertedIndexer.indexWebPageBody(wordId,posting);
            }else{
                invertedIndexer.indexWebPageTitle(wordId,posting);
            }

        }
    }


    public void updateWebpage(Webpage webpage){
        String url = webpage.getUrl();
        int pageId = getPageIdByUrl(url);
        deleteWebpage(pageId);
        indexPage(webpage);
    }
    public void deleteWebpage(int pageId){
        //delete the invertedIndex of body
        HashMap<String, List<Integer>> keywordsInBody = forwardIndexer.getBodyKeywordList(pageId);
        for (Map.Entry<String, List<Integer>> entry : keywordsInBody.entrySet()){
            String word = entry.getKey();
            int wordId = getWordIdByWord(word);
            Set<Posting> postingList = (Set<Posting>) invertedIndexer.getPostingBody(wordId);
            postingList.removeIf(p -> p.getDocId() == pageId);
        }
        //delete invertedindex of title
        HashMap<String, List<Integer>> keywordsInTitle = forwardIndexer.getTitleKeywordList(pageId);
        for (Map.Entry<String, List<Integer>> entry : keywordsInTitle.entrySet()){
            String word = entry.getKey();
            int wordId = getWordIdByWord(word);
            Set<Posting> postingList = (Set<Posting>) invertedIndexer.getPostingTitle(wordId);
            postingList.removeIf(p -> p.getDocId() == pageId);
            //posting list become empty?
        }
        forwardIndexer.deletePage(pageId);
        indexDB.delEntry(URL_TO_PAGE_ID, pageId);
        indexDB.delEntry(PAGE_ID_TO_WEBPAGE, pageId);
        linkIndexer.delete(pageId);
        updatedPageCount(-1);
    }

    public void updatedPageCount(int increment){
        Integer cnt = (Integer) indexDB.getEntry(COUNT_INFO,"pageCount");
        int newCnt = cnt + increment;
        indexDB.addEntry(COUNT_INFO, "pageCount",newCnt);

    }

    public void addParentLink(String url, String parentUrl){
        int pageId = getPageIdByUrl(url);
        int parentId = getPageIdByUrl(parentUrl);
        linkIndexer.addParentLink(pageId, parentId);
    }
    public void addChildLinks(String url, String childUrl){
        int pageId = getPageIdByUrl(url);
        int parentId = getPageIdByUrl(childUrl);
        linkIndexer.addChildLinks(pageId, parentId);
    }


    public double getPageRankValue(int pageId){
        return (double)indexDB.getEntry(WEBPAGE_TO_PAGE_RANK_VALUE, pageId);
    }

    public List<Integer> getAllPageId() throws IOException {
        List<Object> objectList = indexDB.getAllValues(URL_TO_PAGE_ID);
        List<Integer> pageIdsList = new ArrayList<>();
        for(Object object:objectList){
            pageIdsList.add((Integer) object);
        }
        return pageIdsList;
    }
    public Set<Integer> getParentIdsByPageId(int pageId){
        return  linkIndexer.getLinkIdsByPageId(LinkIndexer.PAGE_ID_TO_PARENT_ID, pageId);
    }

    public Set<Integer> getChildIdsByPageId(int pageId){
        return  linkIndexer.getLinkIdsByPageId(LinkIndexer.PAGE_ID_TO_CHILD_ID, pageId);
    }
    public Set<String> getParentLinksByPageId(int pageId){
        Set<Integer> linksId = linkIndexer.getLinkIdsByPageId(LinkIndexer.PAGE_ID_TO_PARENT_ID, pageId);
        Set<String> parentlinks = new HashSet<>();
        for(Integer linkId:linksId){
            String url = getWebpageById(linkId).getUrl();
            parentlinks.add(url);
        }
        return parentlinks;
    }

    public Set<String> getChildLinksByPageId(int pageId){
        Set<Integer> linksId = linkIndexer.getLinkIdsByPageId(LinkIndexer.PAGE_ID_TO_CHILD_ID, pageId);
        Set<String> childlinks = new HashSet<>();
        for(Integer linkId:linksId){
            String url = getWebpageById(linkId).getUrl();
            childlinks.add(url);
        }
        return childlinks;
    }

    public List<Integer> getWordPositionInThisPage(String word,Integer pageID){
        int wordID=getWordIdByWord(word);
        Set<Posting> word_postings=invertedIndexer.getPostingTitle(wordID);
        List<Integer> wordPositionInSinglePage = new ArrayList<>();
        if(word_postings!=null){
            for(Posting posting: word_postings){
                if(posting.getDocId()==pageID){
                    return posting.getPositions();
                }
            }
        }

        return wordPositionInSinglePage;
    }

    public Webpage getWebpageById(int pageId){
        return (Webpage) indexDB.getEntry(PAGE_ID_TO_WEBPAGE, pageId);
    }

    public   List<HashMap.SimpleEntry<String, Integer>>  getTop5Keywords(int pageId){
        int num = 5;
        HashMap<String, List<Integer>>  wordFreq = forwardIndexer.getBodyKeywordList(pageId);
        PriorityQueue<Map.Entry<String, List<Integer>>> minHeap = new PriorityQueue<>(num,
                (entry1, entry2) -> Integer.compare(entry1.getValue().size(), entry2.getValue().size())
        );

        for (Map.Entry<String, List<Integer>> entry : wordFreq.entrySet()) {
            minHeap.add(entry);

            if (minHeap.size() > num) {
                minHeap.poll();
            }
        }

        List<HashMap.SimpleEntry<String, Integer>>  top5Words = new ArrayList<>();
        while (!minHeap.isEmpty()) {
            Map.Entry<String, List<Integer>> entry = minHeap.poll();
            top5Words.add(new HashMap.SimpleEntry<>(entry.getKey(),entry.getValue().size()));
        }
        top5Words.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()));
        return top5Words;

    }

    public void setPageRankValue(int pageId, double pageRankValue){
        indexDB.addEntry(WEBPAGE_TO_PAGE_RANK_VALUE, pageId, pageRankValue);
    }
    public static int getPageCount(){
        return (int) indexDB.getEntry(COUNT_INFO,"pageCount");
    }
    public void close() throws IOException {
        indexDB.close();
        invertedIndexer.close();
        forwardIndexer.close();
        linkIndexer.close();
    }


}
