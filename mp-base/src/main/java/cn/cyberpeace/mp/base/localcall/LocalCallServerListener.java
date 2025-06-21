 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LocalCallServerListener.java.
 // File size 10040.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.localcall;

import cn.cyberpeace.mp.base.annotation.LocalCallFunc;
import cn.cyberpeace.mp.base.annotation.LocalCallServer;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.filelistener.FileModifyListener;
import cn.cyberpeace.mp.base.util.mptool.FileUtil;
import cn.cyberpeace.mp.base.util.thread.taskpool.DefaultPoolTask;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * 例子:
 *
 * @Slf4j
 * @Component
 * @LocalCallServer
 * public class TestLocalCallServer {
 *
 *     @LocalCallFunc
 *     public void test(String param) {
 *         log.info("TestLocalCallServer test: {}", param);
 *     }
 *
 * }
 *
 * 调用:
 * 使用生成的local_call.sh/local_call.bat调用，第一个参数方法，后续参数为方法参数，方法参数不能带空格
 *     local_call.sh/local_call.bat 包名.类名::方法名 方法参数
 * 调用例子:
 *     local_call.sh com.test.test.TestLocalCallServer::test 123
 *
 */
@Slf4j
public class LocalCallServerListener extends FileModifyListener {

    @Getter
    private final Map<String, Object> localCallServerMap = new HashMap<>();

    private final Lock fileChangedLock = new ReentrantLock(true);

    private boolean hasNextChange = false;

    public LocalCallServerListener(String filepath) {
        super(filepath);
    }



    /**
     * fileChanged
     *
     * @param filePath
     */
    @Override
    protected void fileChanged(String filePath) {
        if (!fileChangedLock.tryLock()) {
            hasNextChange = true;
            return;
        }

        try {
            runFileChanged(filePath);
        } finally {
            fileChangedLock.unlock();
        }

        if (hasNextChange) {
            hasNextChange = false;
            fileChanged(filePath);
        }
    }

    /**
     * runFileChanged
     *
     * @param filePath
     */
    private void runFileChanged(String filePath) {
        File file = new File(filePath);
        if (file.length() == 0) {
            return;
        }

        List<String> lines = FileUtil.readUtf8Lines(file);
        try (FileWriter fileWriter = new FileWriter(file);) {
            fileWriter.write(StringUtils.EMPTY);
            fileWriter.flush();
        } catch (IOException e) {
            log.error("Local call message file({}) clean error: {}", filePath, e);
        }

        List<Runnable> tasks = new ArrayList<>();
        for (String line : lines) {
            String lineTrim = line.trim();
            List<String> lineParts = new ArrayList<>(Arrays.asList(
                    TextUtils.coupleStrip(lineTrim, "\"").split("\" \"")));
            String methodName = null;
            List<String> methodArgs = new ArrayList<>();
            for (int i = 0; i < lineParts.size(); i++) {
                String linePart = lineParts.get(i);
                if (i == 0) {
                    methodName = linePart;
                } else {
                    methodArgs.add(linePart);
                }
            }

            final String taskMethodName = methodName;
            final String[] taskMethodArgs = methodArgs.toArray(new String[0]);
            tasks.add(() -> call(taskMethodName, taskMethodArgs));
        }

        final List<DefaultPoolTask> defaultPoolTasks = TaskCommon.runTasks(tasks, true, true);
    }

    /**
     * call
     *
     * @param formatMethodName
     * @param args
     */
    private void call(String formatMethodName, String... args) {
        List<String> methodPaths = new ArrayList<>(Arrays.asList(formatMethodName.split("::")));
        if (methodPaths.size() != 2) {
            log.error("Local call method({}) args({}) error: invalid method", formatMethodName, args);
            return;
        }

        String className = methodPaths.get(0);
        Object serverBean = localCallServerMap.getOrDefault(className, null);
        if (serverBean == null) {
            log.error("Local call method({}) args({}) error: class({}) not found", formatMethodName, args, className);
            return;
        }

        String methodName = methodPaths.get(1);
        if (StringUtils.isEmpty(methodName)) {
            log.error("Local call method({}) args({}) error: method not found", formatMethodName, args);
            return;
        }

        Class<?> serverClass = serverBean.getClass();
        Class[] parameterTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            parameterTypes[i] = String.class;
        }

        Method method = ClassUtils.getMethod(serverClass, methodName, parameterTypes);
        if (method == null) {
            log.error("Local call method({}) args({}) on class({}) error: method not found", formatMethodName, args,
                    serverClass);
            return;
        }

        LocalCallFunc localCallFuncAnnotation = method.getAnnotation(LocalCallFunc.class);
        if (localCallFuncAnnotation == null) {
            log.error("Local call method({}) args({}) on class({}) error: no such method", formatMethodName, args,
                    serverClass);
            return;
        }

        try {
            method.invoke(serverBean, (Object[]) args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Local call method({}) args({}) on class({}) error: {}", formatMethodName, args, serverClass, e);
        }
    }

}
