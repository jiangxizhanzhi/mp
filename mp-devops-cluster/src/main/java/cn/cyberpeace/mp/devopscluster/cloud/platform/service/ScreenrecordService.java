package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.response.ScreenshotUsedSpaceResponse;
import cn.cyberpeace.mp.cscene.service.CsceneService;
import cn.cyberpeace.mp.cscene.service.ScreenshotService;
import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import cn.cyberpeace.mp.devopscluster.cloud.platform.params.ScreenRecordData;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.constant.DevopsClusterErrorCode;
import cn.cyberpeace.mp.devopscluster.request.ScreenrecordBatchDeleteRequest;
import cn.cyberpeace.mp.devopscluster.request.ScreenrecordConfigRequest;
import cn.cyberpeace.mp.devopscluster.request.ScreenrecordDeleteRequest;
import cn.cyberpeace.mp.devopscluster.request.queryparam.ScreenrecordQueryParams;
import cn.cyberpeace.mp.devopscluster.vo.*;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Remote;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.RecordRestoreLimitConfigData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.RecordRestoreLimitConfigResult;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


@Service
@Slf4j
public class ScreenrecordService extends BaseCpService {

    public ScreenrecordService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    @Autowired
    private DevopsClusterConfig devopsClusterConfig;

    @Autowired
    private ScreenshotService screenshotService;

    @Autowired
    private UserService userService ;

    @Autowired
    private CsceneService csceneService;

    
    public ListObjServiceResult<ScreenrecordVO> pageScreenrecord(ScreenrecordQueryParams queryParams,String sceneId) {
        queryParams.ensure();
        StringBuffer getParams = new StringBuffer("?");
        getParams.append("limit=").append(Objects.nonNull(queryParams.getPageSize()) ? queryParams.getPageSize() : queryParams.getPerPage())
                .append("&page=").append(queryParams.getPage());
        if (StringUtils.isNotBlank(queryParams.getSearch())) {
            getParams.append("&params=").append(queryParams.getSearch());
        }
        if (StringUtils.isNotBlank(queryParams.getStartTime())) {
            getParams.append("&start_time=").append(queryParams.getStartTime());
        }
        if (StringUtils.isNotBlank(queryParams.getEndTime())) {
            getParams.append("&end_time=").append(queryParams.getEndTime());
        }
        if (StringUtils.isNotBlank(sceneId)) {
            getParams.append("&scene_id=").append(sceneId);
        }
        if (StringUtils.isNotBlank(queryParams.getUserId())) {
            getParams.append("&user_id=").append(queryParams.getUserId());
        }
        if (StringUtils.isNotBlank(queryParams.getNodeId())) {
            getParams.append("&host_id=").append(queryParams.getNodeId());
        }
        if (Objects.nonNull(queryParams.getOrders())) {
            String sortKey = queryParams.getOrders()[0];
            if (sortKey.startsWith("-")) {
                sortKey = sortKey.substring(1);
                getParams.append("&sort_dir=").append("desc");
            } else {
                getParams.append("&sort_dir=").append("asc");
            }
            if ("scene_name".equals(sortKey) || "sceneName".equals(sortKey)) {
                getParams.append("&sort_key=").append("scene");
            } else if ("file_size".equals(sortKey) || ("fileSize".equals(sortKey))) {
                getParams.append("&sort_key=").append("file_size");
            } else if ("create_time".equals(sortKey) || ("createTime".equals(sortKey))) {
                getParams.append("&sort_key=").append("recordate");
            }
        }

        ScreenrecordPageVO pageResponse = super.sendGet(BaseCloudPlatformConstant.SCREENRECORD_METHOD +
                "/recordfile_list" + getParams, ScreenrecordPageVO.class);
        log.info("page screenrecord response: {}", pageResponse);

        
        var ids = csceneService.getOneSceneInstanceAllElementIds(sceneId);

        List<ScreenrecordVO> responseList = new ArrayList<>();
        Integer count = 0;
        if (Objects.nonNull(pageResponse) && CollectionUtils.isNotEmpty(pageResponse.getRecord())) {
            count = pageResponse.getTotalnum();
            Set<String> existUserIds = new HashSet<>();
            pageResponse.getRecord().forEach(record -> {
                if (ids.size() == 0 || ids.contains(record.getHostId())) {
                    
                    ScreenrecordVO response = new ScreenrecordVO();
                    response.parseResult(record);
                    responseList.add(response);
                    existUserIds.add(response.getUserId());
                }
            });
            
            if (!existUserIds.isEmpty()) {
                List<UserBo> users = userService.listByUserIds(existUserIds.toArray(new String[]{}));
                if (CollectionUtils.isNotEmpty(users)) {
                    Map<String, UserBo> userId2Bo = new HashMap<>();
                    users.forEach(u -> userId2Bo.put(u.getId(), u));
                    responseList.forEach(res -> {
                        if (userId2Bo.containsKey(res.getUserId())) {
                            var userBo = userId2Bo.get(res.getUserId());
                            res.setUserName(userBo.getName());
                            res.setUserAvatar(userBo.getAvatar());
                        }
                    });
                }
            }
        }

        return new ListObjServiceResult(new ListObjResult<>(responseList, count)).asResult();
    }

    
    public ServiceResult<Boolean> removeRecordfile(ScreenrecordDeleteRequest request) {
        JSONObject delObj = new JSONObject();
        delObj.put("recordtime", request.getRecordTime());
        ScreenrecordDelVO delResponse = super.sendDelete(BaseCloudPlatformConstant.SCREENRECORD_METHOD +
                "/remove_file", delObj.toJSONString(), ScreenrecordDelVO.class);

        log.info("remove record file response: {}", delResponse);
        if (Objects.nonNull(delResponse)) {
            return ResultCommon.success(delResponse.isSuccess());
        } else {
            log.error("remove record file error");
        }

        return ResultCommon.success(false);
    }

    
    public ServiceResult<ScreenRecordUsedSpaceVO> queryUsedSpace() {
        CpcloudRecordUsedSpaceVO cpResponse = super.sendGet(BaseCloudPlatformConstant.SCREENRECORD_METHOD +
                "/storage_info", CpcloudRecordUsedSpaceVO.class);
        log.info("query screen record used space response: {}", cpResponse);
        ScreenRecordUsedSpaceVO response = new ScreenRecordUsedSpaceVO();
        if (Objects.nonNull(cpResponse)) {
            response.setUsedSpace(cpResponse.getRecordDirSize());
            response.setTotalSpace(cpResponse.getDiskTotal());
            response.setOtherUseSize(cpResponse.getOtherUseSize());
        }

        return ResultCommon.success(response);
    }

    
    public ServiceResult<SpaceStaticsVO> querySpaceStatics() {
        
        long totalSpace = 0L;
        
        long leftSpace = 0L;
        
        long screenshotUsed = 0L;
        
        long screenRecordUsed = 0L;
        
        long otherUsed = 0L;
        
        long usedTotal = 0L;
        
        double usedPercent = 0;
        
        double otherPercent = 0;
        
        double leftPercent = 0;
        
        double screenRecordPercent = 0;
        
        double screenshotPercent = 0;

        
        CpcloudRecordUsedSpaceVO cpResponse = super.sendGet(BaseCloudPlatformConstant.SCREENRECORD_METHOD +
                "/storage_info", CpcloudRecordUsedSpaceVO.class);
        log.info("query screen record used space response: {}", cpResponse);
        if (Objects.nonNull(cpResponse)) {
            totalSpace = cpResponse.getDiskTotal();
            screenRecordUsed = cpResponse.getRecordDirSize();
            otherUsed = cpResponse.getOtherUseSize();
        }

        ServiceResult<ScreenshotUsedSpaceResponse> screenshotUsedSpace = screenshotService.queryUsedSpace();
        log.info("query screen shop used space response: {}", screenshotUsedSpace);
        if (Objects.nonNull(screenshotUsedSpace.getData())) {
            ScreenshotUsedSpaceResponse screenshotUsedSpaceResponse = screenshotUsedSpace.getData();
            if (Objects.nonNull(screenshotUsedSpaceResponse.getUsedSpace())) {
                screenshotUsed = screenshotUsedSpaceResponse.getUsedSpace();
            }
        }
        
        usedTotal = screenRecordUsed + screenshotUsed;
        
        leftSpace = totalSpace - usedTotal - otherUsed;
        if (leftSpace < 0) {
            leftSpace = 0L;
        }
        usedPercent = new BigDecimal(usedTotal * 100).divide(new BigDecimal(totalSpace), 2, RoundingMode.HALF_UP).doubleValue();
        otherPercent = new BigDecimal(otherUsed * 100).divide(new BigDecimal(totalSpace), 2, RoundingMode.HALF_UP).doubleValue();
        leftPercent = new BigDecimal(leftSpace * 100).divide(new BigDecimal(totalSpace), 2, RoundingMode.HALF_UP).doubleValue();
        screenRecordPercent = new BigDecimal(screenRecordUsed * 100).divide(new BigDecimal(totalSpace), 2, RoundingMode.HALF_UP).doubleValue();
        screenshotPercent = new BigDecimal(screenshotUsed * 100).divide(new BigDecimal(totalSpace), 2, RoundingMode.HALF_UP).doubleValue();
        SpaceStaticsVO spaceStaticsVO = SpaceStaticsVO.builder()
                .leftSpace(leftSpace)
                .usedTotal(usedTotal)
                .screenRecordUsed(screenRecordUsed)
                .screenshotUsed(screenshotUsed)
                .otherUsed(otherUsed)
                .usedPercent(usedPercent)
                .otherPercent(otherPercent)
                .leftPercent(leftPercent)
                .totalSpace(totalSpace)
                .screenshotPercent(screenshotPercent)
                .screenRecordPercent(screenRecordPercent)
                .build();

        return ResultCommon.success(spaceStaticsVO);
    }

    
    public ServiceResult<Boolean> batchDeleteScreenRecord(ScreenrecordBatchDeleteRequest request) {
        JSONObject delObj = new JSONObject();
        JSONArray delArray = new JSONArray();
        request.getRecordNames().forEach(name -> delArray.add(name));
        delObj.put("recording_file", delArray);

        ScreenrecordDelVO delResponse = super.sendDelete(BaseCloudPlatformConstant.SCREENRECORD_METHOD +
                "/filemanager", delObj.toJSONString(), ScreenrecordDelVO.class);

        log.info("batch delete record file response: {}", delResponse);
        if (Objects.nonNull(delResponse)) {
            return ResultCommon.success(delResponse.isSuccess());
        } else {
            log.error("batch delete record file error");
        }

        return ResultCommon.success(false);
    }

    
    public ServiceResult<String> getScreenRecordUrl(String fileName) {
        
        if (StringUtils.isBlank(fileName) || !fileName.endsWith(".mp4") || fileName.contains("../")) {
            throw new MpRuntimeException(DevopsClusterErrorCode.SCREEN_RECORD_ERROR);
        }

        BufferedInputStream bin = null;
        OutputStream byteArrayOutputStream = null;
        ScreenRecordData data = null;
        try {
            URL url = new URL(devopsClusterConfig.getCpUrl() + BaseCloudPlatformConstant.SCREENRECORD_METHOD + "/download_record_file?recordfilename=" + fileName);
            
            URLConnection urlConnection = url.openConnection();
            
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            
            httpURLConnection.connect();
            bin = new BufferedInputStream(httpURLConnection.getInputStream());
            byteArrayOutputStream = new ByteArrayOutputStream();
            int len = 0;
            byte[] buffer = new byte[1024 * 5];
            while ((len = bin.read(buffer)) > 0) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            String resStr = byteArrayOutputStream.toString();
            data = JsonCommon.snakeParse(resStr, ScreenRecordData.class);
        } catch (Exception e) {
            log.error("download screenrecord error for fileName:{}", fileName, e);
        } finally {
            IOUtils.closeQuietly(bin);
            IOUtils.closeQuietly(byteArrayOutputStream);
        }
        if (Objects.isNull(data) || !data.isSuccess()) {
            log.error("failed to get screen record file url,{}", data);
            throw new MpRuntimeException(DevopsClusterErrorCode.SCREEN_RECORD_ERROR);
        }
        return ResultCommon.success(data.getUrl());
    }

    public ServiceResult<ScreenRecordRestoreConfigVO> getRestoreLimitConfig() {
        RecordRestoreLimitConfigResult result = new Remote().getRecordRestoreLimitConfig();
        var config = new ScreenRecordRestoreConfigVO();
        config.setFileLimitSpace(result.getCleanRecordFileLimitSpace());
        config.setFileLimitTime(result.getCleanRecordFileLimitTime());
        return new ServiceResult<>(config);
    }

    public void updateRestoreLimitConfig(ScreenrecordConfigRequest request){
        RecordRestoreLimitConfigData data = new RecordRestoreLimitConfigData();
        data.setCleanRecordFileLimitTime(request.getFileLimitTime());
        data.setCleanRecordFileLimitSpace(request.getFileLimitSpace());
        new Remote().updateRecordRestoreLimitConfig(data);
    }

}
