package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.CloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.common.ComputeStatusModel;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service
@Slf4j
public class ComputeStatusService extends BaseCpService {

    private static final String COMPUTE_STATUS = "/compute_status";


    public ComputeStatusService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    /**
     * 查询
     * @param getParams
     * @return
     */
    public CloudPlatformResult<ComputeStatusModel> getComputeStatus(String getParams){
        CloudPlatformResult result = super.sendGet(BaseCloudPlatformConstant.CPCLOUD_INFO_URL + BaseCloudPlatformConstant.URL_VERSION + COMPUTE_STATUS + getParams, CloudPlatformResult.class);
        super.checkResult(result);
        ComputeStatusModel data = JSONObject.parseObject(JSONObject.toJSONString(result.getData()), ComputeStatusModel.class);
        processDiskNetIO((JSONObject) result.getData(), data);
        CloudPlatformResult cpresult = new CloudPlatformResult();
        cpresult.setData(data);
        cpresult.setCode(result.getCode());
        return cpresult;
    }

    private void processDiskNetIO(JSONObject jsonObject, ComputeStatusModel model) {
        if (Objects.isNull(jsonObject)) {
            return;
        }
        JSONObject netIOObj = jsonObject.getJSONObject("net_io");
        if (Objects.nonNull(netIOObj)) {
            ComputeStatusModel.NetIo netIo = new ComputeStatusModel.NetIo();
            String bytesRatio = netIOObj.getString("bytes_sent/bytes_recv");
            if (StringUtils.isNotBlank(bytesRatio)) {
                netIo.setSentRecvBytesRatio(Double.valueOf(bytesRatio));
            }

            String packRatio = netIOObj.getString("packets_sent/packets_recv");
            if (StringUtils.isNotBlank(packRatio)) {
                netIo.setSentRecvPacketsRatio(Double.valueOf(packRatio));
            }
            model.setNetIo(netIo);
        }


        JSONObject diskIOObj = jsonObject.getJSONObject("disk_io");
        if (Objects.nonNull(diskIOObj)) {
            ComputeStatusModel.DiskIo diskIo = new ComputeStatusModel.DiskIo();

            String countRatio = diskIOObj.getString("read_count/write_count");
            if (StringUtils.isNotBlank(countRatio)) {
                diskIo.setReadWriteCountRatio(Double.valueOf(countRatio));
            }

            String bytesRatio = diskIOObj.getString("read_bytes/write_bytes");
            if (StringUtils.isNotBlank(bytesRatio)) {
                diskIo.setReadWriteBytesRatio(Double.valueOf(bytesRatio));
            }

            String timeRatio = diskIOObj.getString("read_time/write_time");
            if (StringUtils.isNotBlank(timeRatio)) {
                diskIo.setReadWriteBytesRatio(Double.valueOf(timeRatio));
            }

            String mergedCntRatio = diskIOObj.getString("read_merged_count/write_merged_count");
            if (StringUtils.isNotBlank(mergedCntRatio)) {
                diskIo.setReadWriteBytesRatio(Double.valueOf(mergedCntRatio));
            }

            String busyTime = diskIOObj.getString("busy_time");
            if (StringUtils.isNotBlank(busyTime)) {
                diskIo.setBusytime(Long.valueOf(busyTime));
            }

            model.setDiskIo(diskIo);
        }

    }

}
