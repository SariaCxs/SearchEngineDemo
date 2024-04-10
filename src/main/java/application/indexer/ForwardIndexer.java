package application.indexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class ForwardIndexer {

    private final String dbName = "forwardDB";
    public static final String PAGE_ID_TO_KEYWORDS_BODY = "page_id_to_keywords_body";
    public static final String PAGE_ID_TO_KEYWORDS_TITLE = "page_id_to_keywords_title";
    public static final String PAGE_ID_TO_MAXTF = "page_id_to_maxTF";
    public static IndexDB indexDB;

    public ForwardIndexer() {
        ArrayList<String> indexNames = new ArrayList<>();
        indexNames.add(PAGE_ID_TO_KEYWORDS_BODY);
        indexNames.add(PAGE_ID_TO_MAXTF);
        indexNames.add(PAGE_ID_TO_KEYWORDS_TITLE);
        indexDB = new IndexDB(dbName, indexNames);
    }

    public void addMaxTF(int pageId, int maxTF){
        indexDB.addEntry(PAGE_ID_TO_MAXTF, pageId, maxTF);
    }

    public void addKeywordListBody(int pageId, HashMap<String, List<Integer>> wordPositions){
        indexDB.addEntry(PAGE_ID_TO_KEYWORDS_BODY, pageId, wordPositions);
    }
    public void addKeywordListTitle(int pageId, HashMap<String, List<Integer>> wordPositions){
        indexDB.addEntry(PAGE_ID_TO_KEYWORDS_TITLE, pageId, wordPositions);
    }

    public static int getMaxTFById(int pageId){
        return (int) indexDB.getEntry(PAGE_ID_TO_MAXTF,pageId);
    }

    public HashMap<String, List<Integer>> getBodyKeywordList(int pageId){
        HashMap<String, List<Integer>> keyWordList = (HashMap<String, List<Integer>>) indexDB.getEntry(PAGE_ID_TO_KEYWORDS_BODY, pageId);
        if(keyWordList == null){
            return new HashMap<>();
        }
        return keyWordList;
    }

    public HashMap<String, List<Integer>> getTitleKeywordList(int pageId){
        HashMap<String, List<Integer>> keyWordList = (HashMap<String, List<Integer>>) indexDB.getEntry(PAGE_ID_TO_KEYWORDS_TITLE, pageId);
        if(keyWordList == null){
            return new HashMap<>();
        }
        return keyWordList;
    }

    public void deletePage(int pageId){
        indexDB.delEntry(PAGE_ID_TO_KEYWORDS_TITLE, pageId);
        indexDB.delEntry(PAGE_ID_TO_KEYWORDS_BODY, pageId);
    }

    public void close() throws IOException {
        indexDB.close();
    }


}
