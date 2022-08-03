package cn.cyberpeace.mp.situationaware.model.dto.monitor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/7/15 09:05
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceMonitorDto {

    private String launchId;

    private String hostname;

    //private String type;

    private String name;

    private String serviceName;

    // 运行时长（秒）
    private Long uptime;

    // ERROR, DOWN, RUNNING, RESTARTING
    private String state;

    private String ip;

    // 如果某个服务在restart-restrict-list表中，且状态是RUNNING || RESTARTING，则禁止该服务重启，既disableRestart=true，
    // 为false（默认值）表示允许重启，1：服务不在限制列表中；2：服务在限制列表中，但是状态不是RUNNING
    private Boolean disableRestart = false;

    // 采集时间
    private LocalDateTime scanTime;

}
