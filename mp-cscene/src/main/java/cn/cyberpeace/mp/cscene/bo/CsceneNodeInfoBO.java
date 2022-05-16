package cn.cyberpeace.mp.cscene.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description
 * @Author maojun
 * @Date 2022/5/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CsceneNodeInfoBO {
    private String csceneId;
    private String csceneName;
    private String nodeEid;
    private String nodeId;
    private String nodeName;
}
