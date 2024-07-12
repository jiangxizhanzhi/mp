 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneInstanceStatQueryParams.java.
 // File size 1887.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.service.param;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName CsceneInstanceStatQueryParams
 * @Description 查询参数定义
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see QueryParams
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class CsceneInstanceStatQueryParams extends QueryParams {
    @ApiModelProperty(value = "场景实例类型 1测试场景 2应用场景")
    private Integer type;
    @ApiModelProperty(value = "引用类型")
    private String[] referenceTypes;
    /**
     * 正在创建 1
     * 队列中 2
     * 运行中 3
     * 暂停 4
     * 已删除 0
     * 错误 5
     */
    @ApiModelProperty(value = "场景实例状态")
    private Integer sceneStatus;
    @ApiModelProperty(value = "是否只显示活动状态")
    private Boolean active;
    @ApiModelProperty(value = "是否只显示已结束")
    private Boolean over;
    @ApiModelProperty(value = "是否只显示异常")
    private Boolean error;
    @ApiModelProperty(value = "是否只显示自己的场景")
    private Boolean self;
    @ApiModelProperty(value = "是否已删除未清理")
    private Boolean valid;

    public static class SceneStatus {
        public static final Integer DELETED = 0;
        public static final Integer CREATING = 1;
        public static final Integer QUEUE = 2;
        public static final Integer RUNNING = 3;
        public static final Integer PAUSE = 4;
        public static final Integer ERROR = 5;
        public static final Integer SUSPEND = 6;
        public static final Integer DELETING = 7;
    }
}
