package cn.cyberpeace.mp.situationaware.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class DataCollectEventRequest extends BaseEventRequest{
    @ApiModelProperty(value = "wazuh events规则id集合")
    private List<String> ruleIds;

    @ApiModelProperty(value = "是否是滚动分页请求")
    private boolean scroll = false;

    @ApiModelProperty(value = "查询总数是否需要超过1w，默认false")
    private boolean trackTotalHits = false;

    @ApiModelProperty(value = "关键字集合")
    private List<String> keyword;

    @ApiModelProperty(value = "请求id,滚动分页请求时需要")
    private String requestId;
}
