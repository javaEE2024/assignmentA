package cellRenderer;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class DiffTreeCellRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        String nodeLabel = node.getUserObject().toString();

        // 根据节点标签设置不同的颜色
        if (nodeLabel.startsWith("+ ")) {
            // 新增的节点用蓝色表示
            c.setForeground(Color.BLUE);
        } else if (nodeLabel.startsWith("- ")) {
            // 删除的节点用红色表示
            c.setForeground(Color.RED);
        } else if (nodeLabel.startsWith("~ ")) {
            // 修改的节点用黄色表示
            c.setForeground(Color.YELLOW);
        } else {
            // 未修改的节点保持默认颜色
            c.setForeground(Color.BLACK);
        }

        return c;
    }
}
