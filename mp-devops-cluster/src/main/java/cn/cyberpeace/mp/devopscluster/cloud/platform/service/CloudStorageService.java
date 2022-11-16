package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;

import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.BaseModel;
import cn.cyberpeace.mp.devopscluster.vo.FileVO;
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
    public JSONObject downloadFile(String conId, String downloadFile, HttpServletResponse response) {
//        InputStream responseStream = super.streamGet(BaseCloudPlatformConstant.CPCLOUD_CLOUD_STORAGE + "/file?con_id="
//                + conId + "&download_file=" + downloadFile);
//        ResponseUtil.transferStream(downloadFile, responseStream, response);

        BaseModel result = super.sendGet(BaseCloudPlatformConstant.CPCLOUD_CLOUD_STORAGE + "/file?con_id=" + conId
                + "&download_file=" + downloadFile, BaseModel.class);
        return convertToJson(result);
    }

    /**
     * 获取云盘目录list
     * 
     * @throws Exception
     */
    public JSONObject list(String conId, String path) throws Exception {
        BaseModel result = super.sendGet(
                BaseCloudPlatformConstant.CPCLOUD_CLOUD_STORAGE + "/list?con_id=" + conId + "&path=" + path,
                BaseModel.class);
        List<FileVO> vos = genFileVOs(convertToJson(result));

        // 为了和搜索结果返回格式一致
        JSONObject data = new JSONObject();
        data.put("total", vos.size());
        data.put("list", vos);
        JSONObject returnJson = new JSONObject();
        returnJson.put("data", data);

        return returnJson;
    }

    /**
     * 搜索文件
     * 
     * @throws Exception
     */
    public JSONObject search(String conId, String searchWord) throws Exception {
        BaseModel result = super.sendGet(BaseCloudPlatformConstant.CPCLOUD_CLOUD_STORAGE + "/search?con_id=" + conId
                + "&search_word=" + searchWord, BaseModel.class);
        return convertToJson(result);
    }

    /**
     * json转换
     * 
     * @param baseModel
     * @return
     */
    private JSONObject convertToJson(BaseModel baseModel) {
        String httpResponse = baseModel.getHttpResponse();
        return StringUtils.isBlank(httpResponse) ? new JSONObject() : JSONObject.parseObject(httpResponse);
    }

    /**
     * 标准输出格式化文件列表
     * 
     * @param stdout
     * @return
     * @throws Exception
     */
    private List<FileVO> genFileVOs(JSONObject resultJson) throws Exception {
        List<FileVO> vos = Lists.newArrayList();

        String stdout = resultJson.getString("stdout");
        if (stdout == null) {
            throw new Exception(resultJson.getString("message"));
        }
        String[] files = stdout.trim().split("\\n");
        if (files.length <= 1) {
            return vos;
        }

        for (int i = 1; i < files.length; i++) {
            String entry = files[i].replaceAll("\\s+", " ");
            String[] line = entry.split(" ");

            FileVO vo = new FileVO();
            vo.setAuthority(line[0]);
            vo.setRefers(Integer.parseInt(line[1]));
            vo.setOwner(line[2]);
            vo.setOwnerGroup(line[3]);
            vo.setSize(Long.parseLong(line[4]));
            vo.setModifyTime(line[5] + " " + line[6] + " " + line[7]);
            // 文件名可能有空格
            vo.setName(entry.substring(entry.lastIndexOf(line[7]) + line[7].length() + 1));
            vos.add(vo);
        }

        return vos;
    }

}
