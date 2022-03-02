package cn.cyberpeace.mp.devops.cluster.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlavorVO implements Serializable {

    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty("UUID")
    private String uuid;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("cpu")
    private Integer cpu;

    @ApiModelProperty("内存")
    private Integer ram;

    @ApiModelProperty("磁盘")
    private Integer disk;

    @ApiModelProperty("创建时间")
    private String createdAt;

    @ApiModelProperty("更新时间")
    private String updatedAt;

    @ApiModelProperty("用户")
    private Object user;

    @ApiModelProperty("项目")
    private Object project;
}
