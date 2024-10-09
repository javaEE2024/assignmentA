package treepackage;

import java.io.Serializable;
public class SerializableHistoryData implements Serializable {
    private String name;
    private String creationDate;
    private String hash;

    // 将 HistoryData 的数据传递给 SerializableHistoryData
    public SerializableHistoryData(HistoryData historyData) {
        this.name = historyData.getName();
        this.creationDate = historyData.getCreationDate();
        this.hash = historyData.getHash();
    }

    // 从 SerializableHistoryData 创建 HistoryData 对象
    public HistoryData toHistoryData() {
        HistoryData historyData = new HistoryData(this.hash);
        historyData.setName(this.name);
        return historyData;
    }

    // Getter 和 Setter 方法
    public String getName() {
        return name;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getHash() {
        return hash;
    }
}