package cn.cyberpeace.mp.log.utils;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wangssh
 * @version 1.0
 * @description Yaml重写时注释回写，该方法慎用，如需使用请充分测试
 * 注意事项：
 * 1. 如果替换内容中存在非注释 #，替换内容可能出现不一致问题
 * 2. 如果注释放在key后部，#号与前面内容至少有一个空格符相隔
 * @date 2023/6/5
 **/
@Slf4j
public class YamlUtils {

    private static final ThreadLocal<List<Comment>> THREAD_LOCAL = new ThreadLocal<>();

    private static final String C = "#";
    private static final Integer ONE = 1;
    private static final String ENTER = System.lineSeparator();
    private static final String CHARSET = StandardCharsets.UTF_8.toString();

    private YamlUtils() {
    }

    public static void buildComment(String path) {
        List<Comment> commentList = new ArrayList<>();
        THREAD_LOCAL.set(commentList);
        Map<String, Integer> keyCountMap = new HashMap<>();
        try {
            List<String> lines = FileUtils.readLines(new File(path), CHARSET);
            int gap = 0;
            for (int i = 0; i < lines.size(); i = i + 1 + gap) {
                gap = 0;
                String line = lines.get(i);
                String lineTrim = line.trim();
                if (lineTrim.startsWith(C) || StringUtils.isEmpty(lineTrim)) {
                    String nextLine = "";
                    while (i + gap + 1 < lines.size() && (lines.get(i + gap + 1).trim().startsWith(C) || StringUtils.isEmpty(lines.get(i + gap + 1).trim()))) {
                        gap++;
                        nextLine = lines.get(i + gap);
                        line = line + ENTER + nextLine;
                    }
                    if (i + gap + 1 < lines.size()) {
                        gap++;
                    }
                    nextLine = lines.get(i + gap);
                    Comment comment = new Comment();
                    comment.setKey(nextLine);
                    comment.setReplaceKey(line + ENTER + nextLine);
                    comment.setSort(keyCountMap.merge(nextLine, ONE, Integer::sum));
                    commentList.add(comment);
                } else if (lineTrim.contains(C) && lineTrim.contains(" " + C)) {
                    String key = (C + StringUtils.substringBefore(line, C)).trim().substring(1);
                    Comment comment = new Comment();
                    comment.setKey(key);
                    comment.setReplaceKey(line);
                    comment.setSort(keyCountMap.merge(key, ONE, Integer::sum));
                    commentList.add(comment);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addComment(String path) {
        try {
            String content = FileUtils.readFileToString(new File(path), CHARSET);
            if (CollectionUtils.isEmpty(THREAD_LOCAL.get())) {
                log.info("no yaml comment");
                return;
            }
            for (Comment comment : THREAD_LOCAL.get()) {
                content = replacePosition(content, comment.getKey(), comment.getReplaceKey(), comment.getSort());
            }
            FileUtils.writeStringToFile(new File(path), content, CHARSET);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            THREAD_LOCAL.remove();
        }
    }

    /**
     * 替换第position个匹配的目标子串
     *
     * @param str         源字符串。
     * @param target      需要替换的目标子串。
     * @param replacement 需要替换成的字符串。
     * @return 将源字符串中出现的第n个target换成replacement后的字符串。
     */
    public static String replacePosition(String str, String target, String replacement, int position) {
        if (str == null || target == null || replacement == null) {
            log.error("Invalid params. str:{}, target:{}, replacement:{}", str, target, replacement);
            throw new NullPointerException();
        }
        String result = str;
        try {
            if (position < 2) {
                return StringUtils.replaceOnce(str, target, replacement);
            }
            for (int i = 1; i < position; i++) {
                result = replaceSecond(result, target, replacement);
            }
        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
        }
        return result;
    }


    /**
     * 替换第二个匹配的目标子串
     *
     * @param str         源字符串。
     * @param target      需要替换的目标子串。
     * @param replacement 需要替换成的字符串。
     * @return 将源字符串中出现的第二个target换成replacement后的字符串。
     */
    public static String replaceSecond(String str, String target, String replacement) {
        if (str == null || target == null || replacement == null) {
            throw new NullPointerException();
        }
        int index = str.indexOf(target);
        if (index == -1) {
            throw new MpRuntimeException("Not Found.");
        }
        index = str.indexOf(target, index + target.length());
        if (index == -1) {
            throw new MpRuntimeException("Not Found.");
        }
        String str1 = str.substring(0, index);
        String str2 = str.substring(index);
        str2 = StringUtils.replaceOnce(str2, target, replacement);
        return str1 + str2;
    }

}

class Comment {

    /**
     * 替换前的
     */
    private String key;

    /**
     * 替换后的
     */
    private String replaceKey;

    /**
     * 位置(默认为1，一般替换前的行内容(key)都不一样)：若存在替换前的行内容(key)一致,则需此参数
     */
    private Integer sort;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getReplaceKey() {
        return replaceKey;
    }

    public void setReplaceKey(String replaceKey) {
        this.replaceKey = replaceKey;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "key='" + key + '\'' +
                ", replaceKey='" + replaceKey + '\'' +
                ", sort=" + sort +
                '}';
    }
}
