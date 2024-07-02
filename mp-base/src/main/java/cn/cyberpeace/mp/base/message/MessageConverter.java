package cn.cyberpeace.mp.base.message;

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
