package cn.cyberpeace.mp.devopscluster.converter.bo;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
public class DeviceAsyncBO {

    
    private List<Integer> notExistedList = Lists.newArrayList();

    
    private List<Integer> existedList = Lists.newArrayList();
}
