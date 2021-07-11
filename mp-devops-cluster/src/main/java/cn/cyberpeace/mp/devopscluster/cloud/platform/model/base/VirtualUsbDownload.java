package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devopscluster.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("虚拟USB下载对象")
public class VirtualUsbDownload extends BaseModel {

    
    @ApiModelProperty("文件下载地址")
    private String nginxFileUrl;
}
