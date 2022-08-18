package cn.cyberpeace.mp.devopslog.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CpCloudLogPO {

    /**
     * 节点
     */
    private String hostname;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 路径
     */
    private String path;

    /**
     * 节点ip
     */
    private String hostIp;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatetime;
}
