package cn.cyberpeace.mp.flow.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;


@Data
@ApiModel(value = "名称存在校验请求")
public class CheckNamesRequest {

    
    @ApiModelProperty("流量文件名称")
    @NotEmpty(message = "fileNames can not be empty")
    private List<String> fileNames;


}
