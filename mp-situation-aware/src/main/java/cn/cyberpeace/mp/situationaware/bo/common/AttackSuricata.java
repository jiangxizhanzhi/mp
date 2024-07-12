 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AttackSuricata.java.
 // File size 586.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.bo.common;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttackSuricata{

    /**
     * 分类英文描述
     */
    private List<String> category;

    /**
     * 分类英文描述
     */
    private List<String> categoryAtt;

    /**
     * 分类中文描述
     */
    private List<String> categoryZh;

    /**
     * 严重性
     */
    private List<Integer> severity;

}
