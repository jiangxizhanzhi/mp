package cn.cyberpeace.mp.devopscluster.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/7/29 13:57
 */
@Data
@Builder
public class ServiceRestartStatusDto {

    // 用于判断查询到的采集状态是否是过期状态
    private LocalDateTime restartTime;

    private String status;

}
