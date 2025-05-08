package cn.cyberpeace.mp.cscene.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneConfigAreaBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @BelongsPackage: cn.cyberpeace.mp.cscene.bo
 * @Author: hhuang
 * @CreateTime: 2025/05/08  09:28
 * @Description: 场景模板变化业务数据
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CsceneConfigChangeBO {

    private List<SceneConfigAreaBO> updateAreas;


    private List<SceneConfigElementBO> updateElements;
}
