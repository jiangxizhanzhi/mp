package cn.cyberpeace.mp.base.model;

import cn.cyberpeace.mp.base.util.TextUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public abstract class BaseModel {
    private String id;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public void initDefault() {
        initDefaultId();
        initDefaultCreateTime();
        initDefaultUpdateTime();
    }

    public void initDefaultId() {
        if (StringUtils.isEmpty(id)) {
            id = TextUtils.uuid();
        }
    }

    public void initDefaultCreateTime() {
        if (createTime == null) {
            createTime = new Date();
        }
    }

    public void initDefaultUpdateTime() {
        if (updateTime == null) {
            updateTime = new Date();
        }
    }
}
