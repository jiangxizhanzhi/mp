package cn.cyberpeace.mp.cscene.dto.event;

import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.MitreAttackEventBo;
import cn.cyberpeace.mp.sceneplugincollection.bo.MitreRuleBo;
import cn.cyberpeace.mp.situationaware.constant.MetriTacticEnum;
import com.alibaba.excel.util.StringUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.cyberpeace.mp.cscene.constant.CsceneConstant.INTERNATIONAL_DATE_TIME_FORMAT;
import static cn.cyberpeace.mp.cscene.constant.CsceneConstant.M_D_HH_MM_SS_DATE_FORMAT;

/**
 * 返回主机被采集到的符合mitre ATT&CK定义 的攻击事件信息
 * @author zhanzhi
 */
@Data
@Accessors(chain = true)
public class HostMitreAttackEventDto {

    /**
     * 时间，格式为 [M]M-DD HH:MM:SS
     */
    @ApiModelProperty("时间，格式[M]M-DD HH:MM:SS")
    private String formatTime;

    @ApiModelProperty("原始时间, 格式yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private String timestamp;

    /**
     * 场景实例的节点的唯一id
     */
    @ApiModelProperty("场景实例节点的唯一id")
    private String eid;

    /**
     * 主机名称
     */
    @ApiModelProperty("主机名称")
    private String hostName;

    /**
     * 事件类型
     */
    @ApiModelProperty("事件类型")
    private String eventType;

    /**
     * 事件描述
     */
    @ApiModelProperty("事件描述")
    private String eventDescription;

    /**
     * 一次攻击事件使用的技术可能会被归类到多个战术目标，多个，这里用,号隔开
     */
    @ApiModelProperty("攻击技术的战术目标，多个用,隔开")
    private String formatAttackCategory;


    public static HostMitreAttackEventDto getFromBo(MitreAttackEventBo mitreAttackEventBo) {


        HostMitreAttackEventDto hostMitreAttackEventDto = new HostMitreAttackEventDto().setEventType(mitreAttackEventBo.getHostEventEnum().getI18nMessage())
                .setEid(Optional.ofNullable(mitreAttackEventBo.getMitreRuleBo().getEid()).orElse(""))
                .setHostName(Optional.ofNullable(mitreAttackEventBo.getSceneElementBO()).map(SceneElementBO::getName).orElse(""))
                .setEventDescription("-")
                .setFormatAttackCategory(Optional.ofNullable(mitreAttackEventBo.getMitreRuleBo()).map(MitreRuleBo::getTacticList).orElse(Collections.emptyList())
                        .stream().map(tactic -> MetriTacticEnum.getByTactic(tactic).getI18nMessage()).filter(StringUtils::isNotBlank).collect(Collectors.joining(",")));


        LocalDateTime dateTime = mitreAttackEventBo.getBaseEventBo().getTime();
        if (Objects.nonNull(dateTime)) {
            ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.systemDefault());
            hostMitreAttackEventDto.setFormatTime(M_D_HH_MM_SS_DATE_FORMAT.format(dateTime)).setTimestamp(zonedDateTime.withZoneSameInstant(ZoneId.of("UTC")).format(INTERNATIONAL_DATE_TIME_FORMAT));
        }

        return hostMitreAttackEventDto;
    }
}
