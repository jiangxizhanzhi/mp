package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Data
public class DockerParams {
    private Boolean run;
    private Boolean interactive;
    private Boolean privileged;
    private Map<String, String> environment;
    private String environmentStr;
    private Boolean tty;

    public void setEnvironmentStr(String environmentStr) {
        if (StringUtils.isNotEmpty(environmentStr)) {
            this.environmentStr = environmentStr;
            this.environment = new HashMap<>();
            if (StringUtils.isNotEmpty(environmentStr)) {
                String[] kvs = StringUtils.split(this.environmentStr, ";");
                for (String kv : kvs) {
                    String[] kvSplit = StringUtils.split(kv, "=");
                    if (kvSplit.length == 2) {
                        this.environment.put(kvSplit[0], kvSplit[1]);
                    }
                }
            }
        }
    }

    public void setEnvironment(Map<String, String> environment) {
        if (StringUtils.isEmpty(this.environmentStr)) {
            this.environment = environment;
            this.environmentStr = StringUtils.EMPTY;
            if (this.environment != null) {
                for (var e : this.environment.entrySet()) {
                    this.environmentStr += String.join(e.getKey(), e.getValue().toString());
                }
            }
        }
    }
}
