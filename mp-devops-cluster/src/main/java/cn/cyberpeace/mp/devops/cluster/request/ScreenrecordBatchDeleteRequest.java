package cn.cyberpeace.mp.devops.cluster.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description: 批量删除录屏文件请求
 * @Author qiaokang
 * @Date：2021/9/14 10:50
 */
@Data
@ApiModel("批量删除录屏文件请求")
public class ScreenrecordBatchDeleteRequest {

    /**
     * 录屏文件名称集合
     */
    @ApiModelProperty("录屏文件名称集合")
    @NotNull(message = "recordNames can not be empty")
    private List<String> recordNames;

}
