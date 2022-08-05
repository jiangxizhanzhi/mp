package cn.cyberpeace.mp.sceneplugincloud.init.jobs;

/**
 * @Description
 * @Author maojun
 * @Date 2022/8/5
 */
public abstract class AbstractInitJob {

    private volatile boolean executed = false;

    public AbstractInitJob() {
    }

    public void executed() {
        synchronized (this) {
            // 成功执行过且不支持刷新动作 则无需执行
            if (executed && !this.getSupportRefresh()) {
                return;
            }
            executed = this.doExecute();
        }
    }

    protected abstract boolean doExecute();

    /**
     * 由具体业务决定是否支持刷新的动作
     */
    protected abstract boolean getSupportRefresh();

}
