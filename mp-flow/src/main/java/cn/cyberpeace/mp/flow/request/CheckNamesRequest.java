package cn.cyberpeace.mp.flow.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @ClassName CheckNamesRequest
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@ApiModel(value = "名称存在校验请求")
public class CheckNamesRequest {

    /**
     * 流量文件名称
     */
    @ApiModelProperty("流量文件名称")
    @NotEmpty(message = "fileNames can not be empty")
    private List<String> fileNames;


}
