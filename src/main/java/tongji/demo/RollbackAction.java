package tongji.demo;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import treepackage.GitRollback;
import treepackage.GitTree;

public class RollbackAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // 弹出输入框让用户选择要回滚到的版本索引
        String input = Messages.showInputDialog(
                e.getProject(),
                "请输入要回滚到的版本索引 (0 到 " + (GitTree.history.size() - 1) + "):",
                "回滚到指定版本",
                Messages.getQuestionIcon()
        );

        try {
            int versionIndex = Integer.parseInt(input);
            // 调用回滚功能
            GitRollback.rollbackToVersion(versionIndex);
            Messages.showInfoMessage("成功回滚到版本 " + versionIndex, "回滚成功");
        } catch (NumberFormatException ex) {
            Messages.showErrorDialog("请输入有效的数字。", "回滚失败");
        } catch (Exception ex) {
            Messages.showErrorDialog("回滚过程中发生错误: " + ex.getMessage(), "回滚失败");
        }
    }
}
