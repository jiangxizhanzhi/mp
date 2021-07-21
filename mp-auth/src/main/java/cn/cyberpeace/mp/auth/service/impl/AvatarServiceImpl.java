package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.model.AvatarModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.AvatarMapper;
import cn.cyberpeace.mp.auth.service.AvatarService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.service.fields.AvatarFields;
import cn.cyberpeace.mp.base.config.FastDfsConfig;
import cn.cyberpeace.mp.base.enums.FdfsGroupEnum;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.util.fdfsclient.FastDFSClient;
import cn.cyberpeace.mp.base.util.fdfsclient.FileResponseData;
import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    @Autowired
    private UserService userService;

    private static final String DEFAULT_USER_ID = "3f655d87112f4ae094bbaf70830e20aa";

    @Override
    public List<AvatarModel> listAll() {
        return getModelMapper().listAll();
    }

    @Override
    public boolean deleteAll() {
        getModelMapper().deleteAll();
        return true;
    }

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
            responseData.setCode(e.getErrorCode());
            responseData.setMessage(e.getMessage());
        }

        return responseData;
    }


    @Override
    public String uploadByPath(String filePath) {
        String httpUrl = "";
        try {
            // 上传到服务器
            String filepath = fastDFSClient.uploadFileWithFilepath(filePath, FdfsGroupEnum.DEFAULT.toString());

            // 获取当前集群配置
            FastDfsConfig.Group group = fastDfsConfig.getGroupByName(FdfsGroupEnum.DEFAULT);
//
            httpUrl = group.getFileServerAddr() + File.separator + filepath;
            return httpUrl;
        } catch (MpRuntimeException e) {
            log.error(ExceptionUtil.stacktraceToString(e));
            throw e;
        }
    }

    @Override
    public String uploadIs(InputStream is, String fileName, Map<String, String> desc) {

        String httpUrl = "";
        // 获取当前集群配置
        FastDfsConfig.Group group = fastDfsConfig.getGroupByName(FdfsGroupEnum.DEFAULT);

        String filepath = fastDFSClient.upload(is, fileName, desc, FdfsGroupEnum.DEFAULT.toString());
        httpUrl = group.getFileServerAddr() + File.separator + filepath;
        return httpUrl;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean initDefaultAvatars() {
        log.info("uploading default avatar manually!");
        deleteAll();

        // 默认头像
        InputStream avatar01 = Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("avatar/1.png"));
        InputStream avatar02 = Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("avatar/2.png"));
        InputStream avatar03 = Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("avatar/3.png"));
        InputStream avatar04 = Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("avatar/4.png"));
        InputStream avatar05 = Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("avatar/5.png"));
        InputStream avatar06 = Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("avatar/6.png"));
        InputStream avatar07 = Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("avatar/7.png"));
        InputStream avatar08 = Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("avatar/8.png"));
        InputStream avatar09 = Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("avatar/9.png"));


        List<InputStream> avatarlist = Arrays.asList(avatar01, avatar02, avatar03, avatar04, avatar05, avatar06, avatar07, avatar08, avatar09);

        for (int i = 0; i < avatarlist.size(); i++) {
            InputStream ava = avatarlist.get(i);
            String url = uploadIs(ava, i + ".png", null);
            AvatarModel avatarModel = new AvatarModel();
            avatarModel.setUrl(url);
            create(avatarModel);

            if(i == 0) {
                updateDefaultUserAvatar(url);
            }
        }

        return true;
    }

    // 更新默认用户的头像
    private void updateDefaultUserAvatar(String avatarUrl) {
        ObjServiceResult<UserModel> result = userService.get(DEFAULT_USER_ID);
        if (Objects.nonNull(result.getSource())) {
            UserModel old = result.getSource().getObj();
            if (StringUtils.isBlank(old.getAvatar())) {
                UserModel userUpdate = new UserModel();
                userUpdate.setId(DEFAULT_USER_ID);
                userUpdate.setAvatar(avatarUrl);
                userService.update(userUpdate);
            }
        }
    }
}
