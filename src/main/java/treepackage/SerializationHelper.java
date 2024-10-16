package treepackage;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class SerializationHelper {
    public static int sePointer=-1;
    // 序列化对象
    public static void serializeObject(Object object, String filePath) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath,false))) {
            out.writeObject(object);
            System.out.println("对象已成功序列化到文件：" + filePath);
        } catch (IOException e) {
            System.err.println("序列化失败: " + e.getMessage());
        }
    }

    // 反序列化对象
    public static Object deserializeObject(String filePath) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("反序列化失败: " + e.getMessage());
            return null;
        }
    }


    // 序列化整个历史数据列表
    public static void serializeHistoryData(ArrayList<HistoryData> history, String filePath) {
        ArrayList<SerializableHistoryData> serializableHistoryList = new ArrayList<>();
        for (HistoryData data : history) {
            // 使用代理类来包装 HistoryData 对象
            serializableHistoryList.add(new SerializableHistoryData(data));

        }

        // 序列化代理类列表
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath, false))) {
            out.writeObject(serializableHistoryList);  // 将序列化对象写入文件
            System.out.println("历史数据已成功序列化到文件：" + filePath);
        } catch (IOException e) {
            System.err.println("序列化失败: " + e.getMessage());
        }
    }

    // 反序列化整个历史数据列表
    @SuppressWarnings("unchecked")
    public static ArrayList<HistoryData> deserializeHistoryData(String filePath) {
        // 反序列化代理类列表
        ArrayList<SerializableHistoryData> serializableHistoryList =
                (ArrayList<SerializableHistoryData>) deserializeObject(filePath);

        ArrayList<HistoryData> historyList = new ArrayList<>();
        if (serializableHistoryList != null) {
            for (SerializableHistoryData serializableData : serializableHistoryList) {
                // 将代理类转换回 HistoryData 对象
                historyList.add(serializableData.toHistoryData());
                sePointer++;
            }
        }
        return historyList;
    }

    public static void serializeNodes(ArrayList<Node> nodes, String filePath) {
        ArrayList<SerializableNode> serializableNodes = new ArrayList<>();
        for (Node node : nodes) {
            serializableNodes.add(new SerializableNode(node));
        }

        serializeObject(serializableNodes, filePath);
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Node> deserializeNodes(String filePath) {
        ArrayList<Node> nodes = new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            ArrayList<SerializableNode> serializableNodes = (ArrayList<SerializableNode>) in.readObject();
            for (SerializableNode serializableNode : serializableNodes) {
                nodes.add(serializableNode.toNode());
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("反序列化失败: " + e.getMessage());
            e.printStackTrace();
        }
        return nodes;
    }

    public static void serializeMetaDatas(ArrayList<MetaData> metaDatas, String filePath) {
        ArrayList<SerializableMetaData> serializableMetaDatas = new ArrayList<>();
        for (MetaData metaData : metaDatas) {
            serializableMetaDatas.add(new SerializableMetaData(metaData));
        }

        serializeObject(serializableMetaDatas, filePath);
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<MetaData> deserializeMetaDatas(String filePath) {
        ArrayList<MetaData> metaDatas = new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            ArrayList<SerializableMetaData> serializableMetaDatas = (ArrayList<SerializableMetaData>) in.readObject();
            for (SerializableMetaData serializableMetaData : serializableMetaDatas) {
                metaDatas.add(serializableMetaData.toMetaData());
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("反序列化失败: " + e.getMessage());
            e.printStackTrace();
        }
        return metaDatas;
    }
    public static void serializeNodeMap(HashMap<String, Node> nodeMap, String filePath) {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            SerializableNodeMap serializableNodeMap = new SerializableNodeMap(nodeMap);
            oos.writeObject(serializableNodeMap);
            System.out.println("nodeMap has been serialized successfully.");
        } catch (IOException e) {
            System.err.println("Serialization failed: " + e.getMessage());
        }
    }

    public static HashMap<String, Node> deserializeNodeMap(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            SerializableNodeMap serializableNodeMap = (SerializableNodeMap) ois.readObject();
            return serializableNodeMap.toOriginalNodeMap();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Deserialization failed: " + e.getMessage());
        }
        return new HashMap<>();
    }
}