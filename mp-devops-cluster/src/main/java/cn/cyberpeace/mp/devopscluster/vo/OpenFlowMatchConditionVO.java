 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OpenFlowMatchConditionVO.java.
 // File size 516.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName OpenFlowMatchConditionVO
 * @Description 流表基础信息
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
public class OpenFlowMatchConditionVO {

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 入端口
     */
    private Integer inPort;

    /**
     *
     */
    private Integer dlVlan;


}
