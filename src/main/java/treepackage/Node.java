package treepackage;

import java.util.HashMap;
import java.util.ArrayList;

public class Node {
    private final MetaData metaData;
    public static HashMap<String, Node> nodeMap = new HashMap<>();
    private Node parent;

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

    public String getName() {
        return getValue().fileName();
    }

    public void addChild(String childHash) {
        child.add(childHash);
        Node childNode = get(childHash);
        if (childNode != null) {
            childNode.setParent(this);
        }
    }

    public void setParent(Node parent) {
        this.parent = parent;
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

    public Node getChildByName(String name) {
        for (int i = 0; i < getItemsCount(); i++) {
            Node childNode = getChildAt(i);
            if (childNode.getName().equals(name)) {
                return childNode;
            }
        }
        return null;
    }

    // 根据相对路径获取文件节点
    public Node getNodeByPath(String relativePath) {
        return getNodeByPathHelper(relativePath.split("/"), 0);
    }

    private Node getNodeByPathHelper(String[] pathParts, int index) {
        if (index >= pathParts.length) {
            return null;
        }

        if (!this.getName().equals(pathParts[index])) {
            return null;
        }

        if (index == pathParts.length - 1) {
            return this;
        }

        for (int i = 0; i < this.getItemsCount(); i++) {
            Node child = this.getChildAt(i);
            Node result = child.getNodeByPathHelper(pathParts, index + 1);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}
