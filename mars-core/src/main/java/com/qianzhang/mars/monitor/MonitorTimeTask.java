package com.qianzhang.mars.monitor;

import java.util.TimerTask;

/**
 * 监控器线程
 * @author qianzhang
 */
public class MonitorTimeTask extends TimerTask {

    private MonitorBus monitorBus;

    public MonitorTimeTask(MonitorBus monitorBus) {
        this.monitorBus = monitorBus;
    }

    @Override
    public void run() {
        monitorBus.printStatistics();
    }
}
