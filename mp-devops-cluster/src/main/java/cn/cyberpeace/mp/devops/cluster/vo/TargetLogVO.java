package cn.cyberpeace.mp.devops.cluster.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author：Geek Wang
 * @date: 2022/6/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TargetLogVO {

    /**
     * 靶标ID
     */
    private String uuid;

    /**
     * 靶标名称
     */
    private String name;
}
