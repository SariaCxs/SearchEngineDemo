package application;

import application.indexer.ForwardIndexer;
import application.indexer.IndexerController;
import application.model.Posting;
import application.model.Webpage;
import application.search.Searcher;
import application.spider.Spider;
import application.utils.*;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
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
        //compute page rank
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
            indexer.setPageRankValue(pageId,pageRankValue);
        }

        //compute weights for body part
        Map<String, Double> DF = new HashMap<>();
        for(int pageId:pageIds){
            Map<String, List<Integer>> keywordList = indexer.forwardIndexer.getBodyKeywordList(pageId);
            HashMap<String, Double> keyWordWeights = new HashMap<>();
            for(Map.Entry<String,List<Integer>> entry:keywordList.entrySet()){
                String token = entry.getKey();

                int tfMax = ForwardIndexer.getMaxTFById(pageId);
                double tf = entry.getValue().size();

                double df = 0;
                if (DF.keySet().contains(token)){
                    df = DF.get(token);
                }
                else{
                    int wordId = indexer.getWordIdByWord(token);
                    df = indexer.invertedIndexer.getPostingBody(wordId).size();
                    DF.put(token, df);
                }
                int N = indexer.getPageCount();
                double tfIdf = (tf/tfMax) * (Math.log(N/df) / Math.log(2.0));

                keyWordWeights.put(token, tfIdf);
            }
            indexer.forwardIndexer.addKeywordListBodyWeights(pageId,keyWordWeights);
        }

        //get token information
//        try {
//            FileWriter write=new FileWriter("C:\\Users\\User\\Desktop\\wordInfo.txt");
//            BufferedWriter bw=new BufferedWriter(write);
//            List<String> words = indexer.getAllWord();
//            for(String word:words){
//                int cnt = 0;
//                int wordId = indexer.getWordIdByWord(word);
//                if (word.equals("")){
//                    continue;
//                }
//                Set<Posting> postings = indexer.invertedIndexer.getPostingBody(wordId);
//                if(postings==null){
//                    continue;
//                }
//
//                for(Posting posting:postings){
//                    if(word .equals( "titl")){
//                        System.out.println(indexer.getWebpageById(posting.getDocId()).getUrl());
//                    }
//                    cnt += posting.getFrequency();
//                }
//                bw.write(word+" "+cnt+"\n");
//            }
//            bw.close();
//            write.close();
//        }catch(IOException e){
//            e.printStackTrace();
//        }

        indexer.close();
    }
}
