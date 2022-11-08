package cn.cyberpeace.mp.situationaware.model;

import java.io.Serializable;
import java.util.Date;

public class TrafficGroupModel implements Serializable {
    private Integer pk;

    private String trafficGroupId;

    private Date createTime;

    private Integer delFlag;

    private static final long serialVersionUID = 1L;

    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer pk) {
        this.pk = pk;
    }

    public String getTrafficGroupId() {
        return trafficGroupId;
    }

    public void setTrafficGroupId(String trafficGroupId) {
        this.trafficGroupId = trafficGroupId == null ? null : trafficGroupId.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }
}