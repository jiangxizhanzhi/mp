package cn.cyberpeace.mp.aggregation.sceneimport.parse.export;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@ApiModel(value = "场景导出成yml的返回结果")
public class ExportResult {

    @ApiModelProperty(required = true, value = "是否成功")
    private boolean success = true;

    @ApiModelProperty(required = true, value = "导出时进行过转换的信息列表")
    private List<ConvertInfo> convertInfos;

    @Getter
    @Setter
    @ApiModel(value = "导出时进行过转换的信息")
    public static class ConvertInfo {
        @ApiModelProperty(required = true, value = "场景中节点原始的id")
        private String originId;

        @ApiModelProperty(required = true, value = "导出后节点在yml中的id")
        private String id;
    }
}
