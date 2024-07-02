package cn.cyberpeace.mp.devopscluster.converter.bo;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName DeviceAsyncBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
public class DeviceAsyncBO {

    /**
     * 不存在ID集合
     */
    private List<Integer> notExistedList = Lists.newArrayList();

    /**
     * 已存在ID集合
     */
    private List<Integer> existedList = Lists.newArrayList();
}
