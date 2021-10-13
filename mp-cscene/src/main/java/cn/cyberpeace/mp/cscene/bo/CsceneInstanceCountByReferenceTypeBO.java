package cn.cyberpeace.mp.cscene.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wanggang
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
