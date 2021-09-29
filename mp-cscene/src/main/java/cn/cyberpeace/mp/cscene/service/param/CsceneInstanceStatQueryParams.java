package cn.cyberpeace.mp.cscene.service.param;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询参数定义
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class CsceneInstanceStatQueryParams extends QueryParams {
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

    public static class SceneStatus {
        public static final Integer DELETED = 0;
        public static final Integer CREATING = 1;
        public static final Integer QUEUE = 2;
        public static final Integer RUNNING = 3;
        public static final Integer PAUSE = 4;
        public static final Integer ERROR = 5;
    }
}
