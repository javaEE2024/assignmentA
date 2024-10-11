package treepackage;
import java.util.ArrayList;

public class SerializationDemo {
    public static void main(String[] args) {
        // 创建一些数据
        HistoryData data1 = new HistoryData("hash1");
        HistoryData data2 = new HistoryData("hash2");
        ArrayList<HistoryData> historyList = new ArrayList<>();
        historyList.add(data1);
        historyList.add(data2);

// 序列化 HistoryData 列表到文件
        SerializationHelper.serializeHistoryData(historyList, "D:\\code\\shiyan\\h.ser");
        ArrayList<HistoryData> restoredHistoryList = SerializationHelper.deserializeHistoryData("D:\\code\\shiyan\\h.ser");

        ArrayList<Node> nodes = new ArrayList<>();

        MetaData metaData1 = new MetaData("hash123", "file.txt", true);
        ArrayList<MetaData> metaDataList = new ArrayList<>();
        metaDataList.add(metaData1);
// 序列化 MetaData 到文件
        SerializationHelper.serializeMetaDatas(metaDataList, "D:\\code\\shiyan\\hdm.txt");
        ArrayList<MetaData>restoredMetaData=SerializationHelper.deserializeMetaDatas("D:\\code\\shiyan\\hdm.txt");

// 假设已经创建了 Node 对象并添加到 nodes 列表中

        SerializationHelper.serializeNodes(nodes, "D:\\code\\shiyan\\hd.txt");
        ArrayList<Node> restoredNodes = SerializationHelper.deserializeNodes("D:\\code\\shiyan\\hd.txt");
    }
}
