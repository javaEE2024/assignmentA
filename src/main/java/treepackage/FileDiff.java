package treepackage;

import java.util.Arrays;
import java.util.List;
import com.github.difflib.DiffUtils;
import com.github.difflib.patch.Patch;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.DeltaType;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class FileDiff {

    public static Patch<String> diffFiles(String oldContent, String newContent) {
        List<String> original = Arrays.asList(oldContent.split("\\r?\\n"));
        List<String> revised = Arrays.asList(newContent.split("\\r?\\n"));
        return DiffUtils.diff(original, revised);
    }

    public static void compareFiles(String oldContent, String newContent, JTextPane textPane) {
        // 清空文本面板的内容
        textPane.setText("");

        Patch<String> patch = diffFiles(oldContent, newContent);
        System.out.println("Patch content: " + patch.toString());

        StringBuilder mergedContent = new StringBuilder();
        List<String> originalLines = Arrays.asList(oldContent.split("\\r?\\n"));
        List<String> revisedLines = Arrays.asList(newContent.split("\\r?\\n"));

        for (AbstractDelta<String> delta : patch.getDeltas()) {
            DeltaType type = delta.getType();
            switch (type) {
                case INSERT:
                    // 处理新增行
                    List<String> insertedLines = delta.getTarget().getLines();
                    int insertPosition = delta.getTarget().getPosition();
                    for (int i = 0; i < insertedLines.size(); i++) {
                        String line = insertedLines.get(i);
                        int lineNumber = insertPosition + i + 1; // 计算每行的行号
                        mergedContent.append(line).append("\n");
                        appendWithBackground(textPane, "Line " + lineNumber + ": " + line + "\n", Color.BLUE);
                    }
                    break;
                case DELETE:
                    // 处理删除行
                    List<String> deletedLines = delta.getSource().getLines();
                    int deletePosition = delta.getSource().getPosition();
                    for (int i = 0; i < deletedLines.size(); i++) {
                        String line = deletedLines.get(i);
                        int lineNumber = deletePosition + i + 1; // 计算每行的行号
                        mergedContent.append(line).append("\n");
                        appendWithBackground(textPane, "Line " + lineNumber + ": " + line + "\n", Color.RED);
                    }
                    break;
                case CHANGE:
                    // 处理修改前的行
                    List<String> changedSourceLines = delta.getSource().getLines();
                    int changeSourcePosition = delta.getSource().getPosition();
                    for (int i = 0; i < changedSourceLines.size(); i++) {
                        String line = changedSourceLines.get(i);
                        int lineNumber = changeSourcePosition + i + 1; // 计算每行的行号
                        mergedContent.append(line).append("\n");
                        appendWithBackground(textPane, "Line " + lineNumber + " (Before): " + line + "\n", Color.YELLOW);
                    }
                    // 处理修改后的行
                    List<String> changedTargetLines = delta.getTarget().getLines();
                    int changeTargetPosition = delta.getTarget().getPosition();
                    for (int i = 0; i < changedTargetLines.size(); i++) {
                        String line = changedTargetLines.get(i);
                        int lineNumber = changeTargetPosition + i + 1; // 计算每行的行号
                        mergedContent.append(line).append("\n");
                        appendWithBackground(textPane, "Line " + lineNumber + " (After): " + line + "\n", Color.GREEN);
                    }
                    break;
                default:
                    break;
            }
        }
    }


    // 用于插入带背景色的文本的辅助方法
    private static void appendWithBackground(JTextPane textPane, String msg, Color bgColor) {
        StyledDocument doc = textPane.getStyledDocument();
        Style style = textPane.addStyle("HighlightStyle", null);
        StyleConstants.setBackground(style, bgColor);
        StyleConstants.setForeground(style, textPane.getForeground()); // 保持前景色不变

        try {
            doc.insertString(doc.getLength(), msg, style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }


}
