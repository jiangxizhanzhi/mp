package cn.cyberpeace.mp.sceneplugincloud.service.param;

import lombok.Data;

import java.util.List;

@Data
public class GroupCountFilter {
    private List<String> hosts;
    private List<Integer> virtualTypes;
}
