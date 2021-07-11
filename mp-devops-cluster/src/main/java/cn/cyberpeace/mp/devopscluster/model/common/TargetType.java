package cn.cyberpeace.mp.devopscluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TargetType implements Serializable {

    
    private Integer id;

    
    private String name;

    
    private Integer parentId;

    
    private List<TargetType> child;
}
