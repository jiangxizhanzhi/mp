package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.CloudPlatformPageData;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.constant.DevopsClusterErrorCode;
import cn.cyberpeace.mp.devops.cluster.model.BaseModel;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageResult;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * 基础对接云平台接口层
 * @author wanggang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BaseOpenStackService extends BaseService {

    private final DevopsConfig devopsConfig;

    /**
     * 通过Get方式查询信息
     * @param api
     * @param valueType
     * @param <T>
     * @return
     */
    @Override
    protected <T extends BaseModel> T sendGet(String api, Class<T> valueType){
        return super.sendGet(devopsConfig.getCpUrl().concat(api),valueType);
    }

    /**
     * 通过Post方式查询信息
     * @param api
     * @param body
     * @param valueType
     * @param <T>
     * @return
     */
    @Override
    protected <T extends BaseModel> T sendPost(String api,String body,Class<T> valueType){
        return super.sendPost(devopsConfig.getCpUrl().concat(api),body,valueType);
    }

    /**
     * 通过Post方式查询信息
     * @param api
     * @param body
     * @return
     */
    @Override
    protected String sendPost(String api,String body){
        return super.sendPost(devopsConfig.getCpUrl().concat(api),body);
    }

    /**
     * 通过PUT方式查询信息
     * @param api
     * @param body
     * @param valueType
     * @param <T>
     * @return
     */
    @Override
    protected <T extends BaseModel> T sendPut(String api,String body,Class<T> valueType){
        return super.sendPut(devopsConfig.getCpUrl().concat(api),body,valueType);
    }

    /**
     * 通过PUT方式查询信息
     * @param api
     * @param body
     * @return
     */
    @Override
    protected String sendPut(String api,String body){
        return super.sendPut(devopsConfig.getCpUrl().concat(api),body);
    }

    /**
     * 通过PUT方式查询信息
     * @param api
     * @return
     */
    @Override
    protected HttpResponse downLoad(String api){
        return super.downLoad(devopsConfig.getCpUrl().concat(api));
    }

    /**
     * 通过Delete方式删除数据
     * @param api
     * @param body
     * @return
     */
    @Override
    protected <T extends BaseModel> T sendDelete(String api, String body,Class<T> valueType){
        return super.sendDelete(devopsConfig.getCpUrl().concat(api),body, valueType);
    }

    /**
     * 上传文件
     * @param api
     * @param httpEntity
     * @return
     */
    @Override
    protected <T extends BaseModel> T sendFormDataPost(String api, HttpEntity httpEntity ,Class<T> valueType) throws IOException {
        return super.sendFormDataPost(devopsConfig.getCpUrl().concat(api),httpEntity,valueType);
    }

    /**
     * 上传文件
     * @param api
     * @param httpEntity
     * @return
     */
    @Override
    protected <T extends BaseModel> T sendFormDataPut(String api, HttpEntity httpEntity ,Class<T> valueType) throws IOException {
        return super.sendFormDataPut(devopsConfig.getCpUrl().concat(api),httpEntity,valueType);
    }

    /**
     * 通用分页接口
     * @param url 请求URL 例如：/abc/page?name=test&type=1
     * @param tClass 数据转换类
     * @param <T>
     * @return
     */
    protected <T> CloudPlatformPageResult<T> page(String url, Class<T> tClass) {
        cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.CloudPlatformPageResult pageResult = this.sendGet(url, cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.CloudPlatformPageResult.class);
        this.checkResult(pageResult);
        CloudPlatformPageData data = pageResult.getData();
        List<T> list = JSONObject.parseArray(JSONObject.toJSONString(data.getList()),tClass);
        CloudPlatformPageResult<T> model = new CloudPlatformPageResult<>();
        model.setList(CollectionUtils.isEmpty(list) ? Lists.newArrayList() : list);
        model.setTotal(data.getTotal());
        model.setCode(pageResult.getCode());
        return model;
    }

    /**
     * 检查请求云平台接口
     * @param t
     * @param <T>
     */
    protected <T extends BaseCloudPlatformResult> void checkResult(T t){
        if (Objects.isNull(t) || (!BaseCloudPlatformConstant.CODE.equals(t.getCode()) && !t.isSuccess())){
            throw new MpRuntimeException(DevopsClusterErrorCode.REQUEST_CLOUD_INTERFACE_ERROR);
        }
    }
}
