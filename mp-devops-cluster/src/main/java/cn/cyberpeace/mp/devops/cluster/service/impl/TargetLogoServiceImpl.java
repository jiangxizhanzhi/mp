package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.base.config.FastDfsConfig;
import cn.cyberpeace.mp.base.enums.FdfsGroupEnum;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.fdfsclient.FastDFSClient;
import cn.cyberpeace.mp.devops.cluster.model.TargetLogoModel;
import cn.cyberpeace.mp.devops.cluster.repository.TargetLogoMapper;
import cn.cyberpeace.mp.devops.cluster.service.TargetLogoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TargetLogoServiceImpl implements TargetLogoService {

    private FastDFSClient fastDFSClient;

    private FastDfsConfig fastDfsConfig;

    private TargetLogoMapper targetLogoMapper;

    @Autowired
    public TargetLogoServiceImpl(FastDFSClient fastDFSClient,FastDfsConfig fastDfsConfig,
                                 TargetLogoMapper targetLogoMapper) {
        this.fastDFSClient = fastDFSClient;
        this.fastDfsConfig = fastDfsConfig;
        this.targetLogoMapper = targetLogoMapper;
    }

    @Override
    public int init() {
        int total =  targetLogoMapper.total(new HumanCondition());
        if (total == 26 ){
            log.info(" icons has been initialized ");
            return 26;
        }
        List<TargetLogoModel> urlList = new ArrayList<>();
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver()
                    .getResources(ResourceUtils.CLASSPATH_URL_PREFIX + "target.logo/*.png");
            for (Resource resource : resources) {
                // 获取当前集群配置
                FastDfsConfig.Group group = fastDfsConfig.getGroupByName(FdfsGroupEnum.DEFAULT);
                String filepath = fastDFSClient.upload(resource.getInputStream(), resource.getFilename(), null,
                        FdfsGroupEnum.DEFAULT.toString());
                String logoUrl = group.getFileServerAddr() + File.separator + filepath;
                TargetLogoModel targetLogoModel = new TargetLogoModel();
                targetLogoModel.setUrl(logoUrl);
                urlList.add(targetLogoModel);
            }
           return targetLogoMapper.bulkCreate(urlList);
        } catch (IOException e) {
            log.info("logo init failed,message is {}",e.getMessage());
        }
        return 0;
    }

    @Override
    public List<String> list() {
        List<TargetLogoModel> list = targetLogoMapper.list(new HumanCondition());
        return list.stream().map(TargetLogoModel::getUrl).collect(Collectors.toList());
    }
}
