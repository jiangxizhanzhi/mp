package cn.cyberpeace.mp.cscene.request.load;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 载荷执行结果查询
 */
@Data
@NoArgsConstructor
public class LoadResultSearchRequest {

    /**
     * 工具pks
     */
    private List<String> pks;

    /**
     * 开始时间
     */
    private String beginTime;

    /**
     * 结束时间
     */
    private String finishTime;

    /**
     * 场景Id
     */
    private String sceneId;
}