package cn.cyberpeace.mp.collectionapi.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class PageQueryRequest {
    @ApiModelProperty(value = "战队id")
    private String teamId;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "主机ip")
    private List<String> agentIps;

    @ApiModelProperty(value = "agent名称")
    private List<String> agentNames = new ArrayList<>();

    @ApiModelProperty(value = "当前页")
    private Integer page;

    @ApiModelProperty(value = "每页数量")
    private Integer pageSize;

    @ApiModelProperty(value = "分页每页条数(兼容)")
    private Integer perPage;

    public void ensure() {
        if (perPage != null) {
            pageSize = perPage;
        }

        // 默认值
        if (Objects.isNull(page)) {
            page = 1;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = 10;
        }
    }
}
