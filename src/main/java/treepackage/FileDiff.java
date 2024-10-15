package treepackage;

import java.util.Arrays;
import java.util.List;
import com.github.difflib.DiffUtils;
import com.github.difflib.patch.Patch;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.DeltaType;
import com.intellij.ui.JBColor;

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
        // Clear the text pane
        textPane.setText("");

        // Compute the diff
        Patch<String> patch = diffFiles(oldContent, newContent);
        System.out.println("Patch content: " + patch.toString());

        // Process each difference
        for (AbstractDelta<String> delta : patch.getDeltas()) {
            DeltaType type = delta.getType();
            switch (type) {
                case INSERT:
                    // Handle inserted lines (blue)
                    processInsert(delta, textPane);
                    break;
                case DELETE:
                    // Handle deleted lines (red)
                    processDelete(delta, textPane);
                    break;
                case CHANGE:
                    // Handle changed lines (yellow for before, green for after)
                    processChange(delta, textPane);
                    break;
                default:
                    break;
            }
        }
    }

    // Helper method to process inserted lines
    private static void processInsert(AbstractDelta<String> delta, JTextPane textPane) {
        List<String> insertedLines = delta.getTarget().getLines();
        int insertPosition = delta.getTarget().getPosition();
        for (int i = 0; i < insertedLines.size(); i++) {
            String line = insertedLines.get(i);
            int lineNumber = insertPosition + i + 1;
            appendWithBackground(textPane, "Line " + lineNumber + ": " + line + "\n", JBColor.BLUE);
        }
    }

    // Helper method to process deleted lines
    private static void processDelete(AbstractDelta<String> delta, JTextPane textPane) {
        List<String> deletedLines = delta.getSource().getLines();
        int deletePosition = delta.getSource().getPosition();
        for (int i = 0; i < deletedLines.size(); i++) {
            String line = deletedLines.get(i);
            int lineNumber = deletePosition + i + 1;
            appendWithBackground(textPane, "Line " + lineNumber + ": " + line + "\n", JBColor.RED);
        }
    }

    // Helper method to process changed lines
    private static void processChange(AbstractDelta<String> delta, JTextPane textPane) {
        // Lines before the change (yellow)
        List<String> changedSourceLines = delta.getSource().getLines();
        int changeSourcePosition = delta.getSource().getPosition();
        for (int i = 0; i < changedSourceLines.size(); i++) {
            String line = changedSourceLines.get(i);
            int lineNumber = changeSourcePosition + i + 1;
            appendWithBackground(textPane, "Line " + lineNumber + " (Before): " + line + "\n", JBColor.YELLOW);
        }
        // Lines after the change (green)
        List<String> changedTargetLines = delta.getTarget().getLines();
        int changeTargetPosition = delta.getTarget().getPosition();
        for (int i = 0; i < changedTargetLines.size(); i++) {
            String line = changedTargetLines.get(i);
            int lineNumber = changeTargetPosition + i + 1;
            appendWithBackground(textPane, "Line " + lineNumber + " (After): " + line + "\n", JBColor.GREEN);
        }
    }

    // Helper method to append text with background color
    private static void appendWithBackground(JTextPane textPane, String msg, Color bgColor) {
        StyledDocument doc = textPane.getStyledDocument();
        Style style = textPane.addStyle("HighlightStyle", null);
        StyleConstants.setBackground(style, bgColor);
        StyleConstants.setForeground(style, textPane.getForeground());

        try {
            doc.insertString(doc.getLength(), msg, style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
