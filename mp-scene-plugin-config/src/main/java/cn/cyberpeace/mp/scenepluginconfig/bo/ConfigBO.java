package cn.cyberpeace.mp.scenepluginconfig.bo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class ConfigBO {
    private List<Map<String, Object>> config = new ArrayList<>();
}
