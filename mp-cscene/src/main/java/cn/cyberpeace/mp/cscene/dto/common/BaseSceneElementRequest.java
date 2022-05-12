package cn.cyberpeace.mp.cscene.dto.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/29 19:52
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseSceneElementRequest {

    @ApiModelProperty("场景id,多个id用逗号分隔")
    @NotNull
    private String sceneId;

    @ApiModelProperty("节点id")
    private List<String> elementIds;


    /**
     * 源IP集合
     */
    @ApiModelProperty("源IP集合")
    private List<String> sourceIps;

    /**
     * Top数量
     */
    @ApiModelProperty("Top数量")
    private Integer topSize;
}
