 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name EidBO.java.
 // File size 932.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.core.bo.base;

import cn.cyberpeace.mp.base.bo.ModelBO;
import cn.cyberpeace.mp.base.model.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName EidBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see ModelBO
 */
@Data
public abstract class EidBO<M extends BaseModel, T extends EidBO<M, T>> extends ModelBO<M, T> {
    @ApiModelProperty(value = "服务端id")
    private String eid;

    @Override
    public void fromModel(M obj, String... ignoreFields) {
        super.fromModel(obj, ignoreFields);
        eid = obj.getId();
    }

    @Override
    public M toModel(String... ignoreFields) {
        M obj = super.toModel(ignoreFields);
        obj.setId(eid);
        return obj;
    }

    @Override
    public T export() {
        setEid(null);
        return (T) this;
    }

}
