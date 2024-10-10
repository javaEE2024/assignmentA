package treepackage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class GitRollback {

    // 回滚到指定版本的历史记录
    public static void rollbackToVersion(String versionHash) {
        /*if (versionIndex < 0 || versionIndex >= GitTree.history.size()) {
            System.out.println("Invalid version index. Cannot rollback.");
            return;
        }

        // 获取历史版本的哈希值和版本名
        HistoryData version = GitTree.history.get(versionIndex);
        String versionHash = version.getHash();

        System.out.println("Rolling back to version: " + version.getName() + " (Hash: " + versionHash + ")");
        */
        // 根据hash恢复文件树
        Node rootNode = Node.get(versionHash);
        if (rootNode != null) {
            // 在恢复文件之前，先删除现有文件
            File rootDirectory = new File(GitTree.pathOfRoot);
            deleteDirectoryContents(rootDirectory); // 删除目录内容
            restoreFilesFromNode(rootNode, rootDirectory); // 恢复文件
        } else {
            System.out.println("Error: Cannot find the specified version in the tree.");
        }
    }

    // 递归删除目录内容，不删除目录本身
    private static void deleteDirectoryContents(File directory) {
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectoryContents(file); // 递归删除子目录内容
                    }
                    if (!file.delete()) {
                        System.err.println("Failed to delete: " + file.getPath());
                    } else {
                        System.out.println("Deleted: " + file.getPath());
                    }
                }
            }
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
