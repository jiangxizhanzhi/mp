 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OptionMetaModel.java.
 // File size 416.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.model;

import lombok.Data;

/**
 * @ClassName OptionMetaModel
 * @Description
 * @Author zhouxinghuai
 * @Date 2022/8/15 14:39
 * @Version 1.0.0
 */
@Data
public class OptionMetaModel {

    public static Integer OPTION_VISIBLE_YES = 1;

    public static Integer OPTION_VISIBLE_NO = 0;

    private String activKeys;

    private Integer visible;

}
