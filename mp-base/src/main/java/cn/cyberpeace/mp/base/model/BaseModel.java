package cn.cyberpeace.mp.base.model;

import cn.cyberpeace.mp.base.util.ModelUtils;
import cn.cyberpeace.mp.base.util.TextUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

@Slf4j
@Data
public abstract class BaseModel {
    private String id;

    private Date createTime;

    private Date updateTime;

    public void initDefault() {
        Method[] initDefaultMethods = ModelUtils.getInitDefaultMethods(getClass());
        try {
            for (Method initDefaultMethod: initDefaultMethods) {
                initDefaultMethod.invoke(this);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Invoke initDefaultMethod on instance {} failed: {}", this, e.getMessage());
        }
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
