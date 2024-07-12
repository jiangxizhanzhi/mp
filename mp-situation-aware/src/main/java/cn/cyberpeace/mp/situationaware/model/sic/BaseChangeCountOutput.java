 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseChangeCountOutput.java.
 // File size 728.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.model.sic;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/04/11 10:41
 */
@Data
public class BaseChangeCountOutput {

    // 是否有变动，综合
    @JSONField(name = "changed_count")
    private String changedCount;

    @JSONField(name = "added_count")
    private String addedCount;

    @JSONField(name = "values_count")
    private String valuesCount;

    @JSONField(name = "deleted_count")
    private String deletedCount;

    @JSONField(name = "modified_count")
    private String modifiedCount;

    @JSONField(name = "data")
    private String data;

}
