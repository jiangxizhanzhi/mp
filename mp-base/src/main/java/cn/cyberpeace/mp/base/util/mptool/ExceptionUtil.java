package cn.cyberpeace.mp.base.util.mptool;

import org.springframework.util.FastByteArrayOutputStream;

import java.io.PrintStream;
import java.util.Map;

public class ExceptionUtil {

    /**
     * 堆栈转为完整字符串
     * Params:
     * throwable – 异常对象
     */
    public static String stacktraceToString(Throwable throwable) {

        return stacktraceToString(throwable, 3000, null);
    }

    /**
     * stacktraceToString
     *
     * @param throwable
     * @param limit
     * @param replaceCharToStrMap
     * @return
     */
    public static String stacktraceToString(Throwable throwable, int limit, Map<Character, String> replaceCharToStrMap) {
        final FastByteArrayOutputStream baos = new FastByteArrayOutputStream();
        throwable.printStackTrace(new PrintStream(baos));
        String exceptionStr = baos.toString();
        int length = exceptionStr.length();
        if (limit > 0 && limit < length) {
            length = limit;
        }

        if (CollectionUtil.isNotEmpty(replaceCharToStrMap)) {
            final StringBuilder sb = new StringBuilder();
            char c;
            String value;
            for (int i = 0; i < length; i++) {
                c = exceptionStr.charAt(i);
                value = replaceCharToStrMap.get(c);
                if (null != value) {
                    sb.append(value);
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        } else {
            return StrUtil.subPre(exceptionStr, limit);
        }
    }


}
