package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;

import cn.cyberpeace.mp.cscene.util.ResponseUtil;
import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.BaseModel;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description:
 * @author zhangjiaxin
 * @date 2022年11月11日上午9:13:53
 */
@Service
@Slf4j
public class CloudStorageService extends BaseCpService {

    public CloudStorageService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    /**
     * 云盘容量使用量
     */
    public JSONObject capacity(String conId) {
        BaseModel result = super.sendGet(BaseCloudPlatformConstant.CPCLOUD_CLOUD_STORAGE + "/capacity?con_id=" + conId,
                BaseModel.class);
        return convertToJson(result);
    }

    /**
     * 变更分类
     */
    public JSONObject changeFile(JSONObject request) {
        BaseModel result = super.sendPut(BaseCloudPlatformConstant.CPCLOUD_CLOUD_STORAGE + "/file",
                request.toJSONString(), BaseModel.class);
        return convertToJson(result);
    }

    /**
     * 删除文件或者目录
     */
    public JSONObject deleteFile(JSONObject request) {
        BaseModel result = super.sendDelete(BaseCloudPlatformConstant.CPCLOUD_CLOUD_STORAGE + "/file",
                request.toJSONString(), BaseModel.class);
        return convertToJson(result);
    }

    /**
     * 上传文件
     */
    public JSONObject uploadFile(String conId, String destPath, MultipartFile file) {
        BaseModel result;
        try {
            File uploadFile = new File(file.getOriginalFilename());
            FileUtils.copyInputStreamToFile(file.getInputStream(), uploadFile);

            result = super.sendFormDataPost(
                    BaseCloudPlatformConstant.CPCLOUD_CLOUD_STORAGE + "/file?con_id=" + conId
                            + (StringUtils.isBlank(destPath) ? "" : ("&dest_path=" + destPath)),
                    Lists.newArrayList(uploadFile), BaseModel.class);
            uploadFile.delete();

            return convertToJson(result);
        } catch (IOException e) {
            log.error("cloud storage upload file failed", e);
        }

        return null;
    }

    /**
     * 下载文件
     */
    public void downloadFile(String conId, String downloadFile, HttpServletResponse response) {
        InputStream responseStream = super.streamGet(BaseCloudPlatformConstant.CPCLOUD_CLOUD_STORAGE + "/list?con_id="
                + conId + "&download_file=" + downloadFile);
        ResponseUtil.transferStream(downloadFile, responseStream, response);
    }

    /**
     * 获取云盘目录list
     */
    public JSONObject list(String conId, String path) {
        BaseModel result = super.sendGet(
                BaseCloudPlatformConstant.CPCLOUD_CLOUD_STORAGE + "/list?con_id=" + conId + "&path=" + path,
                BaseModel.class);
        return convertToJson(result);
    }

    /**
     * 搜索文件
     */
    public JSONObject search(String conId, String searchWord) {
        BaseModel result = super.sendGet(BaseCloudPlatformConstant.CPCLOUD_CLOUD_STORAGE + "/list?con_id=" + conId
                + "&search_word=" + searchWord, BaseModel.class);
        return convertToJson(result);
    }

    private JSONObject convertToJson(BaseModel baseModel) {
        String httpResponse = baseModel.getHttpResponse();
        return StringUtils.isBlank(httpResponse) ? new JSONObject() : JSONObject.parseObject(httpResponse);
    }

}
