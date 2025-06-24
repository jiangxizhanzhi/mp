 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OptionMetaModel.java.
 // File size 369.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.dto.option;

import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/8/15 14:39
 */
@Data
public class OptionMetaModel {

    public static final Integer OPTION_VISIBLE_YES = 1;

    public static final Integer OPTION_VISIBLE_NO = 0;

    private String activKeys;

    private Integer visible;

}
