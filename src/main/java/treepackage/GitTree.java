package treepackage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.tree.DefaultMutableTreeNode;
import com.intellij.openapi.project.Project;

public class GitTree {
    public static ArrayList<HistoryData>history = new ArrayList<>();
    public static  int pointer=-1;
    public static String pathOfRoot;
    private static String pathOfRepo;
    public static void init(Project project) {
        pathOfRoot=project.getBasePath();
        if (pathOfRoot != null) {
            pathOfRepo=new File(pathOfRoot).getParent()+File.separator+"repository";
        }
        String pathOfSerializableHistoryData=pathOfRepo+File.separator+"HistoryData.ser";
        ArrayList<HistoryData>oldHistory=SerializationHelper.deserializeHistoryData(pathOfSerializableHistoryData);
        history.addAll(oldHistory);
        String pathOfSerializableNodeMap=pathOfRepo+File.separator+"NodeMap.ser";
        HashMap<String, Node> oldNodeMap=SerializationHelper.deserializeNodeMap(pathOfSerializableNodeMap);
        Node.nodeMap.putAll(oldNodeMap);
        File repos=new File(pathOfRepo);
        if(!repos.mkdir()){
            System.out.println("Directory has benn created.");
        }
    }
    public static void newCommit(){
        File rootDir = new File(pathOfRoot);
        String rootNode=buildFileTree(rootDir);
        history.add(new HistoryData(rootNode));
        pointer++;
    }
    private static String buildFileTree(File dir) {
        if(dir.isFile()){
            String newHash=calculateSHA256(dir);
            if(!Node.containsKey(newHash)){
                new Node(newHash,dir.getName(),true);
                try {
                    Files.copy(Path.of(dir.getAbsolutePath()),Path.of(pathOfRepo+File.separator+newHash), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    System.err.println("文件复制失败: " + e.getMessage());
                }
            }
            return newHash;
        }
        ArrayList<String>hashSet=new ArrayList<>();
        StringBuilder sb=new StringBuilder();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                String childHash =buildFileTree(file);
                Node child=Node.get(childHash);
                sb.append(child.getName()).append(child.isFile()).append(childHash);
                hashSet.add(childHash);
            }
        }
        String newHash=calculateSHA256(sb.toString());
        if(!Node.containsKey(newHash)){
            Node father = new Node(newHash,dir.getName(),false);
            for (String s : hashSet) {
                father.addChild(s);
            }
        }
        return newHash;
    }

    public static DefaultMutableTreeNode toTree(String rootHash) {
        Node root=Node.get(rootHash);
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(root.getValue());
        for(int i=0;i<root.getItemsCount();i++){
            treeNode.add(toTree(root.getHashAt(i)));
        }
        return treeNode;
    }
    public static Path getFilePath(String hash) {
        return Path.of(pathOfRepo+File.separator+hash);
    }
    private static String calculateSHA256(File file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] byteArray = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(byteArray)) != -1) {
                    digest.update(byteArray, 0, bytesRead);
                }
            }
            StringBuilder sb = new StringBuilder();
            for (byte b : digest.digest()) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (IOException e) {
            System.err.println("文件读取错误: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.err.println("算法不可用: " + e.getMessage());
        }
        return "";
    }
    private static String calculateSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("算法不可用: " + e.getMessage());
        }
        return "";
    }

}