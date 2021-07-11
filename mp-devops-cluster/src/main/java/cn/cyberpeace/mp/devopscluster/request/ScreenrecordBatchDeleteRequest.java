package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@ApiModel("批量删除录屏文件请求")
public class ScreenrecordBatchDeleteRequest {

    
    @ApiModelProperty("录屏文件名称集合")
    @NotNull(message = "recordNames can not be empty")
    private List<String> recordNames;

}
