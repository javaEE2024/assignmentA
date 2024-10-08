package treepackage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class GitRollback {

    // 回滚到指定版本的历史记录
    public static void rollbackToVersion(String versionHash) {

        // 根据hash恢复文件树
        Node rootNode = Node.get(versionHash);
        if (rootNode != null) {
            restoreFilesFromNode(rootNode, new File(GitTree.pathOfRoot));
        } else {
            System.out.println("Error: Cannot find the specified version in the tree.");
        }
    }

    // 递归恢复文件
    private static void restoreFilesFromNode(Node node, File destination) {
        if (node.isFile()) {
            // 复制文件
            try {
                Path sourcePath = GitTree.getFilePath(node.getValue().hash());
                Path destinationPath = destination.toPath();
                Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Restored file: " + destinationPath);
            } catch (IOException e) {
                System.err.println("Error restoring file: " + node.getName() + ". " + e.getMessage());
            }
        } else {
            // 如果是目录，创建目录并递归处理其子文件
            if (!destination.exists() && !destination.mkdir()) {
                System.err.println("Failed to create directory: " + destination.getPath());
                return;
            }

            for (int i = 0; i < node.getItemsCount(); i++) {
                Node childNode = node.getChildAt(i);
                File childFile = new File(destination, childNode.getName());
                restoreFilesFromNode(childNode, childFile);
            }
        }
    }
}