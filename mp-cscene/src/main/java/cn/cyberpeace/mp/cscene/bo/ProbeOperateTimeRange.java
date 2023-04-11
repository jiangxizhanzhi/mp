package cn.cyberpeace.mp.cscene.bo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @BelongsPackage: cn.cyberpeace.mp.cscene.bo
 * @Author: hhuang
 * @CreateTime: 2023/04/11  14:04
 * @Description: 时间范围bean
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
public class ProbeOperateTimeRange {

    private LocalDateTime from;
    private LocalDateTime to;

    public ProbeOperateTimeRange(String fromYYYYMMDDHHMMSS, String toYYYYMMDDHHMMSS) {
        from = LocalDateTime.parse(fromYYYYMMDDHHMMSS, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        to = LocalDateTime.parse(toYYYYMMDDHHMMSS, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
