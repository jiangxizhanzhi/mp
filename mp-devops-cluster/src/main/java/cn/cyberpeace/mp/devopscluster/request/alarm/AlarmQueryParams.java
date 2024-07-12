 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AlarmQueryParams.java.
 // File size 1194.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.request.alarm;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @ClassName AlarmQueryParams
 * @Description
 * @Author zhouxinghuai
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see QueryParams
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AlarmQueryParams extends QueryParams {

    @ApiModelProperty("状态：0 生效，1失效， 默认为0")
    private Integer[] alarmStatus;

    @ApiModelProperty("告警类别")
    private Integer[] alarmTypes;

    @ApiModelProperty("主机名称")
    private String hostName;


    public void ensure() {
        if (getPerPage() != null) {
            setPageSize(getPerPage());
        }

        // 默认值
        if (Objects.isNull(getPage()) || getPage() <= 1) {
            setPage(1);
        }

        if (Objects.isNull(getPageSize())) {
            setPageSize(10);
        }
    }
}
