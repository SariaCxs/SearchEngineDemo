package application.indexer;

import application.model.Posting;
import jdbm.helper.FastIterator;
import jdbm.htree.HTree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InvertedIndexer {
    private final String dbName = "invertedDB";
    public static final String WORD_ID_TO_POSTING_BODY = "word_id_to_posting_body";
    public static final String WORD_ID_TO_POSTING_TITLE = "word_id_to_posting_title";
    public IndexDB indexDB;

    public InvertedIndexer() {
        ArrayList<String> indexNames = new ArrayList<>();
        indexNames.add(WORD_ID_TO_POSTING_BODY);
        indexNames.add(WORD_ID_TO_POSTING_TITLE);
        indexDB = new IndexDB(dbName, indexNames);
    }


    public void indexWebPageBody(int wordId, Posting posting){
        Set<Posting> currentPosting = (Set<Posting>) indexDB.getEntry(WORD_ID_TO_POSTING_BODY, wordId);
        if(currentPosting == null){
            currentPosting = new HashSet<>();
        }
        currentPosting.add(posting);
        indexDB.addEntry(WORD_ID_TO_POSTING_BODY, wordId, currentPosting);
    }


    public void indexWebPageTitle(int wordId, Posting postingTitle){
        Set<Posting> currentPosting = (Set<Posting>) indexDB.getEntry(WORD_ID_TO_POSTING_TITLE, wordId);
        if(currentPosting == null){
            currentPosting = new HashSet<>();
        }
        currentPosting.add(postingTitle);
        indexDB.addEntry(WORD_ID_TO_POSTING_TITLE, wordId, currentPosting);
    }

    public Set<Posting> getPostingBody(int wordId){
        Set<Posting> currentPosting = (Set<Posting>) indexDB.getEntry(WORD_ID_TO_POSTING_BODY, wordId);
        return currentPosting;
    }

    public Set<Posting> getPostingTitle(int wordId){
        Set<Posting> currentPosting = (Set<Posting>) indexDB.getEntry(WORD_ID_TO_POSTING_TITLE, wordId);
        return currentPosting;
    }

    public void close() throws IOException {
        indexDB.close();
    }
}
