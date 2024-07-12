 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CharSequenceUtil.java.
 // File size 9332.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.util.mptool;

/**
 * @ClassName CharSequenceUtil
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public class CharSequenceUtil {
    public static final String EMPTY = "";
    public static final int INDEX_NOT_FOUND = -1;


    /**
     * subPre
     *
     * @param string
     * @param toIndexExclude
     * @return
     */
    public static String subPre(CharSequence string, int toIndexExclude) {
        return sub(string, 0, toIndexExclude);
    }

    /**
     * sub
     *
     * @param str
     * @param fromIndexInclude
     * @param toIndexExclude
     * @return
     */
    public static String sub(CharSequence str, int fromIndexInclude, int toIndexExclude) {
        if (isEmpty(str)) {
            return str(str);
        }
        int len = str.length();

        if (fromIndexInclude < 0) {
            fromIndexInclude = len + fromIndexInclude;
            if (fromIndexInclude < 0) {
                fromIndexInclude = 0;
            }
        } else if (fromIndexInclude > len) {
            fromIndexInclude = len;
        }

        if (toIndexExclude < 0) {
            toIndexExclude = len + toIndexExclude;
            if (toIndexExclude < 0) {
                toIndexExclude = len;
            }
        } else if (toIndexExclude > len) {
            toIndexExclude = len;
        }

        if (toIndexExclude < fromIndexInclude) {
            int tmp = fromIndexInclude;
            fromIndexInclude = toIndexExclude;
            toIndexExclude = tmp;
        }

        if (fromIndexInclude == toIndexExclude) {
            return EMPTY;
        }

        return str.toString().substring(fromIndexInclude, toIndexExclude);
    }

    /**
     * isEmpty
     *
     * @param str
     * @return boolean
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * str
     *
     * @param cs
     * @return
     */
    public static String str(CharSequence cs) {
        return null == cs ? null : cs.toString();
    }

    /**
     * isNotBlank
     *
     * @param str
     * @return boolean
     */
    public static boolean isNotBlank(CharSequence str) {
        return false == isBlank(str);
    }

    /**
     * isBlank
     *
     * @param str
     * @return boolean
     */
    public static boolean isBlank(CharSequence str) {
        int length;

        if ((str == null) || ((length = str.length()) == 0)) {
            return true;
        }

        for (int i = 0; i < length; i++) {
            // 只要有一个非空字符即为非空字符串
            if (false == CharUtil.isBlankChar(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * addSuffixIfNot
     *
     * @param str
     * @param suffix
     * @return
     */
    public static String addSuffixIfNot(CharSequence str, CharSequence suffix) {
        return appendIfMissing(str, suffix, suffix);
    }

    /**
     * appendIfMissing
     *
     * @param str
     * @param suffix
     * @param suffixes
     * @return
     */
    public static String appendIfMissing(CharSequence str, CharSequence suffix, CharSequence... suffixes) {
        return appendIfMissing(str, suffix, false, suffixes);
    }

    /**
     * appendIfMissing
     *
     * @param str
     * @param suffix
     * @param ignoreCase
     * @param testSuffixes
     * @return
     */
    public static String appendIfMissing(CharSequence str, CharSequence suffix, boolean ignoreCase, CharSequence... testSuffixes) {
        if (str == null || isEmpty(suffix) || endWith(str, suffix, ignoreCase)) {
            return str(str);
        }
        if (ArrayUtil.isNotEmpty(testSuffixes)) {
            for (final CharSequence testSuffix : testSuffixes) {
                if (endWith(str, testSuffix, ignoreCase)) {
                    return str.toString();
                }
            }
        }
        return str.toString().concat(suffix.toString());
    }

    /**
     * endWith
     *
     * @param str
     * @param suffix
     * @param isIgnoreCase
     * @return boolean
     */
    public static boolean endWith(CharSequence str, CharSequence suffix, boolean isIgnoreCase) {
        if (null == str || null == suffix) {
            return null == str && null == suffix;
        }

        if (isIgnoreCase) {
            return str.toString().toLowerCase().endsWith(suffix.toString().toLowerCase());
        } else {
            return str.toString().endsWith(suffix.toString());
        }
    }

    /**
     * removePrefix
     *
     * @param str
     * @param prefix
     * @return
     */
    public static String removePrefix(CharSequence str, CharSequence prefix) {
        if (isEmpty(str) || isEmpty(prefix)) {
            return str(str);
        }

        final String str2 = str.toString();
        if (str2.startsWith(prefix.toString())) {
            return subSuf(str2, prefix.length());// 截取后半段
        }
        return str2;
    }

    /**
     * subSuf
     *
     * @param string
     * @param fromIndex
     * @return
     */
    public static String subSuf(CharSequence string, int fromIndex) {
        if (isEmpty(string)) {
            return null;
        }
        return sub(string, fromIndex, string.length());
    }

    /**
     * isNotEmpty
     *
     * @param str
     * @return boolean
     */
    public static boolean isNotEmpty(CharSequence str) {
        return false == isEmpty(str);
    }

    /**
     * contains
     *
     * @param str
     * @param searchChar
     * @return boolean
     */
    public static boolean contains(CharSequence str, char searchChar) {
        return indexOf(str, searchChar) > -1;
    }

    /**
     * indexOf
     *
     * @param str
     * @param searchChar
     * @return int
     */
    public static int indexOf(final CharSequence str, char searchChar) {
        return indexOf(str, searchChar, 0);
    }

    /**
     * indexOf
     *
     * @param str
     * @param searchChar
     * @param start
     * @return int
     */
    public static int indexOf(CharSequence str, char searchChar, int start) {
        if (str instanceof String) {
            return ((String) str).indexOf(searchChar, start);
        } else {
            return indexOf(str, searchChar, start, -1);
        }
    }

    /**
     * indexOf
     *
     * @param str
     * @param searchChar
     * @param start
     * @param end
     * @return int
     */
    public static int indexOf(final CharSequence str, char searchChar, int start, int end) {
        if (isEmpty(str)) {
            return INDEX_NOT_FOUND;
        }
        final int len = str.length();
        if (start < 0 || start > len) {
            start = 0;
        }
        if (end > len || end < 0) {
            end = len;
        }
        for (int i = start; i < end; i++) {
            if (str.charAt(i) == searchChar) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * trim
     *
     * @param str
     * @return
     */
    public static String trim(CharSequence str) {
        return (null == str) ? null : trim(str, 0);
    }

    /**
     * trim
     *
     * @param str
     * @param mode
     * @return
     */
    public static String trim(CharSequence str, int mode) {
        String result;
        if (str == null) {
            result = null;
        } else {
            int length = str.length();
            int start = 0;
            int end = length;// 扫描字符串头部
            if (mode <= 0) {
                while ((start < end) && (CharUtil.isBlankChar(str.charAt(start)))) {
                    start++;
                }
            }// 扫描字符串尾部
            if (mode >= 0) {
                while ((start < end) && (CharUtil.isBlankChar(str.charAt(end - 1)))) {
                    end--;
                }
            }
            if ((start > 0) || (end < length)) {
                result = str.toString().substring(start, end);
            } else {
                result = str.toString();
            }
        }

        return result;
    }

    /**
     * removePrefixIgnoreCase
     *
     * @param str
     * @param prefix
     * @return
     */
    public static String removePrefixIgnoreCase(CharSequence str, CharSequence prefix) {
        if (isEmpty(str) || isEmpty(prefix)) {
            return str(str);
        }

        final String str2 = str.toString();
        if (str2.toLowerCase().startsWith(prefix.toString().toLowerCase())) {
            return subSuf(str2, prefix.length());// 截取后半段
        }
        return str2;
    }

    /**
     * startWith
     *
     * @param str
     * @param c
     * @return boolean
     */
    public static boolean startWith(CharSequence str, char c) {
        if (isEmpty(str)) {
            return false;
        }
        return c == str.charAt(0);
    }

}
