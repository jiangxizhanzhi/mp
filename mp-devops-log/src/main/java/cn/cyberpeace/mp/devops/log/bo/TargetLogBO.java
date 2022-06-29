package cn.cyberpeace.mp.devops.log.bo;

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
public class TargetLogBO {

    /**
     * 靶标ID
     */
    private String uuid;

    /**
     * 靶标名称
     */
    private String name;

    /**
     * 实装类型
     */
    private String equType;

    /**
     * IP,多个IP以逗号分隔
     */
    private String equIp;
}
