package cn.cyberpeace.mp.cscene.request.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;


@Data
public class AssetsStatisticsRequest {

    @ApiModelProperty("场景id集合")
    @NotNull
    private List<String> sceneIds;

}
