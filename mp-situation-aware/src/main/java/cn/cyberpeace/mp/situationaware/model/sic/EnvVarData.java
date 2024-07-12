 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name EnvVarData.java.
 // File size 578.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.model.sic;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Map;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/04/11 10:41
 */
@Data
public class EnvVarData {

    @JSONField(name = "deleted")
    private Map<String, String> deleted;

    @JSONField(name = "added")
    private Map<String, String> added;

    @JSONField(name = "modified")
    private Map<String, String> modified;

    @JSONField(name = "env")
    private Map<String, String> env;


}
