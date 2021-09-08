package cn.cyberpeace.mp.flow.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 流量脚本查询返回信息
 * @Author qiaokang
 * @Date：2021/9/1 15:15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "流量脚本查询返回信息")
public class QueryFlowScriptDemoResponse {

    /**
     * 脚本模板集合
     */
    @ApiModelProperty(value = "脚本模板集合")
    private List<ScriptDemo> demoList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ApiModel(value = "流量脚本信息")
    public static final class ScriptDemo {
        /**
         * 文件路径
         */
        @ApiModelProperty(value = "文件路径")
        private String file;

        /**
         * 文件url
         */
        @ApiModelProperty(value = "文件url")
        private String fileUrl;

        /**
         * 文件名
         */
        @ApiModelProperty(value = "文件名")
        private String name;
    }

}
