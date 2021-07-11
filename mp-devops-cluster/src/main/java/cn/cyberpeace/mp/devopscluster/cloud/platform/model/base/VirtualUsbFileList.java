package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devopscluster.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("虚拟USB存储文件列表")
public class VirtualUsbFileList extends BaseModel {

    
    @ApiModelProperty("总数")
    private Integer total;

    
    @ApiModelProperty("文件列表")
    private List<VirtualUsbFile> items;

}
