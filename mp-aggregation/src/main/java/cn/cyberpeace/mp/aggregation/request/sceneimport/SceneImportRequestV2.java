package cn.cyberpeace.mp.aggregation.request.sceneimport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Description
 * @Author maojun
 * @Date 2021/10/11
 */
@Getter
@Setter
public class SceneImportRequestV2 {

    @ApiModelProperty("配置文件内容")
    private String content;

}
