 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ConvertRule.java.
 // File size 1627.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.util.cloudmessageconvert;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName ConvertRule
 * @Description
 * @Author maojun
 * @Date 2022/7/11
 * @Version 1.0.0
 */
@Getter
@Setter
class ConvertRule {
    private static final String START_WITH = "start_with";
    private static final String END_WITH = "end_with";
    private static final String CONTAINS = "contains";
    private static final String REGX = "regx";

    private Pattern pattern;

    private String relation;

    private String keyword;

    /**
     * hit
     *
     * @param target
     * @return boolean
     */
    public boolean hit(String target) {
        if (START_WITH.equals(relation)) {
            return StringUtils.startsWith(target, keyword);
        } else if (END_WITH.equals(relation)) {
            return StringUtils.endsWith(target, keyword);
        } else if (CONTAINS.equals(relation)) {
            return StringUtils.contains(target, keyword);
        } else if (REGX.equals(relation)) {
            if (pattern == null) {
                pattern = Pattern.compile(keyword);
            }
            Matcher matcher = pattern.matcher(target);
            return matcher.matches();
        } else {
            return false;
        }
    }

    /**
     * setKeyword
     *
     * @param keyword
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword.toLowerCase();
    }
}
