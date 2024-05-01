package application.spider;
import application.indexer.IndexerController;
import application.model.Webpage;
import application.model.Posting;

import java.io.*;
import java.util.*;

import java.util.ArrayList;


import java.net.URL;


import java.net.URLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Spider
{

    public static IndexerController indexer = new IndexerController();


    public static Date getLastModifiedDate(String _url) throws IOException {
        URL u = new URL(_url);
        URLConnection connection = u.openConnection();
        Date date = new Date(connection.getLastModified());
        return date;
    }
    public static int getPageSize(String _url) throws IOException {
        URL u = new URL(_url);
        URLConnection connection = u.openConnection();
        BufferedReader b = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String input = "";
        String temp = "";
        while((input = b.readLine())!=null)
            temp += input;

        b.close();
        return temp.length();
    }

    public static void fetch(String initial_url) throws IOException {
        try
        {

            int PageMax = 300;
            int pageID = 0;
            int n = 0;
            Map dict = new HashMap();
            //clear pageCount
            indexer.indexDB.addEntry("count_info","pageCount",0);
            indexer.indexDB.addEntry("count_info","wordCount",0);
            //Data structure for breadfirst search: visited_list, queue
            ArrayList<String> visited_urls = new ArrayList<String>();
            Queue<String> queue = new LinkedList<String>();
            queue.offer(initial_url);

            dict.put(initial_url,pageID);

            //start breadthfirst search
            while (!queue.isEmpty() & (n < PageMax) ){
                String url = queue.poll().strip();
                // check url visited or not
                if (!visited_urls.contains(url)) {
                    visited_urls.add(url);
                }
                else{
                    while(visited_urls.contains(url)){
                        url = queue.poll();
                    }
                    visited_urls.add(url);
                }
                if(url == null){
                    return;
                }
                Object current_pageID = dict.get(url);
                System.out.println("Current PageID: " + current_pageID);
                System.out.println("Current URL: " + url);


                //use jsoup to extract title, text and last modified date
                Document doc = Jsoup.connect(url).get();
                String titles = doc.title();
                String texts  = doc.body().text();
                String dates =  getLastModifiedDate(url).toString();
                //remove both Chinese(P) and English punctuation({Punct})
                String cleaned_titles = titles.replaceAll("[\\pP\\p{Punct}]","");
                String cleaned_texts = texts.replaceAll("[\\pP\\p{Punct}]","");
                //construct the webpage object for storing
                Webpage webPage = new Webpage(cleaned_texts, cleaned_titles, url,dates, getPageSize(url));

                Webpage oldPage = indexer.getWebpageByURL(url);
                indexer.updatedPageCount(1);
                if (oldPage!=null){
                    String old_date = oldPage.getLastModifiedDate();
                    // check last_modified_date
                    if (!dates.contentEquals(old_date)){
                        indexer.updateWebpage(webPage);
                    }
                }
                else {
                    // index the page
                    n++;
                    indexer.indexPage(webPage);
                }

                // iterate the child urls by BFS
//                extract links from current webpage
                Elements links = doc.select("a[href]");
                System.out.println("size: "+links.size());
                //iterate the child link to perform breadth first search, and set up the child and parent relationship

                for(Element ele : links) {
                    try {
                        String link = ele.attr("abs:href");
                        // check whether the url is valid
                        if(link == null){
                            continue;
                        }
                        String child = link;
                        pageID++;
                        dict.put(child, pageID);
                        queue.offer(child);
                        // add parent and child urls
                        indexer.addChildLinks(url, link);
                        indexer.addParentLink(link, url);
                    }

                    catch(Exception e){}

                }

            }
        } catch (Exception e) {
            e.printStackTrace ();
        }

    }



//    public static void main(String[] args) throws IOException {
//        String initial_url = "https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm";
//        Spider.fetch(initial_url);

        //test function
//        System.out.println("FINISHED fetching");
//        int pageId =2;
//        System.out.println("webpage information\n"+indexer.getWebpageById(pageId));
//        //test forwardIndexer
//        System.out.println("maxTF: "+indexer.forwardIndexer.getMaxTFById(pageId));
//        System.out.println("body keyword list: "+indexer.forwardIndexer.getBodyKeywordList(pageId));
//        System.out.println("title keyword list: "+indexer.forwardIndexer.getTitleKeywordList(pageId));
//        //test invertedindex
//        HashMap<String, List<Integer>> keywordlist = indexer.forwardIndexer.getBodyKeywordList(pageId);
//        for(Map.Entry<String, List<Integer>> entry: keywordlist.entrySet()){
//            int wordId = indexer.getWordIdByWord(entry.getKey());
//            Set<Posting> postings = indexer.invertedIndexer.getPostingBody(wordId);
//            System.out.println("word:"+wordId);
//            for(Posting posting:postings){
//                System.out.println(posting.getDocId());
//                System.out.println(posting.getFrequency());
//                System.out.println(posting.getPositions());
//            }
//        }
//
//        //test linkindex
//        System.out.println(indexer.getParentLinksByPageId(2));
//        System.out.println(indexer.getChildLinksByPageId(2));
//        //test delete webpage
//        System.out.print("DELETE page 4");
//        indexer.deleteWebpage(3);
//        for(Map.Entry<String, List<Integer>> entry: keywordlist.entrySet()){
//            int wordId = indexer.getWordIdByWord(entry.getKey());
//            Set<Posting> postings = indexer.invertedIndexer.getPostingBody(pageId);
//            if(postings == null){
//                System.out.print(wordId);
//            }
//        }
//        Spider.indexer.close();
        //get statistic
//        List<Integer> pageIds = indexer.getAllPageId();
//        try {
//            FileWriter write=new FileWriter("C:\\Users\\User\\Desktop\\fileSize.txt");
//            BufferedWriter bw=new BufferedWriter(write);
//            for(int page:pageIds){
//                Webpage webpage = indexer.getWebpageById(page);
//                int pageSize = webpage.getPageSize();
//                String modifiedDate = webpage.getLastModifiedDate();
//                System.out.println(modifiedDate);
//                bw.write(pageSize+":"+modifiedDate+"\n");
//            }
//            bw.close();
//            write.close();
//        }catch(IOException e){
//            e.printStackTrace();
//        }
//

//    }


}
