package treepackage;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class MyProjectManagerListener implements ProjectManagerListener {
    @Override
    public void projectClosing(@NotNull Project project) {
        // 项目关闭时执行操作


        String pathOfSerializableHistoryData=GitTree.pathOfRepo+ File.separator+"HistoryData.ser";
        String pathOfSerializableNodeMap=GitTree.pathOfRepo+File.separator+"NodeMap.ser";
        SerializationHelper.serializeHistoryData(GitTree.history,pathOfSerializableHistoryData);
        SerializationHelper.serializeNodeMap(Node.nodeMap,pathOfSerializableNodeMap);
        System.out.println("Project " + project.getName() + " is closing.");

    }


}
