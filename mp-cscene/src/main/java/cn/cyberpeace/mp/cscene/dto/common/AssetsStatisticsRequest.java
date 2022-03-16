package cn.cyberpeace.mp.cscene.dto.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description: 资产统计请求
 * @Author qiaokang
 * @Date：2022/03/14 14:22
 */
@Data
public class AssetsStatisticsRequest {

    @ApiModelProperty("场景id集合")
    @NotNull
    private List<String> sceneIds;

}
