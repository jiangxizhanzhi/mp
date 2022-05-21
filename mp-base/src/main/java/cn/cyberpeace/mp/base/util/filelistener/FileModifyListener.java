package cn.cyberpeace.mp.base.util.filelistener;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;

@Slf4j
public abstract class FileModifyListener {

    private final String filepath;

    private Long fileLastModified;

    private Boolean stopped;

    private WatchKey watchKey;

    protected FileModifyListener(String filepath) {
        this.filepath = filepath;
    }

    public void startListen() {
        stopped = false;

        if (!FileUtil.exist(filepath)) {
            FileUtil.mkParentDirs(filepath);
            var file = new File(filepath);
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

    public void stopListen() {
        log.info("File({}) watch prepare stop", filepath);
        stopped = true;
        if (watchKey != null) {
            watchKey.cancel();
        }
    }

    protected void listen() {
        var fileDir = FileUtil.getParent(filepath, 1);
        var filename = FileUtil.getName(filepath);
        var path = FileSystems.getDefault().getPath(fileDir);
        try (var watchService = FileSystems.getDefault().newWatchService()) {
            watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            while (true) {
                var wk = watchService.take();
                for (var event : wk.pollEvents()) {
                    Path changed = (Path) event.context();
                    if (changed.endsWith(filename)) {
                        var lastModified = new File(filepath).lastModified();
                        if (fileLastModified == null || fileLastModified != lastModified) {
                            log.info("File({}) watch changed", filepath);
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

    protected abstract void fileChanged(String filepath);

}
