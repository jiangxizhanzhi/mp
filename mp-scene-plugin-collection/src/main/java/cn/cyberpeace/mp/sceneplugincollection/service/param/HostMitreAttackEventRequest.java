package cn.cyberpeace.mp.sceneplugincollection.service.param;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.situationaware.request.WazuhMitreAttackEventRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static cn.cyberpeace.mp.situationaware.service.DataCollectionService.indexListBuilder;

/**
 * 主机 mitre attack事件查询请求
 * @author zhanzhi
 */
@Data
@Accessors(chain = true)
@ApiModel("主机 mitre攻击时间查询request")
public class HostMitreAttackEventRequest extends QueryParams {

    @ApiModelProperty(value = "开始时间")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "结束时间")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "节点id列表")
    private Set<String> nodeIds;

    /**
     * 是否是滚动分页请求
     */
    @ApiModelProperty(value = "是否是searchAfter查询")
    private Boolean searchAfterFlag;

    @ApiModelProperty(value = "如果是searchAfter查询,那么需要传入上一次返回条目中最后一条的timestamp")
    private String lastTimestamp;


    public List<String> generateIndexNameFromTimeRange(SceneHandler sceneHandler) {
        if (Objects.isNull(startTime)) {
            startTime = sceneHandler.getScene().getCreateTime();
        }
        if (Objects.isNull(endTime)) {
            endTime = LocalDateTime.now();
        }
        return indexListBuilder(startTime, endTime);
    }

    public static WazuhMitreAttackEventRequest getWazuRequestByHostRequest(HostMitreAttackEventRequest hostMitreAttackEventRequest, List<String> eidList, SceneHandler sceneHandler) {
        WazuhMitreAttackEventRequest wazuhMitreAttackEventRequest = new WazuhMitreAttackEventRequest();
        //es分页查询使用from，size
        if (Boolean.TRUE.equals(hostMitreAttackEventRequest.getSearchAfterFlag())) {
            Integer size = hostMitreAttackEventRequest.getPageSize();
            if (Objects.isNull(size)) {
                size = 0;
            }
            size = size < 0 ? 0 : size;
            wazuhMitreAttackEventRequest.setSearchAfterFlag(true)
                    .setUtcZonedDateTimeString(hostMitreAttackEventRequest.getLastTimestamp())
                    .setSize(size);
        } else {
            wazuhMitreAttackEventRequest.setSearchAfterFlag(false);
            wazuhMitreAttackEventRequest.setSize(hostMitreAttackEventRequest.getPageSize());
            int from = (hostMitreAttackEventRequest.getPage() - 1) * hostMitreAttackEventRequest.getPageSize();
            from = Math.max(from, 0);
            wazuhMitreAttackEventRequest.setFrom(from);
        }
        wazuhMitreAttackEventRequest.setAgentIdList(eidList);
        wazuhMitreAttackEventRequest.setIndexList(hostMitreAttackEventRequest.generateIndexNameFromTimeRange(sceneHandler));
        return wazuhMitreAttackEventRequest;
    }




}
