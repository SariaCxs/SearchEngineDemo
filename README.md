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
![tomcat](https://github.com/SariaCxs/SearchEngineDemo/assets/56586001/bb487348-bc50-4df2-a1da-38fd3cd10308)
* Modify `URL`  and click `Deploy application configured in Tomcat instance`
![config](https://github.com/SariaCxs/SearchEngineDemo/assets/56586001/5cd0a570-5e33-4303-8086-96636a629612)
* add the application when the server stars in the Deployment
![image](https://github.com/SariaCxs/SearchEngineDemo/assets/56586001/26b1f09f-a22f-4a1e-8394-a58e5986aece)



## Running 

When we start the Tomcat for deployment, the server will only read file in the `bin` directory of the installment path of Tomcat. To make sure that the `index` files and `stopwords` file can be read, you need to change two directory in two file, `indexer/IndexDB` and `utils/TokenizerHandler`, you can set them to your own working directory.

![indedb_dir](https://github.com/SariaCxs/SearchEngineDemo/assets/56586001/221465e3-97d5-473e-ab8f-5bb8d678aec0)

![image](https://github.com/SariaCxs/SearchEngineDemo/assets/56586001/1f13c748-4581-46e1-a42c-23bd50c8658a)

Find the **Main class** and run it to perform crawling and indexing, and calculating cosine similarity wieights and page rank value.

![main](https://github.com/SariaCxs/SearchEngineDemo/assets/56586001/b9071d44-c186-4bf8-8e6f-2a9e6739f4ab)
![finish_fetching](https://github.com/SariaCxs/SearchEngineDemo/assets/56586001/897b4aa5-b8a1-44f5-81b4-2c2f3535e427)

After fetching 304 webpages, the index files are stored under the root directory of the project. 

![db](https://github.com/SariaCxs/SearchEngineDemo/assets/56586001/a1ea9efb-2e9e-4a5e-bf76-c0d62d3361c1)

Start the tomcat server for searching

Initial page 

![image](https://github.com/SariaCxs/SearchEngineDemo/assets/56586001/b2c93407-a9b6-454f-aa6d-05c0eec44145)

Searching result 

![image](https://github.com/SariaCxs/SearchEngineDemo/assets/56586001/889f9eb5-a2d6-4142-959e-1a2968e12e02)

## Search examples

1. Input `Magical Rescue` for testing title favor

   ![image](https://github.com/SariaCxs/SearchEngineDemo/assets/56586001/3a553b48-3a97-45a8-a078-bd89c5f27403)

2. Input `"international news"` for testing phrase search (double quotes to detect a phrase)

   Search results without double quotes for internation news
   
   ![image](https://github.com/SariaCxs/SearchEngineDemo/assets/56586001/92aa243b-1ff7-44b5-bf2c-759e27b3022a)
   
   Search results with double quotes for internation news

   ![image](https://github.com/SariaCxs/SearchEngineDemo/assets/56586001/365e793b-9490-45b8-b57c-ef1b8a4c5000)

4. Input `movie`s for testing the PageRank 

   There are many pages pointing to the webpage that ranks first.

   ![image](https://github.com/SariaCxs/SearchEngineDemo/assets/56586001/e72b5d41-6176-439a-862c-9c1ae2d202d3)
