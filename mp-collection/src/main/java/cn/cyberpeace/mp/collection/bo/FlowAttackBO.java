package cn.cyberpeace.mp.collection.bo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FlowAttackBO {
    private String ip;

    private long count;

    private long size;

    private List<AttackTypeBO> attackTypes;

    private LocalDateTime time;
}
