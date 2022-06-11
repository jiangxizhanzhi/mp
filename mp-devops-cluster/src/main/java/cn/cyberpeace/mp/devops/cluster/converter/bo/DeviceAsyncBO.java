package cn.cyberpeace.mp.devops.cluster.converter.bo;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description:
 * @author：Geek Wang
 * @date: 2022/6/11
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
