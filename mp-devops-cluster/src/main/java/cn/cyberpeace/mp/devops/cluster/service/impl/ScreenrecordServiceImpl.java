package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.base.constant.BaseConstants;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.request.ScreenrecordBatchDeleteRequest;
import cn.cyberpeace.mp.devops.cluster.request.ScreenrecordDeleteRequest;
import cn.cyberpeace.mp.devops.cluster.request.queryparam.ScreenrecordQueryParams;
import cn.cyberpeace.mp.devops.cluster.response.*;
import cn.cyberpeace.mp.devops.cluster.service.BaseCloudPlatformService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Description: 录屏service
 * @Author qiaokang
 * @Date：2021/9/13 16:40
 */
@Service
@Slf4j
public class ScreenrecordServiceImpl extends BaseOpenStackServiceImpl {

    public ScreenrecordServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig, objectMapper);
    }

    /**
     * 分页查询录屏文件
     * @param queryParams
     * @return
     */
    public ListObjServiceResult<ScreenrecordResponse> pageScreenrecord(ScreenrecordQueryParams queryParams) {
        StringBuffer getParams = new StringBuffer(BaseConstants.QUESTION_MARK);
        getParams.append("limit=").append(queryParams.getPageSize())
                .append("&page=").append(queryParams.getPage())
                .append("&params=").append(queryParams.getSearch())
                .append("&start_time=").append(queryParams.getStartTime())
                .append("&end_time=").append(queryParams.getEndTime());

        ScreenrecordPageResponse pageResponse = super.sendGet(BaseCloudPlatformService.SCREENRECORD_METHOD +
                "/recordfile_list/" + getParams, ScreenrecordPageResponse.class);
        log.info("page screenrecord response: {}", pageResponse);

        List<ScreenrecordResponse> responseList = new ArrayList<>();
        Integer count = 0;
        if (Objects.nonNull(pageResponse) && CollectionUtils.isNotEmpty(pageResponse.getRecord())) {
            count = pageResponse.getTotalnum();
            pageResponse.getRecord().forEach(record -> {
                // 结果转换
                ScreenrecordResponse response = new ScreenrecordResponse();
                response.parseResult(record);
                responseList.add(response);
            });
        }

        return new ListObjServiceResult(new ListObjResult<>(responseList, count)).asResult();
    }

    /**
     * 按时间删除录屏文件
     * @param request
     * @return
     */
    public ServiceResult<Boolean> removeRecordfile(ScreenrecordDeleteRequest request) {
        JSONObject delObj = new JSONObject();
        delObj.put("recordtime", request.getRecordTime());
        ScreenrecordDelResponse delResponse = super.sendDelete(BaseCloudPlatformService.SCREENRECORD_METHOD +
                "/removerecordfile", delObj.toJSONString(), ScreenrecordDelResponse.class);

        log.info("remove record file response: {}", delResponse);
        if (Objects.nonNull(delResponse)) {
            return ServiceResult.success(delResponse.isSuccess());
        } else {
            log.error("remove record file error");
        }

        return ServiceResult.success(false);
    }

    /**
     * 查询已用空间
     * @return
     */
    public ServiceResult<ScreenRecordUsedSpaceResponse> queryUsedSpace() {
        CpcloudRecordUsedSpaceResponse cpResponse = super.sendGet(BaseCloudPlatformService.SCREENRECORD_METHOD +
                "/storage_info", CpcloudRecordUsedSpaceResponse.class);
        log.info("query screen record used space response: {}", cpResponse);
        ScreenRecordUsedSpaceResponse response = new ScreenRecordUsedSpaceResponse();
        if (Objects.nonNull(cpResponse)) {
            response.setUsedSpace(cpResponse.getRecordDirSize());
            response.setTotalSpace(cpResponse.getDiskTotal());
        }

        return ServiceResult.success(response);
    }

    /**
     * 批量删除录屏文件
     * @param request
     * @return
     */
    public ServiceResult<Boolean> batchDeleteScreenRecord(ScreenrecordBatchDeleteRequest request) {
        JSONObject delObj = new JSONObject();
        JSONArray delArray = new JSONArray();
        request.getRecordNames().forEach(name -> delArray.add(name));
        delObj.put("recording_file", delArray);

        ScreenrecordDelResponse delResponse = super.sendDelete(BaseCloudPlatformService.SCREENRECORD_METHOD +
                "/filemanager", delObj.toJSONString(), ScreenrecordDelResponse.class);

        log.info("batch delete record file response: {}", delResponse);
        if (Objects.nonNull(delResponse)) {
            return ServiceResult.success(delResponse.isSuccess());
        } else {
            log.error("batch delete record file error");
        }

        return ServiceResult.success(false);
    }

}
