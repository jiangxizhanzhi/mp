package cn.cyberpeace.mp.flow.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "流量脚本查询返回信息")
public class QueryFlowScriptDemoResponse {

    
    @ApiModelProperty(value = "脚本模板集合")
    private List<ScriptDemo> demoList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ApiModel(value = "流量脚本信息")
    public static final class ScriptDemo {
        
        @ApiModelProperty(value = "文件路径")
        private String file;

        
        @ApiModelProperty(value = "文件url")
        private String fileUrl;

        
        @ApiModelProperty(value = "文件名")
        private String name;
    }

}
