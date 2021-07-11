package cn.cyberpeace.mp.sceneplugincloud.util.cloudmessageconvert;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


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

    public void setKeyword(String keyword) {
        this.keyword = keyword.toLowerCase();
    }
}
