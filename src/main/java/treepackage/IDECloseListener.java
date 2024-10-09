package treepackage;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import java.io.File;

public class IDECloseListener implements ApplicationListener<ContextClosedEvent> {
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        // 当 IDE 关闭时执行的逻辑
        String pathOfSerializableHistoryData=GitTree.pathOfRepo+ File.separator+"HistoryData.ser";
        String pathOfSerializableNodeMap=GitTree.pathOfRepo+File.separator+"NodeMap.ser";
        SerializationHelper.serializeHistoryData(GitTree.history,pathOfSerializableHistoryData);
        SerializationHelper.serializeNodeMap(Node.nodeMap,pathOfSerializableNodeMap);
        System.out.println("IDE is closing, saving files...");
        // 在这里执行保存操作
    }
}


