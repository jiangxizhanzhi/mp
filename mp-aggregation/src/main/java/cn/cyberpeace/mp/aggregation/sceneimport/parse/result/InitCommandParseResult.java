package cn.cyberpeace.mp.aggregation.sceneimport.parse.result;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class InitCommandParseResult {
    private String o;
    private String n;
    private String s;

    public List<String> getAllCommands() {
        List<String> result = new ArrayList<>();
        if (this.o != null) {
            if (Boolean.TRUE.equals(BeanCommon.getBean(CsceneAppConfig.class).getEnableCommandChange())) {
                result.add("n:" + this.o);
            } else {
                result.add("o:" + this.o);
            }
        }
        if (this.s != null) {
            result.add("s:" + this.s);
        }
        if (this.n != null) {
            result.add("n:" + this.n);
        }
        return result;
    }
}
