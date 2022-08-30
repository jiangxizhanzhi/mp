package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.config.CommonAppConfig;
import cn.cyberpeace.mp.cscene.response.ScreenshotUsedSpaceResponse;
import cn.cyberpeace.mp.cscene.service.ScreenshotService;
import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import cn.cyberpeace.mp.devopscluster.config.DevopsConfig;
import cn.cyberpeace.mp.devopscluster.constant.DevopsClusterErrorCode;
import cn.cyberpeace.mp.devopscluster.request.ScreenrecordBatchDeleteRequest;
import cn.cyberpeace.mp.devopscluster.request.ScreenrecordDeleteRequest;
import cn.cyberpeace.mp.devopscluster.request.queryparam.ScreenrecordQueryParams;
import cn.cyberpeace.mp.devopscluster.vo.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
public class ScreenrecordService extends BaseCpService {

    public ScreenrecordService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    /**
     * 通过 commonAppConfig 获取云平台地址
     */
    @Autowired
    private CommonAppConfig commonAppConfig;

    @Autowired
    private ScreenshotService screenshotService;

    /**
     * 分页查询录屏文件
     * @param queryParams
     * @return
     */
    public ListObjServiceResult<ScreenrecordVO> pageScreenrecord(ScreenrecordQueryParams queryParams) {
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

        List<ScreenrecordVO> responseList = new ArrayList<>();
        Integer count = 0;
        if (Objects.nonNull(pageResponse) && CollectionUtils.isNotEmpty(pageResponse.getRecord())) {
            count = pageResponse.getTotalnum();
            pageResponse.getRecord().forEach(record -> {
                // 结果转换
                ScreenrecordVO response = new ScreenrecordVO();
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
        ScreenrecordDelVO delResponse = super.sendDelete(BaseCloudPlatformConstant.SCREENRECORD_METHOD +
                "/remove_file", delObj.toJSONString(), ScreenrecordDelVO.class);

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

        return ServiceResult.success(response);
    }

    /**
     * 查询资源用量情况
     * @return
     */
    public ServiceResult<SpaceStaticsVO> querySpaceStatics() {
        // 系统总空间
        long totalSpace = 0L;
        // 剩余可用空间
        long leftSpace = 0L;
        // 截屏已用空间
        long screenshotUsed = 0L;
        // 录屏已用空间
        long screenRecordUsed = 0L;
        // 其他占用空间
        long otherUsed = 0L;
        // 资源总占用空间
        long usedTotal = 0L;
        // 资源已用空间占比
        double usedPercent = 0;
        // 其他资源占比
        double otherPercent = 0;
        // 剩余占比
        double leftPercent = 0;
        // 录屏占比
        double screenRecordPercent = 0;
        // 截屏占比
        double screenshotPercent = 0;

        // 录屏占用空间信息
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
        // 资源占用空间
        usedTotal = screenRecordUsed + screenshotUsed;
        // 剩余可用空间
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

        return ServiceResult.success(spaceStaticsVO);
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

        ScreenrecordDelVO delResponse = super.sendDelete(BaseCloudPlatformConstant.SCREENRECORD_METHOD +
                "/filemanager", delObj.toJSONString(), ScreenrecordDelVO.class);

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
        // 录屏文件名称校验
        if (StringUtils.isBlank(fileName) || !fileName.endsWith(".mp4") || fileName.contains("../")) {
            throw new MpRuntimeException(DevopsClusterErrorCode.SCREEN_RECORD_ERROR);
        }

        BufferedInputStream bin = null;
        OutputStream os = null;
        try {
            URL url = new URL(commonAppConfig.getCpUrl() + BaseCloudPlatformConstant.SCREENRECORD_METHOD + "/downloadfile?recordfilename=" + fileName);
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
