package treepackage;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import static treepackage.FileDiff.compareFiles;
import static treepackage.GitTree.getFileContent;

public class FileViewer {
    private final String fileName;
    private final DefaultMutableTreeNode node;
    private JTextPane textPane;

    public FileViewer(String fileName, DefaultMutableTreeNode node) {
        this.fileName = fileName;
        this.node = node;
    }

    public void openFileWindow() {
        // 创建新窗口
        JFrame frame = new JFrame("文件查看器 - " + fileName);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // 初始化 JTextPane，用来显示文件比较的差异
        textPane = new JTextPane();
        textPane.setEditable(false);  // 设置为不可编辑

        // 中央区域：显示文件内容
        JTextArea fileContentArea = new JTextArea();
        fileContentArea.setEditable(false);  // 文件内容是只读的

        String filePath=getFilePath(node);
        // 设置 JTextPane 的初始内容为文件的当前内容
        String content = getContent(filePath);
        System.out.println(content);  // 打印文件内容
        textPane.setText(content);  // 设置内容到 textPane

        JScrollPane fileContentScroll = new JScrollPane(textPane);
        frame.add(fileContentScroll, BorderLayout.CENTER);
        // 左侧：显示历史版本的侧边栏
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS)); // 垂直排列卡片

        // 遍历历史版本并为每个版本创建卡片

            for(HistoryData hd:GitTree.history) {
                ArrayList<String> filePaths=GitTree.getFilePaths(hd.getHash());
                for(String path:filePaths) {
                    if(filePath.equals(path)) {
                        JPanel card = createVersionCard(filePath,hd);
                        cardPanel.add(card);
                        cardPanel.add(Box.createRigidArea(new Dimension(0, 10))); // 卡片之间的间距
                    }
                }
            }

        JScrollPane historyScrollPane = new JScrollPane(cardPanel);
        historyScrollPane.setPreferredSize(new Dimension(200, 600));  // 设置侧边栏的宽度

        frame.add(historyScrollPane, BorderLayout.WEST);  // 将侧边栏放置在窗口的左侧
        // 显示窗口
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    private JPanel createVersionCard(String filePath,HistoryData historyData) {
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


        // 创建比对按钮
        JButton compareButton = new JButton("比对");
        compareButton.setAlignmentX(Component.LEFT_ALIGNMENT); // 左对齐
        compareButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                String oldHash = historyData.getHash();  // 获取旧版本的Hash
                String newHash = GitTree.history.get(GitTree.history.size() - 1).getHash();  // 当前版本的Hash

                String oldContent = null;
                String newContent = null;
                try {
                    oldContent = getFileContent(oldHash, filePath);  // 获取旧版本文件内容
                    newContent = getFileContent(newHash, filePath);  // 获取新版本文件内容

                    System.out.println("Old:"+oldContent);
                    System.out.println("New:"+newContent);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                // 调用比较方法，将差异高亮显示在 JTextPane 中
                compareFiles(oldContent, newContent, textPane);

                textPane.revalidate();
                textPane.repaint();
            }

        });


        // 创建一个面板来放置按钮
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(compareButton);

        // 将按钮面板添加到卡片中
        card.add(buttonPanel);

        return card;
    }

    // 恢复历史版本的方法
    private void restoreVersion(HistoryData historyData) {
        // 处理版本回退逻辑
        // 你可以调用 GitTree 的相关方法来恢复文件到某个历史版本
        System.out.println("恢复到版本: " + historyData.getName());
    }

    // 生成文件相对于根目录的相对路径
    private String getFilePath(DefaultMutableTreeNode node) {
        StringBuilder filePath = new StringBuilder();
        Object[] path = node.getUserObjectPath();
        for (int i = 0; i < path.length; i++) {
            filePath.append(path[i].toString());
            if (i != path.length - 1) {
                filePath.append("/");
            }
        }
        System.out.println(filePath.toString());
        return filePath.toString();
    }

    // 获取文件的当前内容
    private String getContent(String filePath) {
        String latestCommitHash = GitTree.history.get(GitTree.pointer).getHash();  // 当前版本的hash
        String content = null;

        try {
            // 调用 GitTree 的 getFileContent 方法获取文件内容
            content = getFileContent(latestCommitHash, filePath);
        } catch (IllegalArgumentException | IOException ex) {
            // 文件在旧的提交中不存在
            System.err.println("无法读取文件内容: " + ex.getMessage());
        }

        return content != null ? content : "无法显示文件内容。";
    }
}
