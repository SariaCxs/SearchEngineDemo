package application;

import application.indexer.IndexerController;
import application.search.Searcher;
import application.spider.Spider;
import application.utils.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        //crawling and indexing
        String initialUrl = "https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm";
        long startTime=System.currentTimeMillis();
        Spider.fetch(initialUrl);
        long endTime=System.currentTimeMillis();
        System.out.println("fetch time:"+(endTime-startTime)+"ms");
        IndexerController indexer = Spider.indexer;
//
        List<Integer> pageIds = indexer.getAllPageId();
        Map<Integer, List<Integer>> childToParentLinks = new HashMap<>();
        Map<Integer, List<Integer>> parentToChildLinks =new HashMap<>();
        for(int pageId:pageIds){
            List<Integer> childPages = new ArrayList<>(indexer.getChildIdsByPageId(pageId));
            List<Integer> parentPages = new ArrayList<>(indexer.getParentIdsByPageId(pageId));
            childToParentLinks.put(pageId,parentPages);
            parentToChildLinks.put(pageId,childPages);
        }
        PageRank pageRank = new PageRank(childToParentLinks,parentToChildLinks);
        pageRank.computePageRanks();
        for(int pageId:pageIds){
            double pageRankValue = pageRank.pageRankOf(pageId);
            if(pageRankValue > 0.1){
                System.out.println(indexer.getWebpageById(pageId).getUrl());
                System.out.println(pageRankValue);
            }
//            System.out.println(pageRankValue);
            indexer.setPageRankValue(pageId,pageRankValue);
        }
        indexer.close();
    }
}
