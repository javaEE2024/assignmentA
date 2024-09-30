package tongji.demo;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import java.awt.*;
import java.util.Objects;

import com.intellij.ui.treeStructure.Tree;
import treepackage.GitTree;

import javax.swing.*;

public class InitTrace extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        GitTree.init(Objects.requireNonNull(e.getProject()));
        GitTree.newCommit();
        Tree tree = new Tree(GitTree.toTree(GitTree.history.get(GitTree.pointer).getHash()));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(tree, BorderLayout.CENTER);

        // 显示该面板
        JFrame frame = new JFrame("Git Tree Viewer");
        frame.setContentPane(panel);
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

    }
}
