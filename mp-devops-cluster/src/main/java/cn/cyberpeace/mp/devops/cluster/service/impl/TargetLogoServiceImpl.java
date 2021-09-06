package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.base.config.FastDfsConfig;
import cn.cyberpeace.mp.base.enums.FdfsGroupEnum;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.fdfsclient.FastDFSClient;
import cn.cyberpeace.mp.devops.cluster.model.TargetLogoModel;
import cn.cyberpeace.mp.devops.cluster.repository.TargetLogoMapper;
import cn.cyberpeace.mp.devops.cluster.service.TargetLogoService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    public List<String> upload() {
        int total =  targetLogoMapper.total(new HumanCondition());
        if (total == 26 ){
            log.info(" icons has been initialized ");
            return Lists.newArrayList();
        }
        List<String> urlList = new ArrayList<>();
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver()
                    .getResources(ResourceUtils.CLASSPATH_URL_PREFIX + "target.logo/*.png");
            FastDfsConfig.Group group = fastDfsConfig.getGroupByName(FdfsGroupEnum.DEFAULT);
            for (Resource resource : resources) {
                String filepath = fastDFSClient.upload(resource.getInputStream(), resource.getFilename(), null,
                        FdfsGroupEnum.DEFAULT.toString());
                String logoUrl = group.getFileServerAddr() + File.separator + filepath;
                urlList.add(logoUrl);
            }
           return urlList;
        } catch (IOException e) {
            log.info("logo init failed,message is {}",e.getMessage());
        }
        finally {
            return urlList;
        }
    }

    @Override
    public String uploadLogo(MultipartFile file) {
        if (Objects.isNull(file) || file.getSize() == 0 ){
            return null;
        }
        FastDfsConfig.Group group = fastDfsConfig.getGroupByName(FdfsGroupEnum.DEFAULT);
        String logoUrl = "";
        try {
            String filepath = fastDFSClient.upload(file.getInputStream(), file.getOriginalFilename(), null,
                    FdfsGroupEnum.DEFAULT.toString());
            logoUrl = group.getFileServerAddr() + File.separator + filepath;
        } catch (IOException e) {
            log.error("上传图标失败：错误信息：{}",e.getMessage());
        }
        return logoUrl;
    }

    @Transactional
    public int batchAdd(List<String> urlList){
        if (CollectionUtils.isEmpty(urlList)){
            return 0;
        }
        List<TargetLogoModel> modelList = urlList.stream().map(url -> new TargetLogoModel(url)).collect(Collectors.toList());
        return targetLogoMapper.bulkCreate(modelList);
    }

    @Override
    public List<String> list() {
        List<TargetLogoModel> list = targetLogoMapper.list(new HumanCondition());
        return list.stream().map(TargetLogoModel::getUrl).collect(Collectors.toList());
    }
}
