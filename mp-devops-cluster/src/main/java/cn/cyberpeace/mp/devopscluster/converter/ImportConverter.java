package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.ImportService;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.ImportDiskSpaceCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.vo.ImportDiskSpaceVO;
import cn.cyberpeace.mp.frontadmin.constant.AdminErrorCode;
import cn.cyberpeace.mp.frontadmin.dto.activation.ActivationItem;
import cn.cyberpeace.mp.frontadmin.response.activation.ActivationResponse;
import cn.cyberpeace.mp.frontadmin.service.sys.SysActivationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 靶标导入管理
 * @author qiaokang
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ImportConverter implements BaseConverter{

    private final ImportService importService;

    private final SysActivationService sysActivationService;

    @Autowired
    private DevopsClusterConfig devopsClusterConfig;

    /**
     * 获取磁盘剩余空间
     * @return
     */
    public ImportDiskSpaceVO getDiskSpace() {
        ImportDiskSpaceCloudPlatformResult diskSpaceModel = importService.getDiskSpace();

        try {
            // 获取最大并发启动场景节点数
            ActivationResponse activationResponse = sysActivationService.status();
            ActivationItem activationItem = activationResponse.getItems().stream().filter(item ->
                    ("LCS-RES-CR-ACTIVENODES").equals(item.getKey())).findFirst().get();
            BigDecimal maxCount = new BigDecimal(activationItem.getValue());
            BigDecimal maxSize = new BigDecimal(devopsClusterConfig.getMaxTargetSize());
            // 单位换算倍数
            BigDecimal multi = new BigDecimal(1024);
            // 预留容量
            Long reserveSize = maxSize.multiply(maxCount).multiply(multi).multiply(multi).multiply(multi).longValue();
            // 剩余空间 = 实际磁盘空间 - 预留容量
            diskSpaceModel.getData().setVmSpace(diskSpaceModel.getData().getVmSpace() - reserveSize);
        } catch (Exception e) {
            log.error("get disk space error", e);
            throw new MpRuntimeException(AdminErrorCode.SYS_ACTIVATION_ERROR);
        }

        return ImportDiskSpaceVO.builder()
                .vmSpace(diskSpaceModel.getData().getVmSpace())
                .build();
    }
}
