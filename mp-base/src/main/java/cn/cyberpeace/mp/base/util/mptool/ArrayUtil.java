package cn.cyberpeace.mp.base.util.mptool;

/**
 * @ClassName ArrayUtil
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public class ArrayUtil {

    public static final int INDEX_NOT_FOUND = -1;

    /**
     * isNotEmpty
     *
     * @param array
     * @return boolean
     */
    public static <T> boolean isNotEmpty(T[] array) {
        return (null != array && array.length != 0);
    }

    /**
     * contains
     *
     * @param array
     * @param value
     * @return boolean
     */
    public static boolean contains(char[] array, char value) {
        return indexOf(array, value) > INDEX_NOT_FOUND;
    }

    /**
     * indexOf
     *
     * @param array
     * @param value
     * @return int
     */
    public static int indexOf(char[] array, char value) {
        if (null != array) {
            for (int i = 0; i < array.length; i++) {
                if (value == array[i]) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }
}
