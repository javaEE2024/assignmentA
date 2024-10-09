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
                    delta.getTarget().getLines().forEach(line -> {
                        int lineNumber = delta.getTarget().getPosition(); // 新增行的行号
                        mergedContent.append(line).append("\n");
                        appendWithBackground(textPane, "Line " + (lineNumber + 1) + ": " + line + "\n", Color.BLUE); // 前景色不变，背景色蓝色
                    });
                    break;
                case DELETE:
                    // 处理删除行
                    delta.getSource().getLines().forEach(line -> {
                        int lineNumber = delta.getSource().getPosition(); // 删除行的行号
                        mergedContent.append(line).append("\n");
                        appendWithBackground(textPane, "Line " + (lineNumber + 1) + ": " + line + "\n", Color.RED); // 前景色不变，背景色红色
                    });
                    break;
                case CHANGE:
                    // 处理修改前的行
                    delta.getSource().getLines().forEach(line -> {
                        int lineNumber = delta.getSource().getPosition(); // 修改前的行号
                        mergedContent.append(line).append("\n");
                        appendWithBackground(textPane, "Line " + (lineNumber + 1) + " (Before): " + line + "\n", Color.YELLOW); // 前景色不变，背景色黄色
                    });
                    // 处理修改后的行
                    delta.getTarget().getLines().forEach(line -> {
                        int lineNumber = delta.getTarget().getPosition(); // 修改后的行号
                        mergedContent.append(line).append("\n");
                        appendWithBackground(textPane, "Line " + (lineNumber + 1) + " (After): " + line + "\n", Color.GREEN); // 前景色不变，背景色绿色
                    });
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
