 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name InjectFileUploadService.java.
 // File size 710.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.service.upload;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @ClassName InjectFileUploadService
 * @Description 注入文件
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseUploadService
 */
@Service
public class InjectFileUploadService extends BaseUploadService {

    public InjectFileUploadService build(MultipartFile file) {
        this.file = file;
        return this;
    }

    @Override
    protected List<String> supportUploadType() {
        return Lists.newArrayList();
    }
}
