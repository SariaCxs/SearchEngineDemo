package application.indexer;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.helper.FastIterator;
import jdbm.htree.HTree;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class IndexDB {
    private RecordManager recman;
    private HashMap<String, HTree> maps = new HashMap<>();
    public IndexDB(String DBName, ArrayList<String> indexNames) {
        try {
            String root_dir = "C:\\Users\\User\\IdeaProjects\\SearchEngineDemo\\";
            recman = RecordManagerFactory.createRecordManager(root_dir+DBName);
            for(int i =0;i<indexNames.toArray().length;i++){
                String name = indexNames.get(i);
                HTree hashtable;
                long recid = recman.getNamedObject(name);
                if (recid != 0){
                    System.out.println("exists: "+name);
                    hashtable = HTree.load(recman, recid);
                }
                else {
                    hashtable = HTree.createInstance(recman);
                    recman.setNamedObject(name, hashtable.getRecid());
                }
                maps.put(name, hashtable);
            }
        }catch (Exception e){
            System.out.println(e);
        }

    }

    public void addEntry(String indexName, Object key, Object value)
    {
        try {
            HTree hashtable = maps.get(indexName);
            if(hashtable != null){
                hashtable.put(key, value);
            }
        }catch (Exception e) {
            System.out.printf("Error adding entry of index %s; %s", indexName, e.getMessage());
        }

    }

    public Object getEntry(String indexName, Object key){
        try {
            HTree hashtable = maps.get(indexName);
            if(hashtable != null){
                return hashtable.get(key);
            }
        }catch (Exception e) {
            System.out.printf("Error adding entry of index %s; %s", indexName, e.getMessage());
        }
        return null;
    }

    public void delEntry(String indexName, Object key)
    {
        try {
            HTree hashtable = maps.get(indexName);
            if(hashtable != null){
                hashtable.remove(key);
            }
        }
        catch (Exception e) {
            System.out.printf("Error deleting entry of index %s; %s", indexName, e.getMessage());
        }
    }


    public List<Object> getAllValues(String indexName) throws IOException {
        List<Object> res = new ArrayList<>();
        HTree hashtable = maps.get(indexName);
        if(hashtable != null){
            FastIterator iter = hashtable.values();
            Object word = iter.next();
            while ( word != null ) {
                res.add(word);
                word = iter.next();
            }
        }
        return res;
    }

    public HTree getHashtable(String indexName) throws IOException {
        List<Object> res = new ArrayList<>();
        HTree hashtable = maps.get(indexName);
        return hashtable;
    }


    public void close() throws IOException {
        recman.commit();
        recman.close();
    }
}
