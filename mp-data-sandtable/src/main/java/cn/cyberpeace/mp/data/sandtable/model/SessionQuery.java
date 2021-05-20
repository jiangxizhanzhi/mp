package cn.cyberpeace.mp.data.sandtable.model;

import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionQuery {

    /**
     * 查询开始时间
     */
    private LocalDateTime startTime;

    /**
     * 查询
     */
    private LocalDateTime endTime;

    private String groupId;

    /**
     * 查询条件需要的分组
     */
    private List<String> groupList;

    private String sourceIp;

    private String targetIp;

    /**
     * 查询条数默认限制
     */
    private Integer limitSize = 100;
}
