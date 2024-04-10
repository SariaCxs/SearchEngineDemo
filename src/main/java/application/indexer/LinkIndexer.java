package application.indexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class LinkIndexer {
    private final String dbName = "linkDB";
    public ArrayList<String> indexNames;
    public static final String PAGE_ID_TO_PARENT_ID = "page_id_to_parent_id";
    public static final String PAGE_ID_TO_CHILD_ID = "page_id_to_children_id";
    public IndexDB indexDB;
    public LinkIndexer() {
        ArrayList<String> indexNames = new ArrayList<>();
        indexNames.add(PAGE_ID_TO_PARENT_ID);
        indexNames.add(PAGE_ID_TO_CHILD_ID);
        indexDB = new IndexDB(dbName, indexNames);
    }
    public void addParentLink(int pageId, int parentId){
        addLink(PAGE_ID_TO_PARENT_ID, pageId, parentId);
    }
    public void addChildLinks(int pageId, int parentId){
        addLink(PAGE_ID_TO_CHILD_ID, pageId, parentId);
    }
    public void addLink(String linkType, int pageId, int linkId){
        Set<Integer> currentlinkIds = getLinkIdsByPageId(linkType, pageId);
        currentlinkIds.add(linkId);
        indexDB.addEntry(linkType,pageId,currentlinkIds);
    }

    public Set<Integer> getLinkIdsByPageId(String linkType, int pageId) {
        Set<Integer> linkIds = (Set<Integer>)indexDB.getEntry(linkType, pageId);
        if(linkIds == null){
            return new HashSet<>();
        }
        return linkIds;
    }

    public void delete(int pageId){
        Set<Integer> linkIds = getLinkIdsByPageId(PAGE_ID_TO_CHILD_ID,pageId);
        //remove pageId in the set of parentID of the child id
        for(Integer link:linkIds){
            Set<Integer> parentIds = getLinkIdsByPageId(PAGE_ID_TO_PARENT_ID, link);
            parentIds.remove(pageId);
            indexDB.addEntry(PAGE_ID_TO_PARENT_ID,link,parentIds);
        }
        linkIds = getLinkIdsByPageId(PAGE_ID_TO_PARENT_ID,pageId);
        for(Integer link:linkIds){
            Set<Integer> childIds = getLinkIdsByPageId(PAGE_ID_TO_CHILD_ID, link);
            childIds.remove(pageId);
            indexDB.addEntry(PAGE_ID_TO_CHILD_ID,link,childIds);
        }
        indexDB.delEntry(PAGE_ID_TO_CHILD_ID, pageId);
        indexDB.delEntry(PAGE_ID_TO_PARENT_ID,pageId);
    }

    public void close() throws IOException {
        indexDB.close();
    }


}
