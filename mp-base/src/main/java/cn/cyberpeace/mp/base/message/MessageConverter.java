package cn.cyberpeace.mp.base.message;

/**
 * @ClassName MessageConverter
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public interface MessageConverter {

    /**
     * fromMessageString
     *
     * @param messageString
     * @param messageClass
     * @return
     */
    <T> T fromMessageString(String messageString, Class<T> messageClass);

    /**
     * toMessageString
     *
     * @param message
     * @return
     */
    <T> String toMessageString(T message);

}
