 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name MessageConverter.java.
 // File size 207.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:16 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.message;

public interface MessageConverter {

    <T> T fromMessageString(String messageString, Class<T> messageClass);

    <T> String toMessageString(T message);

}
