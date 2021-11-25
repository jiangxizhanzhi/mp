package cn.cyberpeace.mp.cscene.controller.request;

import cn.cyberpeace.mp.cscene.bo.CsceneBO;
import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceCreateContextBO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CsceneCreateInstanceRequest extends CsceneActionRequest {

    @ApiModelProperty(value = "场景存活时间")
    @JsonIgnore
    private Integer sceneTtl;

    @ApiModelProperty(value = "场景属性")
    private Integer sceneAttr;

    @ApiModelProperty(value = "场景引用元数据")
    private String referenceMetaData;

    @ApiModelProperty(value = "场景模板数据")
    private CsceneBO cscene;

    @ApiModelProperty(value = "场景上下文数据")
    private CsceneContextBO context;

    public CsceneInstanceCreateContextBO toCsceneInstanceCreateContextBO() {
        var csceneInstanceCreateContextBO = new CsceneInstanceCreateContextBO();
        csceneInstanceCreateContextBO.setSceneTtl(sceneTtl);
        csceneInstanceCreateContextBO.setSceneAttr(sceneAttr);
        csceneInstanceCreateContextBO.setReferenceMetaData(referenceMetaData);
        csceneInstanceCreateContextBO.setCscene(cscene);
        csceneInstanceCreateContextBO.setContext(context);
        return csceneInstanceCreateContextBO;
    }

}
