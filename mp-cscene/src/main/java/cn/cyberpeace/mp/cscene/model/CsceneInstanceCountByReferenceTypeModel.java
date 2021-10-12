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

    private String target;

    private Integer total;
}
