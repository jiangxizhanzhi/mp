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
    @ApiModelProperty("综合场景实例引用类型")
    private Integer referenceType;

    /**
     * 统计总数
     */
    @ApiModelProperty("总数")
    private Integer total;
}
