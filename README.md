# SearchEngineDemo

## Team Members

| Name           | Student ID |
| -------------- | ---------- |
| CHEN, Xiaoshan | 50007864   |
| YANG, Xinyu    | 20971580   |
| WANG, Ziyi     | 20985608   |

## System Architecture

* **Spider**

  * fetch and parse webpages 

    Jsoup: Java HTML Parser

  * store webpage information and link relation to database

    JDBM(support scalable data structures, such as HTree and B+Tree, to support persistence of large object collections)

* **Indexer**

  * Process webpage, create inverted index, forward index and other statistic information
  * Store index files to database

* **Searcher**

  * Load index files to get information for searching
  * Rank results based on cosine similarity score, PageRank value, and title matched score

* **Web Server**

  * A web interface to users(Tomcat)
  * Forward users' request to Searcher and display searching result

## Requirement

- IntelliJ IDEA
- JDK 21
- Maven 3


## Installation

```
 git clone https://github.com/SariaCxs/search_engine_demo.git
```

* Configuration on IDEA

![image]([https://github.com/lexsaints/powershell/blob/master/IMG/ps2.png](https://github.com/SariaCxs/SearchEngineDemo/blob/main/image/initial.png))

* Modify `URL`  and click `Deploy application configured in Tomcat instance`

![image]([https://github.com/lexsaints/powershell/blob/master/IMG/ps2.png](https://github.com/SariaCxs/SearchEngineDemo/blob/main/image/config.png))



## Running 

In IntelliJ IDEA, find the **Main class** and run it to perform crawling and indexing, and calculating page rank value.

![image]([https://github.com/lexsaints/powershell/blob/master/IMG/ps2.png](https://github.com/SariaCxs/SearchEngineDemo/blob/main/image/main.png))

![image]([https://github.com/lexsaints/powershell/blob/master/IMG/ps2.png](https://github.com/SariaCxs/SearchEngineDemo/blob/main/image/finish_fetching.png))

After fetching 304 webpages, the index files are stored under the root directory of the project. 

![image]([https://github.com/lexsaints/powershell/blob/master/IMG/ps2.png](https://github.com/SariaCxs/SearchEngineDemo/blob/main/image/db.png))

When we start the Tomcat for deployment, the server will only read file in the `bin` directory of the installment path of Tomcat. To make sure that the `index` files and `stopwords` file can be read, you need to change two the directory in two file, `indexer/IndexDB` and `utils/TokenizerHandler`.

![image]([https://github.com/lexsaints/powershell/blob/master/IMG/ps2.png](https://github.com/SariaCxs/SearchEngineDemo/blob/main/image/indedb.png))

![image]([https://github.com/lexsaints/powershell/blob/master/IMG/ps2.png](https://github.com/SariaCxs/SearchEngineDemo/blob/main/image/stopwords.png))

Start the tomcat server for searching

Initial page 

![image]([https://github.com/lexsaints/powershell/blob/master/IMG/ps2.png](https://github.com/SariaCxs/SearchEngineDemo/blob/main/image/initial.png))

Searching result 

![image]([https://github.com/lexsaints/powershell/blob/master/IMG/ps2.png](https://github.com/SariaCxs/SearchEngineDemo/blob/main/image/book.png))

## Search examples

1. Input `Magical Rescue` for testing title favor

![image]([https://github.com/lexsaints/powershell/blob/master/IMG/ps2.png](https://github.com/SariaCxs/SearchEngineDemo/blob/main/image/Magical Rescue.png))

2. Input `"computer science"` for testing phrase search

   We use double quota to indicate recognizing the content inside as a phrase.

   ![image]([https://github.com/lexsaints/powershell/blob/master/IMG/ps2.png](https://github.com/SariaCxs/SearchEngineDemo/blob/main/image/computer science.png))

3. Input `movie`s for testing the PageRank 

   There are many pages pointing to the webpage that ranks first.

![image]([https://github.com/lexsaints/powershell/blob/master/IMG/ps2.png](https://github.com/SariaCxs/SearchEngineDemo/blob/main/image/movies.png))
