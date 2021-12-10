package cn.cyberpeace.mp.devops.cluster.cloud.platform.service.impl;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.BaseCloudPlatformService;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.constant.BaseConstants;
import cn.cyberpeace.mp.devops.cluster.request.ScreenrecordBatchDeleteRequest;
import cn.cyberpeace.mp.devops.cluster.request.ScreenrecordDeleteRequest;
import cn.cyberpeace.mp.devops.cluster.request.queryparam.ScreenrecordQueryParams;
import cn.cyberpeace.mp.devops.cluster.response.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
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

    @Value("${mp.devops.cp-url}")
    private String cpUrl;

    /**
     * 分页查询录屏文件
     * @param queryParams
     * @return
     */
    public ListObjServiceResult<ScreenrecordResponse> pageScreenrecord(ScreenrecordQueryParams queryParams) {
        StringBuffer getParams = new StringBuffer(BaseConstants.QUESTION_MARK);
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

        ScreenrecordPageResponse pageResponse = super.sendGet(BaseCloudPlatformService.SCREENRECORD_METHOD +
                "/recordfile_list" + getParams, ScreenrecordPageResponse.class);
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
                "/remove_file", delObj.toJSONString(), ScreenrecordDelResponse.class);

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

    /**
     * 下载录屏文件
     * @param fileName
     * @param response
     */
    public void download(String fileName, HttpServletResponse response) {
        BufferedInputStream bin = null;
        OutputStream os = null;
        try {
            URL url = new URL(cpUrl + BaseCloudPlatformService.SCREENRECORD_METHOD + "/downloadfile?recordfilename=" + fileName);
            // 建立连接
            URLConnection urlConnection = url.openConnection();
            // http的连接类
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            // 设置字符编码
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            // 打开到此 URL引用的资源的通信链接
            httpURLConnection.connect();
            bin = new BufferedInputStream(httpURLConnection.getInputStream());
            os = response.getOutputStream();
            int len = 0;
            byte[] buffer = new byte[1024 * 5];
            while ((len = bin.read(buffer)) > 0) {
                os.write(buffer, 0, len);
            }
            os.flush();
        } catch (Exception e) {
            log.error("download screenrecord error for fileName:{}", fileName, e);
        } finally {
            IOUtils.closeQuietly(bin);
            IOUtils.closeQuietly(os);
        }
    }

}
