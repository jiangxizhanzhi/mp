package cn.cyberpeace.mp.cscene.request.load;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 载荷标签修改
 */
@Data
@NoArgsConstructor
public class OutLoadModifyTagsRequest {

    /**
     * 载荷标签
     */
    private String[] tags;
}