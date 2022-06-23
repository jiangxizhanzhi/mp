package cn.cyberpeace.mp.flow.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Description: 流量信息导入请求
 * @Author qiaokang
 * @Date：2021/8/23 10:36
 */
@Data
@ApiModel(value = "流量信息导入请求")
public class ImportFlowInfoRequest {

    /**
     * 流量文件路径
     */
    @ApiModelProperty("流量文件路径")
    @NotNull(message = "path can not be empty")
    private String path;


}
