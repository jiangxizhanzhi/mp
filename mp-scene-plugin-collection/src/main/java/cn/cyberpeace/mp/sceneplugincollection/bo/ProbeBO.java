package cn.cyberpeace.mp.sceneplugincollection.bo;

import cn.cyberpeace.mp.sceneplugincollection.bo.base.Probe;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeDto;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

import java.util.List;

/**
 * @Description 云场景探针管理类
 * @Author ling_yu
 * @Date 2024/3/29 9:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProbeBO {

    @ApiModelProperty("探针ID")
    private String id;

    @ApiModelProperty("探针名称")
    private String name;

    @ApiModelProperty("探针描述")
    private String desc;

    @ApiModelProperty("探针分类 0:信息类 1:事件类 2:日志类")
    private Integer type;

    @ApiModelProperty("系统类型 0:windows 1:Unix 2:Linux 3:BSD 4:Other")
    private Integer osType;

    @ApiModelProperty("操作系统集合 0:windows, 1:Unix, 2:Linux, 3:BSD, 4:Other")
    private List<Integer> osTypes;

    @ApiModelProperty("探针脚本 0:shell 1:python 2:bat")
    private String scriptType;

    @ApiModelProperty("探针状态 0:已运行 1:未安装")
    private Integer state;

    @ApiModelProperty("操作类型 0:安装 1:卸载 2:重启 3:删除")
    private Integer operation;

}
