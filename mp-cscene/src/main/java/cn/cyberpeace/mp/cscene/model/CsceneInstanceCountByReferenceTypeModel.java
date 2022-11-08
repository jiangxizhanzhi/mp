package cn.cyberpeace.mp.cscene.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wanggang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CsceneInstanceCountByReferenceTypeModel {

    /**
     * 综合场景实例引用类型
     */
    private Integer referenceType;

    /**
     * 统计总数
     */
    private Integer total;
}
