package cn.cyberpeace.mp.cscene.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName CsceneInstanceCountByReferenceTypeModel
 * @Description
 * @Author wanggang
 * @Date 2024/07/02
 * @Version 1.0.0
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
