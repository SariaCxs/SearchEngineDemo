<%@ page import="application.model.SearchResult" %>
<%@ page import="java.util.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Search Results</title>
    <link rel="stylesheet" type="text/css" href="css/styles.css">
</head>
<body>

<div class="top-background">
    <img src="static/hkust_logo.png" alt="HKUST Logo" class="hkust-logo">
</div>

<div class="search-box">
    <form action="search" method="get">
        <input type="text" name="query" class="search-input" placeholder="Press enter to search..." value="<%= request.getParameter("query") %>">
    </form>
</div>

<div class="content">
    <h2>Search Results for "<%= request.getParameter("query") %>"</h2>
    <%
        String query = (String) request.getParameter("query");
        List<SearchResult> results = (List<SearchResult>) request.getAttribute("data");
        if(results != null) {
            // 限制显示的搜索结果数量最多为50条
            int maxResults = 50;
            for(int i = 0; i < Math.min(results.size(), maxResults); i++) {
                SearchResult result = results.get(i);
                out.println("<div class='result-item'>");
//                out.println("<span>" + (i + 1) + ". </span>"+result.getUrl()+"");
                out.println("<h3>" + (i + 1) + ". "+ result.getTitle()+"</h3>");
                out.println("<a href="+result.getUrl()+">"+result.getUrl()+"</a>");
                StringBuilder keywords = new StringBuilder();
                for (HashMap.SimpleEntry entry : result.getTop5Keywords()) {
                    String word = (String) entry.getKey();
                    Integer frequency = (Integer) entry.getValue();
                    keywords.append(word+" "+frequency+"; ");
                }
                out.println("<p>"+keywords+"</p >");
                out.println("<p>Last Modified: "+result.getLastModifiedDate()+"</p >");
                out.println("<p>Page size: "+result.getSize()+" btye</p >");
                out.println("<p>Score: "+result.getScore()+"</p >");
                out.println("<p>ParentLinks:</p>");
                for(String parent:result.getParentLinks()){
                    out.println("<a href="+parent+">"+parent+"</a>");
                    out.println("<p></p>");
                }
                out.println("<p>ChildrenLinks:</p>");
                for(String children:result.getChildLinks()){
                    out.println("<a href="+children+">"+children+"</a>");
                    out.println("<p></p>");
                }
                out.println("</div>");
            }
        } else {
            out.println("<p>No query provided or query is empty.</p >");
        }
    %>
</div>

<div class="footer">
    Department of Computer Science & Engineering at HKUST
</div>

</body>
</html>
