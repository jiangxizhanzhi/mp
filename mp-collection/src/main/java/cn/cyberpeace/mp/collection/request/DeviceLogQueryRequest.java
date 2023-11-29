package cn.cyberpeace.mp.collection.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;

import javax.validation.constraints.NotBlank;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.existsQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

@Data
@Builder
public class DeviceLogQueryRequest {
    @ApiModelProperty(value = "开始时间")
    private String timeStart;

    @ApiModelProperty(value = "结束时间")
    private String timeEnd;

    @ApiModelProperty(value = "设备ip")
    private String ip;

    @ApiModelProperty(value = "设备类型")
    @NotBlank
    private String type;

    @ApiModelProperty(value = "日志类型")
    @NotBlank
    private String logType;

    @Builder.Default
    @ApiModelProperty(value = "每页数量 默认100")
    private Integer pageSize = 100;

    @Builder.Default
    @ApiModelProperty(value = "当前页 默认1")
    private Integer pageIndex = 1;

    public Query convertToQuery() {
        BoolQueryBuilder boolQuery = boolQuery();
        // 设备扰动日志主要就是查询message字段, 这里保证必须要有message字段
        boolQuery.must(existsQuery("message"));
        if (StringUtils.isNotEmpty(this.getIp())) {
            boolQuery.must(matchQuery("device_ip", this.getIp().trim()));
        }
        if (StringUtils.isNotEmpty(this.getType())) {
            boolQuery.must(matchQuery("device_type", this.getType().trim()));
        }
        if (StringUtils.isNotEmpty(this.getLogType())) {
            boolQuery.must(matchQuery("log_type", this.getLogType().trim()));
        }
        if (StringUtils.isNotEmpty(this.getTimeStart()) && StringUtils.isNotEmpty(this.getTimeEnd())) {
            boolQuery.must(rangeQuery("log_time").gte(this.getTimeStart()).lte(this.getTimeEnd()));
        }

        return new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withSort(SortBuilders.fieldSort("@timestamp").order(SortOrder.DESC))
                .withPageable(PageRequest.of(this.getPageIndex() - 1, this.getPageSize()))
                .build();
    }
}
