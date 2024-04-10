package application.servlet;// 导入必需的 java 库
import java.io.*;

import application.indexer.IndexerController;
import application.model.SearchResult;
import application.spider.Spider;
import application.utils.PageRank;
import jakarta.servlet.annotation.WebServlet;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.*;

import application.search.Searcher;

// 扩展 HttpServlet 类
@WebServlet("/search")
public class SearchServlet implements Servlet {

    private String message = "Hello World";

    public Searcher searcher;
    public SearchServlet() throws IOException {
//        String initialUrl = "https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm";
//        Spider.fetch(initialUrl);
//        IndexerController indexer = Spider.indexer;
//        List<Integer> pageIds = indexer.getAllPageId();
//        Map<Integer, Set<Integer>> childToParentLinks = new HashMap<>();
//        Map<Integer, Set<Integer>> parentToChildLinks =new HashMap<>();
//        for(int pageId:pageIds){
//            Set<Integer> childPages = indexer.getChildIdsByPageId(pageId);
//            Set<Integer> parentPages = indexer.getParentIdsByPageId(pageId);
//            childToParentLinks.put(pageId,parentPages);
//            parentToChildLinks.put(pageId,childPages);
//        }
//        PageRank pageRank = new PageRank(childToParentLinks,parentToChildLinks);
//        pageRank.computePageRanks();
//        for(int pageId:pageIds){
//            double pageRankValue = pageRank.getPageRank(pageId);
//            indexer.setPageRankValue(pageId,pageRankValue);
//        }
//        indexer.close();
        IndexerController indexer  = Spider.indexer;
        this.searcher = new Searcher(indexer);
    }

    @Override
    public void init(ServletConfig servletConfig)  {

//        searcher =
    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        servletResponse.setContentType("text/html");
        String query = servletRequest.getParameter("query");
        System.out.println(query);
        List<SearchResult> searchResults = searcher.search(query);
        // Hello
//        PrintWriter out = servletResponse.getWriter();
//        for(SearchResult searchResult:searchResults){
//            out.println("<div class='result-item'>");
//            out.println("<p>Result for: " + query + "</p>");
//            out.println("<p>URL:"+searchResult.getUrl()+"</p>");
//            out.println("<p>Title:"+searchResult.getTitle()+"</p>");
////            out.println("<p>Last Modified: 2021-01-01</p>");
////            out.println("<p>Size: 10KB</p>");
////            out.println("<p>Score: 0.9</p>");
//            out.println("</div>");
//        }
        servletRequest.setAttribute("data", searchResults);
        servletRequest.getRequestDispatcher("SEDemo.jsp").forward(servletRequest, servletResponse);

    }

    @Override
    public String getServletInfo() {
        return "";
    }

    @Override
    public void destroy() {

    }
}

