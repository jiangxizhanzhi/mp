package cn.cyberpeace.mp.cscene.request.common;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.cscene.bo.ProbeOperateBO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProbeOperateRequest {

    @ApiModelProperty("场景id")
    @NotNull
    private String sceneId;

    @ApiModelProperty("变更事件开关状态, 0:关闭,1:开启")
    @NotNull
    private Integer changeEventState;

    @ApiModelProperty("原始日志开关状态, 0:关闭,1:开启")
    @NotNull
    private Integer originLog;

    @ApiModelProperty("日志事件开关状态, 0:关闭,1:开启")
    @NotNull
    private Integer logEvent;

    @ApiModelProperty("自定义采集开关状态, 0:关闭,1:开启")
    @NotNull
    private Integer customCollection;

    /**
     * @description: ProbeOperateRequest转List<ProbeOperateBO>
     * @throws
     * @return List<ProbeOperateBO>
     */
    public List<ProbeOperateBO> transFormToProbeOperateBO(){
        Map<String, Object> objectMap = queryAllTypeMap();
        return objectMap.entrySet().stream().map(stringObjectEntry -> {
            return ProbeOperateBO.builder().scene(this.getSceneId()).type(stringObjectEntry.getKey())
                .operate((Integer)stringObjectEntry.getValue()).build();
        }).collect(Collectors.toList());
    }

    public Map<String, Object> queryAllTypeMap(){
        Map<String, Object> objectMap = BeanUtils.getMap(this);
        objectMap.remove("sceneId");
        return objectMap;
    }

}
