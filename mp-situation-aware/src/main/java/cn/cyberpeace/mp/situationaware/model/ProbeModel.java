package cn.cyberpeace.mp.situationaware.model;

import java.io.Serializable;
import java.util.Date;

public class ProbeModel implements Serializable {
    private Integer pk;

    private String id;

    private String probeName;

    private String des;

    private Integer osType;

    private Integer probeCategory;

    private Integer scriptType;

    private Date createTime;

    private Date updateTime;

    private Integer delFlag;

    private byte[] scriptContent;

    private String commandInterval;

    private static final long serialVersionUID = 1L;

    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer pk) {
        this.pk = pk;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getProbeName() {
        return probeName;
    }

    public void setProbeName(String probeName) {
        this.probeName = probeName == null ? null : probeName.trim();
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des == null ? null : des.trim();
    }

    public Integer getOsType() {
        return osType;
    }

    public void setOsType(Integer osType) {
        this.osType = osType;
    }

    public Integer getProbeCategory() {
        return probeCategory;
    }

    public void setProbeCategory(Integer probeCategory) {
        this.probeCategory = probeCategory;
    }

    public Integer getScriptType() {
        return scriptType;
    }

    public void setScriptType(Integer scriptType) {
        this.scriptType = scriptType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public byte[] getScriptContent() {
        return scriptContent;
    }

    public void setScriptContent(byte[] scriptContent) {
        this.scriptContent = scriptContent;
    }

    public String getCommandInterval() {
        return commandInterval;
    }

    public void setCommandInterval(String commandInterval) {
        this.commandInterval = commandInterval;
    }
}