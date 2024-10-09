package treepackage;

import java.util.HashMap;
import java.util.ArrayList;
public class Node{
    private final MetaData metaData;
    public static HashMap<String, Node> nodeMap = new HashMap<>();

    public static boolean containsKey(String Key) {
        return nodeMap.containsKey(Key);
    }

    public static Node get(String Key) {
        return nodeMap.get(Key);
    }

    private final ArrayList<String> child = new ArrayList<>();

    public Node(String hash, String name, boolean isFile) {
        metaData = new MetaData(hash, name, isFile);
        nodeMap.put(hash, this);
    }


    public MetaData getValue() {
        return metaData;
    }

    public String getName(){
        return getValue().fileName();
    }

    public void addChild(String childHash) {
        child.add(childHash);
    }

    public int getItemsCount() {
        return child.size();
    }

    public Node getChildAt(int index) {
        return nodeMap.get(child.get(index));
    }

    public String getHashAt(int index) {
        return child.get(index);
    }

    public boolean isFile() {
        return metaData.isFile();
    }

}