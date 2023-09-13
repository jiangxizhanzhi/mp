package cn.cyberpeace.mp.sceneplugincollection.bo;

import cn.cyberpeace.mp.situationaware.model.MitreEventModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 采集到的事件基本信息
 * @author zhanzhi
 */
@Data
@Accessors(chain = true)
@ApiModel("基础事件信息对象")
public class BaseEventBo {

    /**
     * 发生时间
     */
    @ApiModelProperty("事件发生时间")
    private LocalDateTime time;

    /**
     * es上面这个记录的id，对应_id
     */
    @ApiModelProperty("事件发生时的documentId")
    private String documentId;

    /**
     * 发生的agentName，其实就是eid
     */
    @ApiModelProperty("事件发生的节点eid")
    private String eid;

    public static BaseEventBo getFromMitreEventModel(MitreEventModel mitreEventModel) {
        return new BaseEventBo().setTime(mitreEventModel.getTime())
                .setEid(mitreEventModel.getAgentName())
                .setDocumentId(mitreEventModel.getDocumentId());
    }

}
