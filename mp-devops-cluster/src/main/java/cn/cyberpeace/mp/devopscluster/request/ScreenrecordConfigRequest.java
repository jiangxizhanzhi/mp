package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Author qiaokang
 * @Date：2021/9/14 10:50
 */
@Data
@ApiModel("录屏空间自动清除配置")
public class ScreenrecordConfigRequest {
    @ApiModelProperty("存储总空间上限(单位G)")
    private Integer fileLimitSpace;

    @ApiModelProperty("单个文件存储时间上限(单位月)")
    private Integer fileLimitTime;
}
