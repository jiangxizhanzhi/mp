package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author zhanglin
 * @Date 2023/3/27
 */
@Data
public class UpdateApplicationRequest {
    @ApiModelProperty(value = "申请使用的实体设备的uuid")
    private String uuid;
    @ApiModelProperty(value = "操作类型： 1 新增请求，2 删除请求")
    private Integer operate;
}