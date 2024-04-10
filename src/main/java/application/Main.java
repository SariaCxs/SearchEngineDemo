package application;

import application.indexer.IndexerController;
import application.search.Searcher;
import application.spider.Spider;
import application.utils.PageRank;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        //crawling and indexing
        String initialUrl = "https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm";
        Spider.fetch(initialUrl);
        IndexerController indexer = Spider.indexer;

        //start runing page rank
        List<Integer> pageIds = indexer.getAllPageId();
        Map<Integer, Set<Integer>> childToParentLinks = new HashMap<>();
        Map<Integer, Set<Integer>> parentToChildLinks =new HashMap<>();
        for(int pageId:pageIds){
            Set<Integer> childPages = indexer.getChildIdsByPageId(pageId);
            Set<Integer> parentPages = indexer.getParentIdsByPageId(pageId);
            childToParentLinks.put(pageId,parentPages);
            parentToChildLinks.put(pageId,childPages);
        }
        PageRank pageRank = new PageRank(childToParentLinks,parentToChildLinks);
        pageRank.computePageRanks();
        for(int pageId:pageIds){
            double pageRankValue = pageRank.getPageRank(pageId);
            indexer.setPageRankValue(pageId,pageRankValue);
        }
        //store the data
        indexer.close();
    }
}
