// Copyright (C) 2024
// All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TargetActivBo.java.
 // File size 546.
 // File create date Fri Jan 17 09:57:14 CST 2025.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.bo.target;

import cn.cyberpeace.mp.auth.model.TargetModel;
import lombok.Data;

import java.util.List;

 /**
  * 目标活动bo
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Data
public class TargetActivBo extends TargetModel {

    // 表示这个菜单不受系统激活项的限制，如帮助中心等，无论系统激活项如何选择，都应该显示
    public static final String NO_AUTH = "no_auth";

    private List<String> active;

    private List<String> edition;

}
