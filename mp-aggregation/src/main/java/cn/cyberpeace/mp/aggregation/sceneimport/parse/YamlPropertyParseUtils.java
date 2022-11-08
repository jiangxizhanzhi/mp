package cn.cyberpeace.mp.aggregation.sceneimport.parse;

import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description yml文件中key名称格式化为驼峰
 * @Author maojun
 * @Date 2021/10/11
 */
class YamlPropertyParseUtils extends PropertyUtils {
    private static final Pattern middleLinePattern = Pattern.compile("-([a-z])");

    private static final Pattern underLinePattern = Pattern.compile("_([a-z])");

    public YamlPropertyParseUtils() {
        this.setSkipMissingProperties(true);
    }

    @Override
    public Property getProperty(Class<?> type, String name) {
        String formatName = name;
        if (formatName.contains("-")) {
            formatName = this.lineToHump(formatName, middleLinePattern);
        }
        if (formatName.contains("_")) {
            formatName = this.lineToHump(formatName, underLinePattern);
        }
        return super.getProperty(type, formatName);
    }

    public String lineToHump(String str, Pattern linePattern) {
        StringBuilder sb = new StringBuilder();
        Matcher matcher = linePattern.matcher(str);
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
