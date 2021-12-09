package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.model.AvatarModel;
import cn.cyberpeace.mp.auth.repository.AvatarMapper;
import cn.cyberpeace.mp.auth.service.AvatarService;
import cn.cyberpeace.mp.auth.service.fields.AvatarFields;
import cn.cyberpeace.mp.base.config.FastDfsConfig;
import cn.cyberpeace.mp.base.enums.FdfsGroupEnum;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.util.fdfsclient.FastDFSClient;
import cn.cyberpeace.mp.base.util.fdfsclient.FileResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @Description: 头像管理service
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Service
@Slf4j
public class AvatarServiceImpl extends BaseModelServiceImpl<AvatarModel, AvatarMapper, AvatarFields, QueryParams> implements AvatarService {

    @Autowired
    public AvatarServiceImpl(AvatarMapper modelMapper, AvatarFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Autowired
    private FastDFSClient fastDFSClient;

    @Autowired
    private FastDfsConfig fastDfsConfig;

    public FileResponseData upload(MultipartFile file) {
        // todo 判断是否是图片

        FileResponseData responseData = new FileResponseData();
        try {
            // 上传到服务器
            String filepath = fastDFSClient.uploadFileWithMultipart(file, FdfsGroupEnum.DEFAULT.toString());

            // 获取当前集群配置
            FastDfsConfig.Group group = fastDfsConfig.getGroupByName(FdfsGroupEnum.DEFAULT);

            responseData.setFileName(file.getOriginalFilename());
            responseData.setFilePath(filepath);
            responseData.setFileType(FastDFSClient.getFilenameSuffix(file.getOriginalFilename()));
            responseData.setHttpUrl(group.getFileServerAddr() + File.separator + filepath);
            responseData.setInnerHttpUrl(group.getInnerFileServerAddr() + File.separator + filepath);
        } catch (MpRuntimeException e) {
            responseData.setSuccess(false);
//            responseData.setCode(e.getErrorCode());
            responseData.setMessage(e.getMessage());
        }

        return responseData;
    }

}
