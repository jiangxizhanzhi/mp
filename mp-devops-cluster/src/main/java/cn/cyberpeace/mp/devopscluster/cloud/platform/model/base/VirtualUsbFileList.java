package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devopscluster.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description: 虚拟USB存储文件列表
 * @Author: guquan
 * @Date：2023/4/18 10:24
 */
@Data
@ApiModel("虚拟USB存储文件列表")
public class VirtualUsbFileList extends BaseModel {

    /**
     * 总数
     */
    @ApiModelProperty("总数")
    private Integer total;

    /**
     * 文件列表
     */
    @ApiModelProperty("文件列表")
    private List<VirtualUsbFile> items;

}
