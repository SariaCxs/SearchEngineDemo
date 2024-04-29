<%@ page import="application.model.SearchResult" %>
<%@ page import="java.util.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Search Results</title>
    <link rel="stylesheet" type="text/css" href="css/styles.css">
    <script>
        window.onload = function() {
            var hiddenSections = document.querySelectorAll('.hidden-links');
            hiddenSections.forEach(function(section) {
                section.style.display = 'none'; // page is hidden when it loads
            });
        };

        function toggleLinks(id) {
            var hiddenLinks = document.getElementById('hidden-' + id);
            if (hiddenLinks.style.display === 'none') {
                hiddenLinks.style.display = 'block'; // show links
                document.getElementById('button-' + id).textContent = 'Show Less';
            } else {
                hiddenLinks.style.display = 'none'; // hidden links
                document.getElementById('button-' + id).textContent = 'Show More';
            }
        }
    </script>

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
            // maximum display 50
            int maxResults = 50;
            int linkDisplayLimit = 4;
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
//                out.println("<p>ParentLinks:</p>");
//                for(String parent:result.getParentLinks()){
//                    out.println("<a href="+parent+">"+parent+"</a>");
//                    out.println("<p></p>");
//                }
//                out.println("<p>ChildrenLinks:</p>");
//                for(String children:result.getChildLinks()){
//                    out.println("<a href="+children+">"+children+"</a>");
//                    out.println("<p></p>");
//                }
//                out.println("</div>");

                // ParentLinks
                out.println("<p>ParentLinks:</p>");
                List<String> parentLinks = new ArrayList<String>(result.getParentLinks());
                if(parentLinks.size() > linkDisplayLimit) {
                    for(int j = 0; j < linkDisplayLimit; j++) {
                        out.println("<a href='"+parentLinks.get(j)+"'>"+parentLinks.get(j)+"</a><br>");
                    }
                    out.println("<div id='hidden-" + i + "-parents' class='hidden-links'>");
                    for(int j = linkDisplayLimit; j < parentLinks.size(); j++) {
                        out.println("<a href='"+parentLinks.get(j)+"'>"+parentLinks.get(j)+"</a><br>");
                    }
                    out.println("</div>");
                    out.println("<span id='button-" + i + "-parents' onclick='toggleLinks(\"" + i + "-parents\")' class='show-more'>Show More</span>");
                } else {
                    for(String parent : parentLinks) {
                        out.println("<a href='"+parent+"'>"+parent+"</a><br>");
                    }
                }

                // ChildLinks
                out.println("<p>ChildrenLinks:</p>");
                List<String> childLinks = new ArrayList<String>(result.getChildLinks());
                if(childLinks.size() > linkDisplayLimit) {
                    for(int j = 0; j < linkDisplayLimit; j++) {
                        out.println("<a href='"+childLinks.get(j)+"'>"+childLinks.get(j)+"</a><br>");
                    }
                    out.println("<div id='hidden-" + i + "-children' class='hidden-links'>");
                    for(int j = linkDisplayLimit; j < childLinks.size(); j++) {
                        out.println("<a href='"+childLinks.get(j)+"'>"+childLinks.get(j)+"</a><br>");
                    }
                    out.println("</div>");
                    out.println("<span id='button-" + i + "-children' onclick='toggleLinks(\"" + i + "-children\")' class='show-more'>Show More</span>");
                } else {
                    for(String children : childLinks) {
                        out.println("<a href='"+children+"'>"+children+"</a><br>");
                    }
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

