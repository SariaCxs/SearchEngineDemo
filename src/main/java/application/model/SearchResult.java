package application.model;
import java.util.*;

public class SearchResult {
    private String score;
    private String title;
    private String url;
    private String lastModifiedDate;
    private int size;
    private List<HashMap.SimpleEntry<String, Integer>>  top5Keywords;
    private Set<String> parentLinks;
    private Set<String> childLinks;

    private String summary;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setParentLinks(Set<String> parentLinks) {
        this.parentLinks = parentLinks;
    }

    public void setChildLinks(Set<String> childLinks) {
        this.childLinks = childLinks;
    }

    public String getScore() {
        return score;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public int getSize() {
        return size;
    }

    public Set<String> getParentLinks() {
        return parentLinks;
    }

    public Set<String> getChildLinks() {
        return childLinks;
    }

    public List<HashMap.SimpleEntry<String, Integer>> getTop5Keywords() {
        return top5Keywords;
    }

    public void setTop5Keywords(List<HashMap.SimpleEntry<String, Integer>> top5Keywords) {
        this.top5Keywords = top5Keywords;
    }

    @Override
    public String toString() {
        return "PageInfo{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", lastModifiedDate='" + lastModifiedDate + '\'' +
                ", score=" + score +
                '}';
    }
}
