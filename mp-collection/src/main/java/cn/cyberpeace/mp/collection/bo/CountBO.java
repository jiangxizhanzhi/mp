package cn.cyberpeace.mp.collection.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangjiaxin
 * @Description:
 * @date 2022年10月21日上午9:15:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountBO {

    private String key;
    private long count;

}
