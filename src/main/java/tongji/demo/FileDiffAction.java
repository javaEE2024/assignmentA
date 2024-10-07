package tongji.demo;

import com.github.difflib.patch.DeltaType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import javax.swing.*;
import treepackage.GitTree;
import treepackage.HistoryData;
import treepackage.FileDiff;
import com.github.difflib.patch.Patch;
import com.github.difflib.patch.AbstractDelta;
import com.intellij.openapi.ui.Messages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class FileDiffAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // 检查是否有至少两个提交
        if (GitTree.history.size() < 2) {
            Messages.showMessageDialog("需要至少两个提交来查看文件差异", "提示", Messages.getInformationIcon());
            return;
        }

        // 获取所有提交名称
        String[] commitOptions = GitTree.history.stream()
                .map(HistoryData::getName)
                .toArray(String[]::new);

        // 选择旧的提交
        String oldCommitName = showCommitSelectionDialog("选择旧的提交", commitOptions);
        if (oldCommitName == null) return;

        // 选择新的提交
        String newCommitName = showCommitSelectionDialog("选择新的提交", commitOptions);
        if (newCommitName == null) return;

        String oldHash = getHashByName(oldCommitName);
        String newHash = getHashByName(newCommitName);

        if (oldHash.equals(newHash)) {
            Messages.showMessageDialog("请选择不同的两个提交", "错误", Messages.getErrorIcon());
            return;
        }

        // 获取两个提交中的文件列表
        List<String> oldCommitFiles = GitTree.getFilePaths(oldHash);
        List<String> newCommitFiles = GitTree.getFilePaths(newHash);

        // 合并文件列表，消除重复
        Set<String> allFiles = new TreeSet<>(oldCommitFiles);
        allFiles.addAll(newCommitFiles);

        // 将文件列表转换为数组
        String[] fileOptions = allFiles.toArray(new String[0]);

        // 让用户选择要比较的文件
        String selectedFile = showFileSelectionDialog("请选择要比较的文件", fileOptions);
        if (selectedFile == null) return;

        try {
            String oldContent = null;
            String newContent = null;

            try {
                oldContent = GitTree.getFileContent(oldHash, selectedFile);
            } catch (IllegalArgumentException ex) {
                // 文件在旧的提交中不存在
            }

            try {
                newContent = GitTree.getFileContent(newHash, selectedFile);
            } catch (IllegalArgumentException ex) {
                // 文件在新的提交中不存在
            }

            if (oldContent == null && newContent == null) {
                Messages.showMessageDialog("文件在两个提交中都不存在", "错误", Messages.getErrorIcon());
                return;
            } else if (oldContent == null) {
                Messages.showMessageDialog("文件在旧的提交中不存在，已新增", "提示", Messages.getInformationIcon());
                showFileContent("文件新增内容", newContent);
                return;
            } else if (newContent == null) {
                Messages.showMessageDialog("文件在新的提交中不存在，已删除", "提示", Messages.getInformationIcon());
                showFileContent("文件已被删除", oldContent);
                return;
            }

            // 比较文件内容
            Patch<String> patch = FileDiff.diffFiles(oldContent, newContent);

            // 显示差异
            showDiffResult(patch);

        } catch (IOException ex) {
            Messages.showMessageDialog("读取文件时出错：" + ex.getMessage(), "错误", Messages.getErrorIcon());
        }
    }

    private String getHashByName(String name) {
        for (HistoryData data : GitTree.history) {
            if (data.getName().equals(name)) {
                return data.getHash();
            }
        }
        return null;
    }

    private String showCommitSelectionDialog(String title, String[] commitOptions) {
        String selectedCommitName = (String) JOptionPane.showInputDialog(
                null, title, "提交选择",
                JOptionPane.PLAIN_MESSAGE, null,
                commitOptions, commitOptions[0]);

        if (selectedCommitName == null || selectedCommitName.isEmpty()) {
            return null;
        }
        return selectedCommitName;
    }

    private String showFileSelectionDialog(String title, String[] fileOptions) {
        String selectedFileName = (String) JOptionPane.showInputDialog(
                null, title, "文件选择",
                JOptionPane.PLAIN_MESSAGE, null,
                fileOptions, fileOptions[0]);

        if (selectedFileName == null || selectedFileName.isEmpty()) {
            return null;
        }
        return selectedFileName;
    }

    private void showFileContent(String title, String content) {
        JTextArea textArea = new JTextArea(content);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(null, scrollPane, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void showDiffResult(Patch<String> patch) {
        StringBuilder diffText = new StringBuilder();

        for (AbstractDelta<String> delta : patch.getDeltas()) {
            DeltaType type = delta.getType();
            switch (type) {
                case INSERT:
                    diffText.append("新增行：\n");
                    delta.getTarget().getLines().forEach(line -> diffText.append("+ ").append(line).append("\n"));
                    break;
                case DELETE:
                    diffText.append("删除行：\n");
                    delta.getSource().getLines().forEach(line -> diffText.append("- ").append(line).append("\n"));
                    break;
                case CHANGE:
                    diffText.append("修改前的行：\n");
                    delta.getSource().getLines().forEach(line -> diffText.append("- ").append(line).append("\n"));
                    diffText.append("修改后的行：\n");
                    delta.getTarget().getLines().forEach(line -> diffText.append("+ ").append(line).append("\n"));
                    break;
                default:
                    break;
            }
            diffText.append("\n");
        }

        JTextArea textArea = new JTextArea(diffText.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(null, scrollPane, "文件差异结果", JOptionPane.INFORMATION_MESSAGE);
    }
}
