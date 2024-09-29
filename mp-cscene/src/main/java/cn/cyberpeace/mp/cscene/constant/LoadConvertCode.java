package cn.cyberpeace.mp.cscene.constant;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class LoadConvertCode {

    private static Map<String, String> loadTypeMap = new HashMap<>();

    public static final String RUN_TABLE_OTHER = "1005020204";

    public static final String CONTROL_TYPE_OTHER = "1005020303";

    @PostConstruct
    private void init() {
        this.loadTypeMap.put("docker", "1005020201");
        this.loadTypeMap.put("VMware", "1005020202");
        this.loadTypeMap.put("VM", "1005020203");
        this.loadTypeMap.put("shell", "1005020301");
        this.loadTypeMap.put("web", "1005020302");
    }

    public String getTypeCode(String loadTypeName) {
        return this.loadTypeMap.get(loadTypeName);
    }

}
