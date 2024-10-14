package treepackage;

import tongji.demo.InitTrace;

import java.util.Timer;
import java.util.TimerTask;


public class AutoCommitTimer {

    private Timer timer;

    public AutoCommitTimer(InitTrace initTrace) {
        // 初始化定时器
        timer = new Timer();
        // 设置定时任务，每隔10秒提交一次新版本
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 提交新版本
                GitTree.newCommit();
                System.out.println("新版本已提交");
                
                initTrace.refreshCards();
            }
        }, 0, 10000); // 0表示立即开始执行，10000表示间隔10秒
    }

    public void stop() {
        timer.cancel(); // 停止定时任务
    }
}
