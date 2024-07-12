 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name StatusReporter.java.
 // File size 241.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.core.scene;

/**
 * @ClassName StatusReporter
 * @Description
 * @Author maojun
 * @Date 2022/3/21
 * @Version 1.0.0
 */
public interface StatusReporter<T> {
    public void reportStatus(T data);
}
