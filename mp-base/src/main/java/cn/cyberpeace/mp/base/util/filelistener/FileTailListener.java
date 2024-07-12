 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FileTailListener.java.
 // File size 2114.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.util.filelistener;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

import java.io.File;
import java.util.function.Consumer;

/**
 * @ClassName FileTailListener
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see TailerListenerAdapter
 */
@Slf4j
public class FileTailListener extends TailerListenerAdapter {

    private Tailer tailer;

    private final File file;
    private final Long delay;
    private final Boolean end;
    private final Consumer<String> handleLine;

    /**
     * FileTailListener
     *
     * @param file
     * @param handleLine
     */
    public FileTailListener(File file, Consumer<String> handleLine) {
        this(file, handleLine, 1000L);
    }

    /**
     * FileTailListener
     *
     * @param file
     * @param handleLine
     * @param delay
     */
    public FileTailListener(File file, Consumer<String> handleLine, long delay) {
        this(file, handleLine, delay, false);
    }

    /**
     * FileTailListener
     *
     * @param file
     * @param handleLine
     * @param delay
     * @param end
     */
    public FileTailListener(File file, Consumer<String> handleLine, long delay, boolean end) {
        this.file = file;
        this.delay = delay;
        this.end = end;
        this.handleLine = handleLine;
    }

    /**
     * handle
     *
     * @param line
     */
    @Override
    public void handle(String line) {
        handleLine.accept(line);
    }

    /**
     * start
     */
    public synchronized void start() {
        stop();

        log.info("Start file tailer: {}", file.getAbsolutePath());
        tailer = Tailer.create(file, this, delay, end);
    }

    /**
     * stop
     */
    public synchronized void stop() {
        if (tailer != null) {
            log.info("Stop file tailer: {}", file.getAbsolutePath());
            tailer.stop();
        }
        tailer = null;
    }

}
