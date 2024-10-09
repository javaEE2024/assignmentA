package treepackage;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.tree.DefaultMutableTreeNode;

public class FileTreeDiff {

    public static DefaultMutableTreeNode diffTrees(String oldRootHash, String newRootHash) {
        Node oldRoot = Node.get(oldRootHash);
        Node newRoot = Node.get(newRootHash);
        return diffNodes(oldRoot, newRoot);
    }

    private static ColoredTreeNode diffNodes(Node oldNode, Node newNode) {
        if (oldNode == null && newNode != null) {
            // 新增的节点，蓝色
            ColoredTreeNode node = new ColoredTreeNode("+ " + newNode.getName(), Color.BLUE);
            for (int i = 0; i < newNode.getItemsCount(); i++) {
                node.add(diffNodes(null, newNode.getChildAt(i)));
            }
            return node;
        } else if (oldNode != null && newNode == null) {
            // 删除的节点，红色
            ColoredTreeNode node = new ColoredTreeNode("- " + oldNode.getName(), Color.RED);
            for (int i = 0; i < oldNode.getItemsCount(); i++) {
                node.add(diffNodes(oldNode.getChildAt(i), null));
            }
            return node;
        } else if (oldNode != null && newNode != null) {
            if (!oldNode.getValue().hash().equals(newNode.getValue().hash())) {
                ColoredTreeNode node;
                if (oldNode.isFile() && newNode.isFile()) {
                    // 修改的文件，黄色
                    node = new ColoredTreeNode("~ " + newNode.getName(), Color.YELLOW);
                } else if (!oldNode.isFile() && !newNode.isFile()) {
                    // 目录，检查子节点变化
                    node = new ColoredTreeNode(newNode.getName(), Color.BLACK);
                    HashSet<String> allChildNames = new HashSet<>();
                    for (int i = 0; i < oldNode.getItemsCount(); i++) {
                        allChildNames.add(oldNode.getChildAt(i).getName());
                    }
                    for (int i = 0; i < newNode.getItemsCount(); i++) {
                        allChildNames.add(newNode.getChildAt(i).getName());
                    }
                    for (String childName : allChildNames) {
                        Node oldChild = oldNode.getChildByName(childName);
                        Node newChild = newNode.getChildByName(childName);
                        ColoredTreeNode childDiff = diffNodes(oldChild, newChild);
                        if (childDiff != null) {
                            node.add(childDiff);
                        }
                    }
                } else {
                    // 类型改变，黄色
                    node = new ColoredTreeNode("~ " + newNode.getName(), Color.YELLOW);
                }
                return node;
            }
            // 节点相同，但子节点可能有变化
            if (!oldNode.isFile() && !newNode.isFile()) {
                ColoredTreeNode node = new ColoredTreeNode(newNode.getName(), Color.BLACK);
                boolean hasChanges = false;
                HashSet<String> allChildNames = new HashSet<>();
                for (int i = 0; i < oldNode.getItemsCount(); i++) {
                    allChildNames.add(oldNode.getChildAt(i).getName());
                }
                for (int i = 0; i < newNode.getItemsCount(); i++) {
                    allChildNames.add(newNode.getChildAt(i).getName());
                }
                for (String childName : allChildNames) {
                    Node oldChild = oldNode.getChildByName(childName);
                    Node newChild = newNode.getChildByName(childName);
                    ColoredTreeNode childDiff = diffNodes(oldChild, newChild);
                    if (childDiff != null) {
                        node.add(childDiff);
                        hasChanges = true;
                    }
                }
                return hasChanges ? node : null;
            }
            // 文件且未变化
            return null;
        }
        return null;
    }
}
