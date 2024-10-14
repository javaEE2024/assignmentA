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
    private JFrame frame;
    private JPanel cardPanel;
    private JTextPane textPane;
    private JPanel currentPanel;  // 保存当前显示的面板

    public FileViewer(String fileName, DefaultMutableTreeNode node) {
        this.fileName = fileName;
        this.node = node;
    }

    public void openFileWindow() {
        // 创建新窗口
        frame = new JFrame("文件查看器 - " + fileName);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // 初始化 JTextPane，用来显示文件比较的差异
        textPane = new JTextPane();
        textPane.setEditable(false);  // 设置为不可编辑

        
        cardPanel = new JPanel();

        // 创建主面板
        currentPanel = new JPanel(new BorderLayout());
        frame.add(currentPanel, BorderLayout.CENTER);

        // 显示当前文件内容
        showCurrentFileContent();

        // 显示窗口
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    private void showCurrentFileContent() {
        currentPanel.removeAll();  // 清除当前显示内容

        String filePath = getFilePath(node);
        // 设置 JTextPane 的初始内容为文件的当前内容
        String content = getContent(filePath);
        System.out.println(content);  // 打印文件内容
        textPane.setText(content);  // 设置内容到 textPane

        JScrollPane fileContentScroll = new JScrollPane(textPane);
        currentPanel.add(fileContentScroll, BorderLayout.CENTER);

        // 左侧：显示历史版本的侧边栏
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS)); // 垂直排列卡片

        // 遍历历史版本并为每个版本创建卡片
        for (HistoryData hd : GitTree.history) {
            ArrayList<String> filePaths = GitTree.getFilePaths(hd.getHash());
            for (String path : filePaths) {
                if (filePath.equals(path)) {
                    JPanel card = createVersionCard(filePath, hd);
                    cardPanel.add(card);
                    cardPanel.add(Box.createRigidArea(new Dimension(0, 10))); // 卡片之间的间距
                }
            }
        }

        JScrollPane historyScrollPane = new JScrollPane(cardPanel);
        historyScrollPane.setPreferredSize(new Dimension(200, 600));  // 设置侧边栏的宽度

        currentPanel.add(historyScrollPane, BorderLayout.WEST);  // 将侧边栏放置在窗口的左侧
        currentPanel.revalidate();
        currentPanel.repaint();
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

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                // 显示比对结果
                showDiffResult(oldContent, newContent);
            }

        });


        // 创建一个面板来放置按钮
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(compareButton);

        // 将按钮面板添加到卡片中
        card.add(buttonPanel);

        return card;
    }
    private void showDiffResult(String oldContent, String newContent) {
        currentPanel.removeAll(); // 清除当前显示的内容

        // 调用比较方法，将差异高亮显示在 JTextPane 中
        compareFiles(oldContent, newContent, textPane);

        // 创建返回按钮
        JButton returnButton = new JButton("返回");
        returnButton.addActionListener(e -> {
            showCurrentFileContent();  // 切换回文件内容和历史卡片的界面
            returnButton.setVisible(false);  // 隐藏返回按钮
        });

        // 创建一个面板，将比对结果和返回按钮加入其中
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.add(new JScrollPane(textPane), BorderLayout.CENTER);
        resultPanel.add(returnButton, BorderLayout.SOUTH);

        // 添加比对结果到主面板
        currentPanel.add(resultPanel);
        currentPanel.revalidate();
        currentPanel.repaint();
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
        String content = null;

        // 从当前版本倒序遍历历史版本
        for (int i = GitTree.pointer; i >= 0; i--) {
            String commitHash = GitTree.history.get(i).getHash();  // 获取当前遍历到的提交 hash

            try {
                // 调用 GitTree 的 getFileContent 方法获取文件内容
                content = getFileContent(commitHash, filePath);
                if (content != null) {
                    // 找到文件内容则返回
                    return content;
                }
            } catch (IllegalArgumentException | IOException ex) {
                // 文件在当前提交中不存在，继续尝试之前的版本
                System.err.println("无法读取提交 " + commitHash + " 中的文件内容: " + ex.getMessage());
            }
        }

        // 如果遍历完所有历史版本，仍然找不到文件，返回提示
        return "无法显示文件内容。";
    }

}
