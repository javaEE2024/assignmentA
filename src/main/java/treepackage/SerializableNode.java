package treepackage;

import java.io.Serializable;
import java.util.ArrayList;

public class SerializableNode implements Serializable {
    private final String hash;
    private final String name;
    private final boolean isFile;
    private final ArrayList<String> childHashes;

    public SerializableNode(Node node) {
        this.hash = node.getValue().hash();
        this.name = node.getValue().fileName();
        this.isFile = node.isFile();
        this.childHashes = new ArrayList<>();
        for (int i = 0; i < node.getItemsCount(); i++) {
            childHashes.add(node.getHashAt(i));
        }
    }

    // 将 SerializableNode 转换回 Node 对象
    public Node toNode() {
        Node newNode = new Node(hash, name, isFile);
        for (String childHash : childHashes) {
            newNode.addChild(childHash);
        }
        return newNode;
    }

    public String getHash() {
        return hash;
    }

    public String getName() {
        return name;
    }

    public boolean isFile() {
        return isFile;
    }

    public ArrayList<String> getChildHashes() {
        return childHashes;
    }
}