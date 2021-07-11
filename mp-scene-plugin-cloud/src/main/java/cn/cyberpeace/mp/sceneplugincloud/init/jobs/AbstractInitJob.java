package cn.cyberpeace.mp.sceneplugincloud.init.jobs;


public abstract class AbstractInitJob {

    private volatile boolean executed = false;

    public AbstractInitJob() {
    }

    public void executed() {
        synchronized (this) {
            
            if (executed && !this.getSupportRefresh()) {
                return;
            }
            executed = this.doExecute();
        }
    }

    protected abstract boolean doExecute();

    
    protected abstract boolean getSupportRefresh();

}
