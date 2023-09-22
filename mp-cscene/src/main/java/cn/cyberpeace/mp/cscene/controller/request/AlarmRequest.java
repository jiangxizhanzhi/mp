package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class AlarmRequest {
    @ApiModelProperty(value = "场景id")
    private String id;

    @ApiModelProperty(value = "节点id")
    private List<String> nodeIds;

    @ApiModelProperty(value = "告警类别 0:关键文件系统 1:系统进程 2:系统配置 3:系统日志 4:关键日志 ")
    private Integer type;

    @ApiModelProperty(value = "告警关键字")
    private String key;

    @ApiModelProperty(value = "当前页")
    private Integer page;

    @ApiModelProperty(value = "每页数量")
    private Integer pageSize;

    public Integer getPage() {
        return page == null ? 1 : page;
    }

    public Integer getPageSize() {
        return pageSize == null ? 10 : pageSize;
    }

}
