package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.controller.request.MibInfoQueryRequest;
import cn.cyberpeace.mp.cscene.dto.protocolcollection.MibFileInfo;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.EntityNode;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.EntityNodeMibFileResult;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.EntityNodeMibUploadData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.MibNodeQueryParam;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.MibResultData;
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


    /**
     * @description: 上传并处理mib文件
     * @param multipartFile
     * @return ServiceResult
     */
    public ServiceResult importMibFile(MultipartFile multipartFile) {
        EntityNode entityNode = new EntityNode();
        //mib文件临时路径
        String mibFilePath = StringUtils.joinWith(FileUtil.FILE_SEPARATOR, baseAppConfig.getResourcesDir(), "mibfile/tmp.rules");
        File file = new File(mibFilePath);
        String result = "";
        try {
            //写入文件
            FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), file);
            //请求云平台并获取解析结果
            List<EntityNodeMibFileResult> entityNodeMibFileResults =
                entityNode.uploadAndParseMibFile(new EntityNodeMibUploadData(file));
            //转换为树形结构
            List<MibFileInfo> tree = toTree(entityNodeMibFileResults);
            //排序
            sortTree(tree);
        } catch (Exception e) {
            log.error("import mib file process failed ", e);
            return new ServiceResult<>(0, BaseErrorCode.ERROR, e.getMessage());
        }finally {
            FileUtils.deleteQuietly(file);
        }
        return new ServiceResult<>(result).asResult();
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
            mapTmp.put(targetDto.getValue(), targetDto);
        }

        //处理所有mib节点的父子关系
        for (Map.Entry<String, MibFileInfo> entry : mapTmp.entrySet()) {
            MibFileInfo current = entry.getValue();
            String parentId = current.getParent();
            MibFileInfo parent = mapTmp.get(parentId);
            current.setParentInfo(parent);
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
            MibFileInfo parent = mapTmp.get(parentId);
            if(Objects.isNull(parent)) {
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
        List<MibResultData> mibNodeInfo = entityNode.getMibNodeInfo(
            new MibNodeQueryParam(mibInfoQueryRequest.getOid(), mibInfoQueryRequest.getVersion(),
                mibInfoQueryRequest.getCommunity(), mibInfoQueryRequest.getIp(), mibInfoQueryRequest.getUsername(),
                mibInfoQueryRequest.getAuthKey(), mibInfoQueryRequest.getPrivKey()));
        return new ServiceResult<>(mibNodeInfo).asResult();
    }

}
