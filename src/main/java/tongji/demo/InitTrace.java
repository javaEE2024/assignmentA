package tongji.demo;

import com.github.difflib.patch.Patch;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.DeltaType;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;


import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.ui.treeStructure.Tree;
import treepackage.*;


public class InitTrace extends AnAction {
    private JPanel panel;
    private JPanel cardPanel; // 保持对卡片面板的引用
    private JTextPane textPane; // 确保声明了这个变量
    private StyledDocument doc;
    private Tree tree;
    AutoCommitTimer autoCommit;

    @Override
    public void actionPerformed(AnActionEvent e) {
        GitTree.init(Objects.requireNonNull(e.getProject()));
        GitTree.newCommit();
        tree = new Tree(GitTree.toTree(GitTree.history.get(GitTree.pointer).getHash()));
        // 确保树中的节点已正确添加并显示
        System.out.println("Tree initialized with root: " + tree.getModel().getRoot());

        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                System.out.println("TreeSelectionListener triggered."); // 确认监听器被触发
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                System.out.println("Selected Node: " + selectedNode); // 打印选中的节点
                if (selectedNode == null) {
                    System.out.println("No node selected.");
                    return;
                }

                // 判断是否是文件节点
                if (selectedNode.isLeaf()) {
                    String fileName = selectedNode.toString();
                    // 获取文件当前内容并打开窗口
                    FileViewer fileViewer = new FileViewer(fileName, selectedNode);
                    fileViewer.openFileWindow();
                }
            }


        });


        // 初始化主面板和侧边栏
        panel = new JPanel(new BorderLayout());
        panel.add(tree, BorderLayout.CENTER);

        // 创建卡片面板
        cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS)); // 垂直排列卡片

        // 创建卡片
        for (HistoryData historyData : GitTree.history) {
            JPanel card = createVersionCard(historyData);
            cardPanel.add(card);
            cardPanel.add(Box.createRigidArea(new Dimension(0, 10))); // 卡片之间的间距
        }

        JScrollPane scrollPane = new JScrollPane(cardPanel);
        panel.add(scrollPane, BorderLayout.EAST); // 将卡片面板添加到面板的右侧

        // 创建提交按钮
        JButton commitButton = new JButton("提交新版本");
        commitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 调用 GitTree 的 newCommit 方法
                GitTree.newCommit(); // 提交新版本

                // 提交后刷新历史版本卡片
                updateTreeView();
                refreshCards();
                JOptionPane.showMessageDialog(panel, "新版本已提交");
            }
        });

        panel.add(commitButton, BorderLayout.SOUTH); // 将按钮添加到面板底部

        // 显示该面板
        JFrame frame = new JFrame("Git Tree Viewer");
        frame.setContentPane(panel);
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

        //启动定时器
        autoCommit = new AutoCommitTimer(this);
    }


    private JPanel createVersionCard(HistoryData historyData) {
        // 创建卡片面板
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1)); // 添加边框
        card.setBackground(Color.WHITE); // 设置背景色
        card.setPreferredSize(new Dimension(200, 100)); // 设置卡片大小

        // 创建版本号标签
        JLabel versionLabel = new JLabel(historyData.getName());
        versionLabel.setFont(new Font("Arial", Font.BOLD, 16)); // 设置字体
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // 居中对齐
        card.add(versionLabel);

        // 创建提交时间标签
        JLabel dateLabel = new JLabel(historyData.getCreationDate());
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 12)); // 设置字体
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // 居中对齐
        card.add(dateLabel);

        // 创建恢复按钮
        JButton restoreButton = new JButton("恢复");
        restoreButton.setAlignmentX(Component.LEFT_ALIGNMENT); // 左对齐
        restoreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 处理恢复到指定版本的逻辑
                restoreToVersion(historyData.getHash());
            }
        });

        // 创建比对按钮
        JButton compareButton = new JButton("比对");
        compareButton.setAlignmentX(Component.LEFT_ALIGNMENT); // 左对齐
        compareButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 处理比对逻辑
                compareWithCurrentVersion(historyData.getHash());
            }
        });

        // 创建一个面板来放置按钮
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(restoreButton);
        buttonPanel.add(compareButton);

        // 将按钮面板添加到卡片中
        card.add(buttonPanel);

        return card;
    }

    public void updateTreeView() {
        panel.remove(tree);
        tree = new Tree(GitTree.toTree(GitTree.history.get(GitTree.pointer).getHash()));
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                System.out.println("TreeSelectionListener triggered."); // 确认监听器被触发
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                System.out.println("Selected Node: " + selectedNode); // 打印选中的节点
                if (selectedNode == null) {
                    System.out.println("No node selected.");
                    return;
                }

                // 判断是否是文件节点
                if (selectedNode.isLeaf()) {
                    String fileName = selectedNode.toString();
                    // 获取文件当前内容并打开窗口
                    FileViewer fileViewer = new FileViewer(fileName, selectedNode);
                    fileViewer.openFileWindow();
                }
            }


        });
        panel.add(tree, BorderLayout.CENTER);
    }

    public void refreshCards() {
        // 清空当前卡片面板
        cardPanel.removeAll();

        // 创建卡片
        for (HistoryData historyData : GitTree.history) {
            JPanel card = createVersionCard(historyData);
            cardPanel.add(card);
            cardPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        // 更新界面
        cardPanel.revalidate();
        cardPanel.repaint();
    }


    private void restoreToVersion(String hash) {
        GitRollback.rollbackToVersion(hash);
        VirtualFileManager.getInstance().refreshWithoutFileWatcher(true);
    }

    private void writeFileContent(String filePath, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
        }
    }

    private void compareWithCurrentVersion(String oldHash) {

        HistoryData currentVersion = GitTree.history.get(GitTree.history.size() - 1);
        String newHash = currentVersion.getHash();

        System.out.println("Old:" + oldHash);
        System.out.println("New:" + newHash);

        // 调用 FileTreeDiff.diffTrees 进行文件树对比
        DefaultMutableTreeNode diffTree = FileTreeDiff.diffTrees(oldHash, newHash);

        // 使用新生成的差异树更新显示
        tree.setModel(new DefaultTreeModel(diffTree)); // 更新树模型以显示差异
        tree.setCellRenderer(new ColoredTreeCellRenderer());
        tree.setModel(new DefaultTreeModel(diffTree)); // 这里使用 diffTree 作为模型

    }

}