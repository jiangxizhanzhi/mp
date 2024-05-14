package cn.cyberpeace.mp.cscene.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SceneAreaTreeBO {
    @ApiModelProperty(value = "区域列表")
    private List<Area> areaList = new ArrayList<>();

    @Data
    public static class Area {
        @ApiModelProperty(value = "节点id")
        private String id;
        @ApiModelProperty(value = "父区域节点id")
        private String label;
        @ApiModelProperty(value = "名称")
        private String name;
        @ApiModelProperty(value = "描述")
        private String description;
        @ApiModelProperty(value = "显示参数")
        private String displayContext;
        @ApiModelProperty(value = "子区域列表")
        private List<Area> children = new ArrayList<>();
    }
}
