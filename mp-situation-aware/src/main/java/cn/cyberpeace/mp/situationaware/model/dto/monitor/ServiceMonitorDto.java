package cn.cyberpeace.mp.situationaware.model.dto.monitor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    //private String type;

    private String name;

    // 运行时长（秒）
    private Long uptime;

    // ERROR, DOWN, RUNNNING
    private String state;

    private String ip;

}
