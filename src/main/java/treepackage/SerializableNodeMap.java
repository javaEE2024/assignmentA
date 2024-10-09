package treepackage;

import java.io.*;
import java.util.HashMap;

public class SerializableNodeMap implements Serializable {

    private static final long serialVersionUID = 1L;
    private final HashMap<String, SerializableNode> serializableNodeMap = new HashMap<>();

    public SerializableNodeMap(HashMap<String, Node> originalMap) {
        // 将 Node 转换为 SerializableNode
        for (String key : originalMap.keySet()) {
            Node originalNode = originalMap.get(key);
            SerializableNode serializableNode = new SerializableNode(originalNode);
            serializableNodeMap.put(key, serializableNode);
        }
    }

    public HashMap<String, Node> toOriginalNodeMap() {
        HashMap<String, Node> originalMap = new HashMap<>();
        for (String key : serializableNodeMap.keySet()) {
            SerializableNode serializableNode = serializableNodeMap.get(key);
            Node originalNode = serializableNode.toNode();
            originalMap.put(key, originalNode);
        }
        return originalMap;
    }
}
