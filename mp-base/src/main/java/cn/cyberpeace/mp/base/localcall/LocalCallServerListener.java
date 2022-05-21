package cn.cyberpeace.mp.base.localcall;

import cn.cyberpeace.mp.base.annotation.LocalCallFunc;
import cn.cyberpeace.mp.base.annotation.LocalCallServer;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.base.util.filelistener.FileModifyListener;
import cn.hutool.core.io.FileUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


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
 */
@Slf4j
public class LocalCallServerListener extends FileModifyListener {

    @Getter
    private final Map<String, Object> localCallServerMap = new HashMap<>();

    private Lock fileChangedLock = new ReentrantLock(true);

    private boolean hasNextChange = false;

    public LocalCallServerListener(String filepath) {
        super(filepath);
    }

    /**
     * run this method after bean initiated
     */
    public static void initStart(String projectName, ApplicationContext context) {
        var messageFilepath = StringUtils.joinWith(FileUtil.FILE_SEPARATOR,
                FileUtil.getTmpDirPath(), projectName, "local_call_message_file");
        log.info("Local call message file: {}", messageFilepath);

        var localCallServerListener = new LocalCallServerListener(messageFilepath);
        var serverBeans = context.getBeansWithAnnotation(LocalCallServer.class);
        serverBeans.forEach((serverBeanName, serverBean) -> {
            localCallServerListener.getLocalCallServerMap().put(serverBean.getClass().getName(), serverBean);
        });
        localCallServerListener.startListen();
    }

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

    private void runFileChanged(String filePath) {
        var file = new File(filePath);
        if (file.length() == 0) {
            return;
        }

        var lines = FileUtil.readUtf8Lines(file);
        try (var fileWriter = new FileWriter(file);) {
            fileWriter.write(StringUtils.EMPTY);
            fileWriter.flush();
        } catch (IOException e) {
            log.error("Local call message file({}) clean error: {}", filePath, e);
        }

        List<Runnable> tasks = new ArrayList<>();
        lines.forEach(line -> {
            List<String> lineParts = new ArrayList<>(Arrays.asList(line.split(" ")));
            var methodName = lineParts.get(0);
            final String[] args = lineParts.size() > 1 ?
                    lineParts.subList(1, lineParts.size()).toArray(new String[0]) : new String[0];
            tasks.add(() -> call(methodName, args));
        });

        TaskCommon.runTasks(tasks, true, true);
    }

    private void call(String formatMethodName, String... args) {
        List<String> methodPaths = new ArrayList<>(Arrays.asList(formatMethodName.split("::")));
        if (methodPaths.size() != 2) {
            log.error("Local call method({}) args({}) error: invalid method", formatMethodName, args);
            return;
        }

        var className = methodPaths.get(0);
        Object serverBean = localCallServerMap.getOrDefault(className, null);
        if (serverBean == null) {
            log.error("Local call method({}) args({}) error: class({}) not found", formatMethodName, args, className);
            return;
        }

        var methodName = methodPaths.get(1);
        if (StringUtils.isEmpty(methodName)) {
            log.error("Local call method({}) args({}) error: method not found", formatMethodName, args);
            return;
        }

        Class<?> serverClass = serverBean.getClass();
        var parameterTypes = new Class[args.length];
        for (var i = 0; i < args.length; i++) {
            parameterTypes[i] = String.class;
        }

        var method = ClassUtils.getMethod(serverClass, methodName, parameterTypes);
        if (method == null) {
            log.error("Local call method({}) args({}) on class({}) error: method not found", formatMethodName, args,
                    serverClass);
            return;
        }

        var localCallFuncAnnotation = method.getAnnotation(LocalCallFunc.class);
        if (localCallFuncAnnotation == null) {
            log.error("Local call method({}) args({}) on class({}) error: no such method", formatMethodName, args,
                    serverClass);
            return;
        }

        try {
            method.invoke(serverBean, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Local call method({}) args({}) on class({}) error: {}", formatMethodName, args, serverClass, e);
        }
    }

}
