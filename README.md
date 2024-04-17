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

<img src="C:\Users\User\AppData\Roaming\Typora\typora-user-images\image-20240410222900320.png" alt="image-20240410222900320" style="zoom: 50%;" />

* Modify `URL`  and click `Deploy application configured in Tomcat instance`

<img src="C:\Users\User\AppData\Roaming\Typora\typora-user-images\image-20240417110458687.png" alt="image-20240417110458687" style="zoom:50%;" />



## Running 

In IntelliJ IDEA, find the **Main class** and run it to perform crawling and indexing, and calculating page rank value.

<img src="C:\Users\User\AppData\Roaming\Typora\typora-user-images\image-20240410223312080.png" alt="image-20240410223312080" style="zoom:50%;" />

<img src="C:\Users\User\AppData\Roaming\Typora\typora-user-images\image-20240410233702276.png" alt="image-20240410233702276" style="zoom:50%;" />

After fetching 304 webpages, the index files are stored under the root directory of the project. 

<img src="C:\Users\User\AppData\Roaming\Typora\typora-user-images\image-20240417131933633.png" alt="image-20240417131933633" style="zoom:50%;" />

When we start the Tomcat for deployment, the server will only read file in the `bin` directory of the installment path of Tomcat. To make sure that the `index` files and `stopwords` file can be read, you need to change two the directory in two file, `indexer/IndexDB` and `utils/TokenizerHandler`.

<img src="C:\Users\User\AppData\Roaming\Typora\typora-user-images\image-20240417101104907.png" alt="image-20240417101104907" style="zoom:50%;" />

![image-20240417101135317](C:\Users\User\AppData\Roaming\Typora\typora-user-images\image-20240417101135317.png)

Start the tomcat server for searching

Initial page 

<img src="C:\Users\User\Documents\WeChat Files\wxid_wicg1dy8nxmo12\FileStorage\Temp\22845195dfd799fdd96cb4ca54bbf39.png" alt="22845195dfd799fdd96cb4ca54bbf39" style="zoom: 33%;" />

Searching result 

![2f94a338c242a43898b8fde71c3b012](C:\Users\User\Documents\WeChat Files\wxid_wicg1dy8nxmo12\FileStorage\Temp\2f94a338c242a43898b8fde71c3b012.png)

## Search examples

1. Input `Magical Rescue` for testing title favor

   ![3451305454d5d05c31977765e93ed6f](C:\Users\User\Documents\WeChat Files\wxid_wicg1dy8nxmo12\FileStorage\Temp\3451305454d5d05c31977765e93ed6f.png)

2. Input `"computer science"` for testing phrase search

   We use double quota to indicate recognizing the content inside as a phrase.

   ![5daa5c56e28683061a1e7b61bfa9e39](C:\Users\User\Documents\WeChat Files\wxid_wicg1dy8nxmo12\FileStorage\Temp\5daa5c56e28683061a1e7b61bfa9e39.png)

3. Input `movie`s for testing the PageRank 

   There are many pages pointing to the webpage that ranks first.

   ![953cce06bdb627d8b4470230f46d870](C:\Users\User\Documents\WeChat Files\wxid_wicg1dy8nxmo12\FileStorage\Temp\953cce06bdb627d8b4470230f46d870.png)
