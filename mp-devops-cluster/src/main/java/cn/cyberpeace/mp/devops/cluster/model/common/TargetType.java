package cn.cyberpeace.mp.devops.cluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TargetType implements Serializable {

    /**
     * 二级类型ID
     */
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 1级类型的ID
     */
    private Integer parentId;

    /**
     *
     */
    private List<TargetType> child;
}
