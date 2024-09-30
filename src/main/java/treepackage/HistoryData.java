package treepackage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class HistoryData {
    private static int count=0;
    private String name;
    private final String creationDate;
    private final String hash;
    public HistoryData(String hash) {
        this.name ="第"+String.valueOf(++count)+"版";
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.creationDate = currentTime.format(formatter);
        this.hash = hash;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCreationDate() {
        return creationDate;
    }
    public String getHash() {
        return hash;
    }
}
