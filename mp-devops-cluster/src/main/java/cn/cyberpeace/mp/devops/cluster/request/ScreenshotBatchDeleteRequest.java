package cn.cyberpeace.mp.devops.cluster.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description: 批量删除截屏请求
 * @Author qiaokang
 * @Date：2021/9/14 10:50
 */
@Data
@ApiModel("批量删除截屏请求")
public class ScreenshotBatchDeleteRequest {

    /**
     * 截屏文件id集合
     */
    @ApiModelProperty("截屏文件id集合")
    @NotNull(message = "ids can not be empty")
    private List<String> ids;

}
