package treepackage;

import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.Color;

public class ColoredTreeNode extends DefaultMutableTreeNode {
    private Color color;

    public ColoredTreeNode(Object userObject, Color color) {
        super(userObject);
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}

