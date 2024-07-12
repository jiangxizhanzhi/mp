 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneCreateInstanceRequest.java.
 // File size 1911.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.controller.request;

import cn.cyberpeace.mp.cscene.bo.CsceneBO;
import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceCreateContextBO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName CsceneCreateInstanceRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CsceneActionRequest
 */
@Data
public class CsceneCreateInstanceRequest extends CsceneActionRequest {

    @ApiModelProperty(value = "场景存活时间")
    @JsonIgnore
    private Integer sceneTtl;

    @ApiModelProperty(value = "场景属性")
    private Integer sceneAttr;

    @ApiModelProperty(value = "场景引用元数据")
    private String referenceMetaData;

    @ApiModelProperty(value = "场景模板引用类型")
    private Integer referenceType;

    @ApiModelProperty(value = "场景名称")
    private String name;

    @ApiModelProperty(value = "场景模板数据")
    private CsceneBO cscene;

    @ApiModelProperty(value = "场景上下文数据")
    private CsceneContextBO context;

    public CsceneInstanceCreateContextBO toCsceneInstanceCreateContextBO() {
        CsceneInstanceCreateContextBO csceneInstanceCreateContextBO = new CsceneInstanceCreateContextBO();
        csceneInstanceCreateContextBO.setSceneTtl(sceneTtl);
        csceneInstanceCreateContextBO.setSceneAttr(sceneAttr);
        csceneInstanceCreateContextBO.setReferenceMetaData(referenceMetaData);
        csceneInstanceCreateContextBO.setReferenceType(referenceType);
        csceneInstanceCreateContextBO.setCscene(cscene);
        csceneInstanceCreateContextBO.setContext(context);
        csceneInstanceCreateContextBO.setName(name);
        return csceneInstanceCreateContextBO;
    }

}
