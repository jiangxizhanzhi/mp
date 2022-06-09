package cn.cyberpeace.mp.cscene.bo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CsceneInstanceCreateContextBO {

    @ApiModelProperty(value = "场景存活时间")
    @JsonIgnore
    private Integer sceneTtl;

    @ApiModelProperty(value = "场景属性")
    private Integer sceneAttr;

    @ApiModelProperty(value = "场景引用元数据")
    private String referenceMetaData;

    @ApiModelProperty(value = "场景模板引用类型")
    private Integer referenceType;

    @ApiModelProperty(value = "场景模板数据")
    private CsceneBO cscene;

    @ApiModelProperty(value = "场景上下文数据")
    private CsceneContextBO context;

}
