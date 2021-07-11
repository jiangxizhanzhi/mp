package cn.cyberpeace.mp.cscene.service.param;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class CsceneInstanceStatQueryParams extends QueryParams {
    @ApiModelProperty(value = "场景实例类型 1测试场景 2应用场景")
    private Integer type;
    @ApiModelProperty(value = "引用类型")
    private String[] referenceTypes;
    
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
