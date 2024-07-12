 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SuricataQueryRequest.java.
 // File size 1355.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/*
 * @BelongsPackage: cn.cyberpeace.mp.situationaware.request
 * @Author: hhuang
 * @CreateTime: 2023/04/23  14:41
 * @Description: Suricata规则查询参数
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuricataQueryRequest {

    @ApiModelProperty("协议")
    private String protocol;

    @ApiModelProperty("规则名称")
    private String ruleName;

    @ApiModelProperty("Att&ck技术")
    private String attck;

    @ApiModelProperty("分类类型")
    private String classType;

    @ApiModelProperty(value = "当前页")
    private Integer page;

    @ApiModelProperty(value = "每页数量")
    private Integer pageSize;

    @ApiModelProperty(value = "每页数量(兼容前端)")
    private Integer perPage;

    public void ensure() {
        // 默认值
        if (Objects.isNull(page)) {
            page = 1;
        }
        if (Objects.isNull(pageSize)) {
            if (Objects.nonNull(perPage)) {
                pageSize = perPage;
            } else {
                pageSize = 10;
            }
        }
    }

}
