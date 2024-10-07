package tongji.demo;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import treepackage.GitTree;

import javax.swing.*;

public class NewCommitAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // 执行新提交操作

        if(GitTree.newCommit()){
            JOptionPane.showMessageDialog(null, "新提交已成功创建！", "提交成功", JOptionPane.INFORMATION_MESSAGE);
        }
        else{
            JOptionPane.showMessageDialog(null, "没有检测到任何变化，未创建新的提交。", "提示", JOptionPane.INFORMATION_MESSAGE);
        }

        // 提示用户提交已完成
    }
}
