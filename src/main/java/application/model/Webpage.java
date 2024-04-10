package application.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Save some useful page information when crawling, including title, url, lastModified data, page size, tfMax
 */
public class Webpage implements Serializable {
    private String title;
    private String url;
    private String lastModifiedDate;
    private int pageSize;
    private int tfMax;

    private  String text;

    public Webpage(){}
    public Webpage(String text, String title, String url, String lastModifiedDate, int pageSize){
        this.title = title;
        this.url = url;
        this.lastModifiedDate = lastModifiedDate;
        this.pageSize = pageSize;
        this.text = text;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public int getTfMax() {
        return tfMax;
    }

    public void setTfMax(int tfMax) {
        this.tfMax = tfMax;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "PageInfo{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", lastModifiedDate='" + lastModifiedDate + '\'' +
                ", pageSize=" + pageSize +
                ", tfMax=" + tfMax +
                '}';
    }
}
