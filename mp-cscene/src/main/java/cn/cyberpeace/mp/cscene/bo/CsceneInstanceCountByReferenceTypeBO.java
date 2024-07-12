 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneInstanceCountByReferenceTypeBO.java.
 // File size 787.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName CsceneInstanceCountByReferenceTypeBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CsceneInstanceCountByReferenceTypeBO {

    /**
     * 综合场景实例引用类型
     */
    @ApiModelProperty("综合场景实例引用类型，课程训练：140，综合训练：235，红蓝对抗：214，靶标测试：221，竞技演练：310")
    private Integer referenceType;

    /**
     * 统计总数
     */
    @ApiModelProperty("统计总数")
    private Integer total;
}
