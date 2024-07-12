 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name WazuhParamsConvert.java.
 // File size 376.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.paramsconvert;

import java.util.List;

/**
 * @ClassName WazuhParamsConvert
 * @Description
 * @Author hewei
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public interface WazuhParamsConvert <T>{
    /**
     * convertParams
     *
     * @return
     */
    public List<T> convertParams();
}
