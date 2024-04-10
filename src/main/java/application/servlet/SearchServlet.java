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

    private Searcher searcher = new Searcher(Spider.indexer);

    @Override
    public void init(ServletConfig servletConfig)  {
    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        servletResponse.setContentType("text/html");
        String query = servletRequest.getParameter("query");
        List<SearchResult> searchResults = searcher.search(query);

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

