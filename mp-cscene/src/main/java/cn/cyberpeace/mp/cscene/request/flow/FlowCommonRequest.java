 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowCommonRequest.java.
 // File size 1151.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.request.flow;

import cn.cyberpeace.mp.cscene.request.common.BaseSceneElementRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName FlowCommonRequest
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/11/22 17:58
 * @Version 1.0.0
 * @see BaseSceneElementRequest
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlowCommonRequest extends BaseSceneElementRequest {

    @ApiModelProperty("开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @ApiModelProperty("结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 源IP集合
     */
    @ApiModelProperty("源IP集合")
    private List<String> sourceIps;


    private Integer topSize;

}
