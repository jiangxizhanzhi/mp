package cn.cyberpeace.mp.flow.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ListSceneRequest implements Serializable {

    // 场景名称
    private String name;

    // 数量限制
    private Integer limit;
}
