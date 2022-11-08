package cn.cyberpeace.mp.situationaware.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * wazuh数据模型
 **/
public class WazuhAgentProcessModel {
    private String pid;

    private int nice;

    private int session;

    private int stime;

    private int size;

    private String name;

    @JSONField(name = "vm_size")
    private int vmSize;

    private int nlwp;

    private int utime;

    private String cmd;

    private int priority;

    private int ppid;

    @JSONField(name = "agent_id")
    private String agentId;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getNice() {
        return nice;
    }

    public void setNice(int nice) {
        this.nice = nice;
    }

    public int getSession() {
        return session;
    }

    public void setSession(int session) {
        this.session = session;
    }

    public int getStime() {
        return stime;
    }

    public void setStime(int stime) {
        this.stime = stime;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVmSize() {
        return vmSize;
    }

    public void setVmSize(int vmSize) {
        this.vmSize = vmSize;
    }

    public int getNlwp() {
        return nlwp;
    }

    public void setNlwp(int nlwp) {
        this.nlwp = nlwp;
    }

    public int getUtime() {
        return utime;
    }

    public void setUtime(int utime) {
        this.utime = utime;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getPpid() {
        return ppid;
    }

    public void setPpid(int ppid) {
        this.ppid = ppid;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }
}
