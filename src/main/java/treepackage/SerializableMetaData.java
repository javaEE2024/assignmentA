package treepackage;
import java.io.Serializable;

public class SerializableMetaData implements Serializable {
    private final String hash;
    private final String fileName;
    private final boolean isFile;

    public SerializableMetaData(MetaData metaData) {
        this.hash = metaData.hash();
        this.fileName = metaData.fileName();
        this.isFile = metaData.isFile();
    }

    // 将 SerializableMetaData 转换回 MetaData 对象
    public MetaData toMetaData() {
        return new MetaData(hash, fileName, isFile);
    }

    public String getHash() {
        return hash;
    }

    public String getFileName() {
        return fileName;
    }

    public boolean isFile() {
        return isFile;
    }
}