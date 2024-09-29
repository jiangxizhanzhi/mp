package cn.cyberpeace.mp.cscene.request.load;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 任务删除
 */
@Data
@NoArgsConstructor
public class OutLoadTaskDeleteRequest {

    /**
     * 任务ids
     */
    private String[] taskIds;
}