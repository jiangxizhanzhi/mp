 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ResourceParseWrapper.java.
 // File size 714.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.util.resourcename;

import lombok.Getter;

import javax.validation.constraints.NotNull;

/**
 * @ClassName ResourceParseWrapper
 * @Description
 * @Author maojun
 * @Date 2022/5/18
 * @Version 1.0.0
 */
@Getter
public class ResourceParseWrapper<T extends RsourceNameParams> {
    private boolean success;
    private T result;

    /**
     * ResourceParseWrapper
     */
    public ResourceParseWrapper() {
        this.success = false;
    }

    /**
     * ResourceParseWrapper
     *
     * @param result
     */
    public ResourceParseWrapper(@NotNull T result) {
        this.result = result;
        this.success = true;
    }
}
