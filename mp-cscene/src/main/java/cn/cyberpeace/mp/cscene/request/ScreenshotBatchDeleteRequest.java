package cn.cyberpeace.mp.cscene.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@ApiModel("批量删除截屏请求")
public class ScreenshotBatchDeleteRequest {

    
    @ApiModelProperty("截屏文件id集合")
    @NotNull(message = "ids can not be empty")
    private List<String> ids;

}
