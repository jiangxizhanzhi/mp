 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FileTailListener.java.
 // File size 1489.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.util.filelistener;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

import java.io.File;
import java.util.function.Consumer;

@Slf4j
public class FileTailListener extends TailerListenerAdapter {

    private Tailer tailer;

    private final File file;
    private final Long delay;
    private final Boolean end;
    private final Consumer<String> handleLine;

    public FileTailListener(File file, Consumer<String> handleLine) {
        this(file, handleLine, 1000L);
    }

    public FileTailListener(File file, Consumer<String> handleLine, long delay) {
        this(file, handleLine, delay, false);
    }

    public FileTailListener(File file, Consumer<String> handleLine, long delay, boolean end) {
        this.file = file;
        this.delay = delay;
        this.end = end;
        this.handleLine = handleLine;
    }

    @Override
    public void handle(String line) {
        handleLine.accept(line);
    }

    public synchronized void start() {
        stop();

        log.info("Start file tailer: {}", file.getAbsolutePath());
        tailer = Tailer.create(file, this, delay, end);
    }

    public synchronized void stop() {
        if (tailer != null) {
            log.info("Stop file tailer: {}", file.getAbsolutePath());
            tailer.stop();
        }
        tailer = null;
    }

}
