package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneBO;
import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import cn.cyberpeace.mp.cscene.controller.request.MibInfoQueryRequest;
import cn.cyberpeace.mp.cscene.core.CsceneHandler;
import cn.cyberpeace.mp.cscene.core.CsceneInstanceHandler;
import cn.cyberpeace.mp.cscene.dto.protocolcollection.MibFileInfo;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.SnmpConfig;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginContext;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.EntityNode;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.EntityNodeMibFileResult;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.EntityNodeMibUploadData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.MibNodeQueryParam;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.MibResultData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.EntityNodeMibUploadParam;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

/**
 * @BelongsPackage: cn.cyberpeace.mp.cscene.service
 * @Author: hhuang
 * @CreateTime: 2023/05/10  09:28
 * @Description: 实装协议采集mib处理
 * @Version: 1.0
 */
@Slf4j
@Service
public class ProtocolCollectionService {

    @Resource
    private BaseAppConfig baseAppConfig;

    @Resource
    private CsceneInstanceService csceneInstanceService;


    /**
     * @description: 上传并处理mib文件
     * @param multipartFile
     * @return ServiceResult
     */
    public ServiceResult<List<MibFileInfo>> importMibFile(MultipartFile multipartFile) {
        EntityNode entityNode = new EntityNode();
        String fileName = multipartFile.getOriginalFilename();
        //mib文件临时路径
        String mibFilePath = StringUtils.joinWith(FileUtil.FILE_SEPARATOR, baseAppConfig.getResourcesDir(), "mibfile/",fileName);
        File file = new File(mibFilePath);
        List<MibFileInfo> tree = new ArrayList<>();
        try {
            //写入文件
            FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), file);
            //请求云平台并获取解析结果
            CloudResponse<List<EntityNodeMibFileResult>> cloudResponse =
                entityNode.uploadAndParseMibFile(new EntityNodeMibUploadData(file),
                    new EntityNodeMibUploadParam(fileName));
            //获取解析结果
            List<EntityNodeMibFileResult> entityNodeMibFileResults = cloudResponse.getData();
            String responseMessage = cloudResponse.getMessage();
            if(StringUtils.isNotEmpty(responseMessage) && CollectionUtils.isEmpty(entityNodeMibFileResults)){
                log.warn("import mib file and parse have something wrong!  {}", responseMessage);
                return new ServiceResult<>(null, BaseErrorCode.ERROR, responseMessage);
            }
            //转换为树形结构
            tree = toTree(entityNodeMibFileResults);
            //排序
            sortTree(tree);
        } catch (Exception e) {
            log.error("import mib file process failed ", e);
            return new ServiceResult<>(null, BaseErrorCode.ERROR, e.getMessage());
        }finally {
            FileUtils.deleteQuietly(file);
        }
        return new ServiceResult<>(tree).asResult();
    }

    /**
     * @description: 转换为树形结构
     * @param entityNodeMibFileResults
     * @return List<MibFileInfo>
     */
    private List<MibFileInfo> toTree(List<EntityNodeMibFileResult> entityNodeMibFileResults) {
        Map<String, MibFileInfo> mapTmp = new HashMap<>();

        //保存oid和MibFileInfo的关系
        for (EntityNodeMibFileResult mb : entityNodeMibFileResults) {
            MibFileInfo targetDto = new MibFileInfo();
            BeanUtils.copyProperties(mb, targetDto);
            //oid MibFileInfo
            mapTmp.put(targetDto.getOid(), targetDto);
        }

        //处理所有mib节点的父子关系
        for (Map.Entry<String, MibFileInfo> entry : mapTmp.entrySet()) {
            MibFileInfo current = entry.getValue();
            String parentId = current.getParent();
            MibFileInfo parent = mapTmp.get(parentId);
            if (!Objects.isNull(parent)) {
                if (parent.getChildren() == null) {
                    parent.setChildren(new ArrayList<>());
                }
                parent.getChildren().add(current);
                mapTmp.put(parentId, parent);
            }
        }
        List<MibFileInfo> parentMibNode = new ArrayList<>();
        //获取根节点信息
        for (MibFileInfo mf : mapTmp.values()) {
            String parentId = mf.getParent();
            if (StringUtils.isEmpty(parentId)) {
                parentMibNode.add(mf);
            } else if (!mapTmp.containsKey(parentId)) {
                // todo 等底层保证没有这种不存在的parentId的时候,可以去掉
                parentMibNode.add(mf);
            }
        }
        return parentMibNode;
    }

    /**
     * @description: 节点排序
     * @param mibs
     * @return void
     */
    private void sortTree(List<MibFileInfo> mibs) {
        mibs.forEach(mib -> {
            if (CollectionUtils.isNotEmpty(mib.getChildren())) {
                sortTree(mib.getChildren());
            }
        });
        mibs.stream().sorted(Comparator.comparing(MibFileInfo::getName));
    }


    /**
     * @description: 查询mib节点信息
     * @param mibInfoQueryRequest
     * @return ServiceResult
     */
    public ServiceResult queryMibOidInfo(MibInfoQueryRequest mibInfoQueryRequest){
        EntityNode entityNode = new EntityNode();
        //请求参数构建（oid必传）
        var paramBuilder = MibNodeQueryParam.builder().oid(mibInfoQueryRequest.getOid());
        //综合场景id获取场景id
        String sceneId = csceneInstanceService.getSceneId(mibInfoQueryRequest.getSceneId());
        //获取场景处理类
        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        //获取云平台场景处理类
        CloudScenePluginHandler cloudPluginHandler = sceneHandler.getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
        //获取节点和云平台插件场景节点信息的关系
        Map<String, CloudPluginSceneElementBO> elementMap = cloudPluginHandler.getElementMap();
        //获取节点信息
        CloudPluginSceneElementBO cloudPluginSceneElementBO = elementMap.get(mibInfoQueryRequest.getElementId());
        //获取snmp配置信息
        SnmpConfig snmpConfig = cloudPluginSceneElementBO.getSnmpConfig();
        // 优先获取端口映射的IP
        if (StringUtils.isNotEmpty(snmpConfig.getSceneIp())) {
            paramBuilder = paramBuilder.ip(snmpConfig.getSceneIp()).port(snmpConfig.getScenePort());
        } else {
            //只传递此ip，无需获取生成的ip和端口
            paramBuilder = paramBuilder.ip(snmpConfig.getIp());
        }
        MibNodeQueryParam mibNodeQueryParam =
            paramBuilder.communityName(mibInfoQueryRequest.getCommunityName()).version(mibInfoQueryRequest.getVersion())
                .username(mibInfoQueryRequest.getUsername()).authKey(mibInfoQueryRequest.getAuthKey())
                .privKey(mibInfoQueryRequest.getPrivKey()).build();
        List<MibResultData> mibNodeInfo = entityNode.getMibNodeInfo(mibNodeQueryParam);

        return new ServiceResult<>(mibNodeInfo).asResult();
    }

}
