package cn.cyberpeace.mp.devops.cluster.response;

import cn.cyberpeace.mp.devops.cluster.vo.DeviceTargetVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceTargetByTypeVO implements Serializable {

    @ApiModelProperty("一级分类ID")
    private Integer firstTypeId;

    @ApiModelProperty("一级分类名称")
    private String firstTypeName;

    @ApiModelProperty("二级分类ID")
    private Integer secondTypeId;

    @ApiModelProperty("二级分类名称")
    private String secondTypeName;

    @ApiModelProperty("二级分类对应的靶标集合")
    private List<DeviceTargetVO> deviceList;
}
