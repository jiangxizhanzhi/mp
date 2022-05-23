package cn.cyberpeace.mp.base.localcall;

import cn.cyberpeace.mp.base.annotation.LocalCallFunc;
import cn.cyberpeace.mp.base.annotation.LocalCallServer;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.filelistener.FileModifyListener;
import cn.hutool.core.io.FileUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
     * run this method after bean initiated
     */
    public static void initStart(BaseAppConfig baseAppConfig, ApplicationContext context) {
        var messageFilepath = StringUtils.joinWith(FileUtil.FILE_SEPARATOR,
                FileUtil.getTmpDirPath(), baseAppConfig.getProjectName(), "local_call_message_file");
        log.info("Local call message file: {}", messageFilepath);

        var localCallServerListener = new LocalCallServerListener(messageFilepath);
        var serverBeans = context.getBeansWithAnnotation(LocalCallServer.class);
        serverBeans.forEach((serverBeanName, serverBean) -> {
            localCallServerListener.getLocalCallServerMap().put(serverBean.getClass().getName(), serverBean);
        });

        if (SystemUtils.IS_OS_WINDOWS) {
            prepareWindowsClientScript(baseAppConfig.getHomeDir(), messageFilepath);
        } else {
            prepareLinuxClientScript(baseAppConfig.getHomeDir(), messageFilepath);
        }

        localCallServerListener.startListen();
    }

    public static void prepareWindowsClientScript(String targetDir, String messageFilepath) {
        var scriptFilePath = StringUtils.joinWith(FileUtil.FILE_SEPARATOR, targetDir, "local_call.bat");
        log.info("Local call client script file: {}", scriptFilePath);

        FileUtil.mkParentDirs(scriptFilePath);
        List<String> contentLines = Stream.of(
                "@echo off",
                "set allparam=",
                "",
                ":param",
                "set str=%~1",
                "if not defined str (",
                "    goto end",
                ")",
                "if defined allparam (",
                "    set allparam=%allparam% \"%str%\"",
                ") else (",
                "    set allparam=\"%str%\"",
                ")",
                "shift /0",
                "goto param",
                "",
                ":end",
                "",
                "echo %allparam%>> " + messageFilepath
        ).collect(Collectors.toList());
        FileUtil.writeUtf8Lines(contentLines, scriptFilePath);
    }

    public static void prepareLinuxClientScript(String targetDir, String messageFilepath) {
        var scriptFilePath = StringUtils.joinWith(FileUtil.FILE_SEPARATOR, targetDir, "local_call.sh");
        log.info("Local call client script file: {}", scriptFilePath);

        FileUtil.mkParentDirs(scriptFilePath);
        List<String> contentLines = Stream.of(
                "#/bin/bash",
                "allparam=",
                "",
                "args=(\"$@\")",
                "for ((i=0;i<${#args[@]};i++))",
                "do",
                "if [ \"$allparam\" ];then",
                "    allparam=$allparam\" \"\\\"${args[i]}\\\"",
                "else",
                "    allparam=\\\"${args[i]}\\\"",
                "fi",
                "done",
                "",
                "echo $allparam>> " + messageFilepath
        ).collect(Collectors.toList());
        FileUtil.writeUtf8Lines(contentLines, scriptFilePath);

        try {
            Runtime.getRuntime().exec(String.format("chmod +x %s", scriptFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            line = line.trim();
            List<String> lineParts = new ArrayList<>(Arrays.asList(line.split(" ")));
            String methodName = null;
            List<String> methodArgs = new ArrayList<>();
            for (var i = 0; i < lineParts.size(); i++) {
                var linePart = lineParts.get(i);
                if (i == 0) {
                    methodName = linePart;
                    methodName = TextUtils.coupleStrip(methodName, "\"");
                    methodName = TextUtils.coupleStrip(methodName, "'");
                } else {
                    var methodArg = linePart;
                    methodArg = TextUtils.coupleStrip(methodArg, "\"");
                    methodArg = TextUtils.coupleStrip(methodArg, "'");
                    methodArgs.add(methodArg);
                }
            }

            final var taskMethodName = methodName;
            final var taskMethodArgs = methodArgs.toArray(new String[0]);
            tasks.add(() -> call(taskMethodName, taskMethodArgs));
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
