package cn.cyberpeace.mp.devopscluster.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Builder
public class ServiceRestartStatusDto {

    
    private LocalDateTime restartTime;

    private String status;

}
