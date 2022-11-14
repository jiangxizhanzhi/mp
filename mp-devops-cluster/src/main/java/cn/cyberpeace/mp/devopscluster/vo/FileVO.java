package cn.cyberpeace.mp.devopscluster.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @author zhangjiaxin
 * @date 2022年11月14日上午9:55:26
 */
@Data
public class FileVO {

    @ApiModelProperty("权限")
    private String authority;

    @ApiModelProperty("引用数")
    private int refers;

    @ApiModelProperty("所有者")
    private String owner;

    @ApiModelProperty("所有者组")
    private String ownerGroup;

    @ApiModelProperty("大小")
    private long size;

    @ApiModelProperty("修改时间")
    private String modifyTime;

    @ApiModelProperty("名称")
    private String name;

}
