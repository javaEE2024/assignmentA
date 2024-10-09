package treepackage;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class ColoredTreeCellRenderer extends DefaultTreeCellRenderer {
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        if (value instanceof ColoredTreeNode) {
            ColoredTreeNode node = (ColoredTreeNode) value;
            c.setForeground(node.getColor());
        } else {
            c.setForeground(Color.BLACK);
        }
        return c;
    }
}

