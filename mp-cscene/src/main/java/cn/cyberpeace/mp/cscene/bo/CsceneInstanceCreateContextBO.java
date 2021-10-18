package cn.cyberpeace.mp.cscene.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CsceneInstanceCreateContextBO extends CsceneContextBO {

    @ApiModelProperty(value = "场景存活时间")
    private Integer sceneTtl;

    @ApiModelProperty(value = "场景属性")
    private Integer sceneAttr;

    @ApiModelProperty(value = "场景引用元数据")
    private String referenceMetaData;

    @ApiModelProperty(value = "场景模板数据")
    private CsceneBO cscene;

    public CsceneContextBO toCsceneContext() {
        return new CsceneContextBO(getAccessUserId(), getContext());
    }
}
