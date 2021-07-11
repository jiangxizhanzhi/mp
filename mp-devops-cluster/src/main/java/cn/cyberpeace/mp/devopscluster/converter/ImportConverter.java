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


@Service
@Slf4j
@RequiredArgsConstructor
public class ImportConverter implements BaseConverter{

    private final ImportService importService;

    private final SysActivationService sysActivationService;

    @Autowired
    private DevopsClusterConfig devopsClusterConfig;

    
    public ImportDiskSpaceVO getDiskSpace() {
        ImportDiskSpaceCloudPlatformResult diskSpaceModel = importService.getDiskSpace();

        try {
            
            ActivationResponse activationResponse = sysActivationService.status();
            ActivationItem activationItem = activationResponse.getItems().stream().filter(item ->
                    ("LCS-RES-CR-ACTIVENODES").equals(item.getKey())).findFirst().get();
            BigDecimal maxCount = new BigDecimal(activationItem.getValue());
            BigDecimal maxSize = new BigDecimal(devopsClusterConfig.getMaxTargetSize());
            
            BigDecimal multi = new BigDecimal(1024);
            
            Long reserveSize = maxSize.multiply(maxCount).multiply(multi).multiply(multi).multiply(multi).longValue();
            
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
