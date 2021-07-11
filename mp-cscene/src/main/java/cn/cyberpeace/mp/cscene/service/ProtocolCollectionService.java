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


@Slf4j
@Service
public class ProtocolCollectionService {

    @Resource
    private BaseAppConfig baseAppConfig;

    @Resource
    private CsceneInstanceService csceneInstanceService;


    
    public ServiceResult<List<MibFileInfo>> importMibFile(MultipartFile multipartFile) {
        EntityNode entityNode = new EntityNode();
        String fileName = multipartFile.getOriginalFilename();
        
        String mibFilePath = StringUtils.joinWith(FileUtil.FILE_SEPARATOR, baseAppConfig.getResourcesDir(), "mibfile/",fileName);
        File file = new File(mibFilePath);
        List<MibFileInfo> tree = new ArrayList<>();
        try {
            
            FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), file);
            
            CloudResponse<List<EntityNodeMibFileResult>> cloudResponse =
                entityNode.uploadAndParseMibFile(new EntityNodeMibUploadData(file),
                    new EntityNodeMibUploadParam(fileName));
            
            List<EntityNodeMibFileResult> entityNodeMibFileResults = cloudResponse.getData();
            String responseMessage = cloudResponse.getMessage();
            if(StringUtils.isNotEmpty(responseMessage) && CollectionUtils.isEmpty(entityNodeMibFileResults)){
                log.warn("import mib file and parse have something wrong!  {}", responseMessage);
                return new ServiceResult<>(null, BaseErrorCode.ERROR, responseMessage);
            }
            
            tree = toTree(entityNodeMibFileResults);
            
            sortTree(tree);
        } catch (Exception e) {
            log.error("import mib file process failed ", e);
            return new ServiceResult<>(null, BaseErrorCode.ERROR, e.getMessage());
        }finally {
            FileUtils.deleteQuietly(file);
        }
        return new ServiceResult<>(tree).asResult();
    }

    
    private List<MibFileInfo> toTree(List<EntityNodeMibFileResult> entityNodeMibFileResults) {
        Map<String, MibFileInfo> mapTmp = new HashMap<>();

        
        for (EntityNodeMibFileResult mb : entityNodeMibFileResults) {
            MibFileInfo targetDto = new MibFileInfo();
            BeanUtils.copyProperties(mb, targetDto);
            
            mapTmp.put(targetDto.getOid(), targetDto);
        }

        
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
        
        for (MibFileInfo mf : mapTmp.values()) {
            String parentId = mf.getParent();
            if (StringUtils.isEmpty(parentId)) {
                parentMibNode.add(mf);
            } else if (!mapTmp.containsKey(parentId)) {
                
                parentMibNode.add(mf);
            }
        }
        return parentMibNode;
    }

    
    private void sortTree(List<MibFileInfo> mibs) {
        mibs.forEach(mib -> {
            if (CollectionUtils.isNotEmpty(mib.getChildren())) {
                sortTree(mib.getChildren());
            }
        });
        mibs.stream().sorted(Comparator.comparing(MibFileInfo::getName));
    }


    
    public ServiceResult queryMibOidInfo(MibInfoQueryRequest mibInfoQueryRequest){
        EntityNode entityNode = new EntityNode();
        
        var paramBuilder = MibNodeQueryParam.builder().oid(mibInfoQueryRequest.getOid());
        
        String sceneId = csceneInstanceService.getSceneId(mibInfoQueryRequest.getSceneId());
        
        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        
        CloudScenePluginHandler cloudPluginHandler = sceneHandler.getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
        
        Map<String, CloudPluginSceneElementBO> elementMap = cloudPluginHandler.getElementMap();
        
        CloudPluginSceneElementBO cloudPluginSceneElementBO = elementMap.get(mibInfoQueryRequest.getElementId());
        
        SnmpConfig snmpConfig = cloudPluginSceneElementBO.getSnmpConfig();
        
        if (StringUtils.isNotEmpty(snmpConfig.getSceneIp())) {
            paramBuilder = paramBuilder.ip(snmpConfig.getSceneIp()).port(snmpConfig.getScenePort());
        } else {
            
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
