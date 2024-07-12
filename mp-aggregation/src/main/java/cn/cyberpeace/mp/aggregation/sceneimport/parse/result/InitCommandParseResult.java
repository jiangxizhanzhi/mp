 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name InitCommandParseResult.java.
 // File size 1057.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.sceneimport.parse.result;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName InitCommandParseResult
 * @Description
 * @Author maojun
 * @Date 2021/10/12
 * @Version 1.0.0
 */
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
