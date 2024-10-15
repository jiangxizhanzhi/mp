 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ImportConverter.java.
 // File size 3006.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.ImportService;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.ImportDiskSpaceCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.vo.ImportDiskSpaceVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * @ClassName ImportConverter
 * @Description 靶标导入管理
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseConverter
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ImportConverter implements BaseConverter{

    private final ImportService importService;

    @Autowired
    private DevopsClusterConfig devopsClusterConfig;

    /**
     * 获取磁盘剩余空间
     * @return
     */
    public ImportDiskSpaceVO getDiskSpace() {
        ImportDiskSpaceCloudPlatformResult diskSpaceModel = importService.getDiskSpace();

        return ImportDiskSpaceVO.builder()
                .vmSpace(diskSpaceModel.getData().getVmSpace())
                .build();
    }
}
