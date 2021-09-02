package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.Installer;
import cn.cyberpeace.mp.devops.cluster.model.InstallerCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.InstallerFormDataCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.service.InstallerService;
import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
public class InstallerServiceImpl extends BaseOpenStackServiceImpl implements InstallerService {

    public InstallerServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig, objectMapper);
    }

    @Override
    public InstallerCloudPlatformModel page(String getParams) {
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + "/installer/lists" + getParams, InstallerCloudPlatformModel.class);
    }

    @Override
    public Installer add(InstallerFormDataCloudPlatformModel installer, List<MultipartFile> multipartFiles) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> installerMap = mapper.readValue(mapper.writeValueAsString(installer), Map.class);
        Set<String> keySet = installerMap.keySet();
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        for (String key : keySet) {
            if (Objects.nonNull(installerMap.get(key)))
            builder.addPart(BeanUtils.humpToLine(key), new StringBody(installerMap.get(key).toString(), ContentType.create("text/plain", Consts.UTF_8)));
        }

        int count = 1;
        if (CollectionUtils.isNotEmpty(multipartFiles)) {
            for (MultipartFile file : multipartFiles) {
                builder.addBinaryBody("file" + count, convertMultipartFile(file));
                count++;
            }
        }
        if (StringUtils.isNotBlank(installer.getUuid())){
            return super.sendFormDataPut(CPCLOUD_DEVICE + URL_VERSION + "/installer", builder.build(), Installer.class);
        }else {
           return super.sendFormDataPost(CPCLOUD_DEVICE + URL_VERSION + "/installer/lists", builder.build(), Installer.class);
        }
    }



    @Override
    public Installer update(String uuid, Installer installer) {
        return super.sendPut(CPCLOUD_DEVICE + URL_VERSION + "/installer/" + uuid,JsonUtils.toUnderlineJSONString(installer),Installer.class);
    }

    @Override
    public Installer findById(String id) {
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + "/installer/" + id, Installer.class);
    }

    @Override
    public Boolean deleteByIds(List<String> ids) {
        var jsonObject = new JSONObject();
        jsonObject.put("uuids", ids);
        BaseCloudPlatformModel model = super.sendDelete(CPCLOUD_DEVICE + URL_VERSION + "/installer/lists",JSONObject.toJSONString(jsonObject),BaseCloudPlatformModel.class);
        return model.isSuccess();
    }

    /**
     *
     * @param file
     * @return
     * @throws Exception
     */
    protected File convertMultipartFile(MultipartFile file) {

        File toFile = null;
        if (file.equals("") || file.getSize() <= 0) {
            file = null;
        } else {
            InputStream ins = null;
            try {
                ins = file.getInputStream();
                toFile = new File(file.getOriginalFilename());
                inputStreamToFile(ins, toFile);
                ins.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return toFile;
    }

    /**
     * 获取流文件
     * @param ins
     * @param file
     */
    private void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = ins.read(buffer, 0, 1024)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
