 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FileModifyListener.java.
 // File size 3868.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.util.filelistener;

import cn.cyberpeace.mp.base.util.mptool.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

/**
 * @ClassName FileModifyListener
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
public abstract class FileModifyListener {

    private final String filepath;

    private Long fileLastModified;

    private Boolean stopped;

    private WatchKey watchKey;

    /**
     * FileModifyListener
     *
     * @param filepath
     */
    protected FileModifyListener(String filepath) {
        this.filepath = filepath;
    }

    /**
     * startListen
     */
    public void startListen() {
        stopped = false;

        if (!FileUtil.exist(filepath)) {
            FileUtil.mkParentDirs(filepath);
            File file = new File(filepath);
            try {
                if (!file.createNewFile()) {
                    log.warn("File({}) already exist", filepath);
                }
            } catch (IOException e) {
                log.error("File({}) watch start failed: {}", filepath, e);
                return;
            }
        }

        log.info("File({}) watch start", filepath);
        listen();

        if (Boolean.TRUE.equals(stopped)) {
            return;
        }

        log.warn("File({}) watch will try start listen again after 10s", filepath);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        startListen();
    }

    /**
     * stopListen
     */
    public void stopListen() {
        log.info("File({}) watch prepare stop", filepath);
        stopped = true;
        if (watchKey != null) {
            watchKey.cancel();
        }
    }

    /**
     * listen
     */
    protected void listen() {
        String fileDir = FileUtil.getParent(filepath, 1);
        String filename = FileUtil.getName(filepath);
        Path path = FileSystems.getDefault().getPath(fileDir);
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            while (true) {
                WatchKey wk = watchService.take();
                for (WatchEvent<?> event : wk.pollEvents()) {
                    Path changed = (Path) event.context();
                    if (changed.endsWith(filename)) {
                        long lastModified = new File(filepath).lastModified();
                        if (fileLastModified == null || fileLastModified != lastModified) {
                            log.debug("File({}) watch changed", filepath);
                            fileLastModified = lastModified;
                            try {
                                fileChanged(filepath);
                            } catch (Exception e) {
                                log.error("File({}) watch handle change error: {}", filepath, e);
                            }
                        }
                    }
                }

                boolean valid = wk.reset();
                if (!valid) {
                    log.error("File({}) watch stopped", filepath);
                    break;
                }

                if (Boolean.TRUE.equals(stopped)) {
                    watchKey.cancel();
                    break;
                }
            }
        } catch (IOException | InterruptedException e) {
            log.error("File({}) watch error: {}", filepath, e);
        }
    }

    /**
     * fileChanged
     *
     * @param filepath
     */
    protected abstract void fileChanged(String filepath);

}
