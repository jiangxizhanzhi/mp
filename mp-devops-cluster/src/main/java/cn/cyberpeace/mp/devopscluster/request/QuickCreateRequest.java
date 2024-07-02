package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName QuickCreateRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class QuickCreateRequest {
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "靶标uuid")
    private String deviceUuid;
    @ApiModelProperty(value = "软件id集合")
    private List<String> softwareIds;
}
