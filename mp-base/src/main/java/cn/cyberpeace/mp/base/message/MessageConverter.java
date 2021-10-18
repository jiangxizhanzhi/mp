package cn.cyberpeace.mp.base.message;

public interface MessageConverter {

    <T> T fromMessageString(String messageString, Class<T> messageClass);

    <T> String toMessageString(T message);

}
