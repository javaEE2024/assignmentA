package tongji.demo;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import treepackage.FileTreeDiff;
import treepackage.GitTree;
import treepackage.HistoryData;
import com.intellij.ui.treeStructure.Tree;

public class DiffTrace extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        if (GitTree.history.size() < 2) {
            JOptionPane.showMessageDialog(null, "需要至少两个提交来查看文件树差异", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 设置全局字体
        UIManager.put("ComboBox.font", new Font("微软雅黑", Font.PLAIN, 12));

        String[] commitOptions = GitTree.history.stream()
                .map(HistoryData::getName)
                .toArray(String[]::new);

        String oldCommitName = (String) JOptionPane.showInputDialog(null, "选择旧的提交:", "提交选择",
                JOptionPane.QUESTION_MESSAGE, null, commitOptions, commitOptions[0]);

        String newCommitName = (String) JOptionPane.showInputDialog(null, "选择新的提交:", "提交选择",
                JOptionPane.QUESTION_MESSAGE, null, commitOptions, commitOptions[1]);

        if (oldCommitName == null || newCommitName == null) {
            // 用户取消了选择
            return;
        }

        String oldHash = getHashByName(oldCommitName);
        String newHash = getHashByName(newCommitName);

        if (oldHash == null || newHash == null || oldHash.equals(newHash)) {
            JOptionPane.showMessageDialog(null, "请选择不同的两个提交", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DefaultMutableTreeNode diffTree = FileTreeDiff.diffTrees(oldHash, newHash);
        if (diffTree == null) {
            JOptionPane.showMessageDialog(null, "两个提交之间没有差异", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Tree tree = new Tree(diffTree);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(tree), BorderLayout.CENTER);

        JFrame frame = new JFrame("文件树差异查看器");
        frame.setContentPane(panel);
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    private String getHashByName(String name) {
        for (HistoryData data : GitTree.history) {
            if (data.getName().equals(name)) {
                return data.getHash();
            }
        }
        return null;
    }
}
