package cn.cyberpeace.mp.devops.cluster.response;

import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 流表操作返回信息
 * @Author qiaokang
 * @Date：2021/7/9 15:08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("流表操作返回信息")
public class OpenflowActionResponse extends BaseCloudPlatformModel {

    @ApiModelProperty("流表操作列表")
    private List<Action> list;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Action {

        @ApiModelProperty("动作")
        private String actions;

        @ApiModelProperty("优先级")
        private String priority;

        @ApiModelProperty("入端口")
        private String in_port;

        @ApiModelProperty("所属物理节点")
        private String hostname;
    }

}
