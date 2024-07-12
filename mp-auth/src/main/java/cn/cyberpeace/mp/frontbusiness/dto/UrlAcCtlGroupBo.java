 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UrlAcCtlGroupBo.java.
 // File size 844.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.frontbusiness.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName UrlAcCtlGroupBo
 * @Description
 * @Author shgang
 * @Date 2022/11/18
 * @Version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlAcCtlGroupBo {

    public static final String QUERY_FIELD_GROUP = "group";
    public static final String QUERY_FIELD_URL = "url";
    public static final String QUERY_FIELD_IS_PATTERN = "isPattern";
    public static final String QUERY_FIELD_METHODS= "methods";
    public static final String QUERY_FIELD_TARGETS = "targets";

    private String group;

    private String targets;

    private String url;

    private Integer isPattern;

    private List<String> methods;

}
