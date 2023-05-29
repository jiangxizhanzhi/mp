package cn.cyberpeace.mp.devopscluster.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 资源用量情况返回
 * @Author qiaokang
 * @Date：2021/9/13 19:55
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "录屏空间自动清除配置")
@Builder
public class ScreenRecordRestoreConfigVO {

    @ApiModelProperty("存储总空间上限(单位G)")
    private Integer fileLimitSpace;

    @ApiModelProperty("单个文件存储时间上限(单位天)")
    private Integer fileLimitTime;

}
