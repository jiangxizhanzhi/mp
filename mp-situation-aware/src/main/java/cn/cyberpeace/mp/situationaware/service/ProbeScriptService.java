package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.config.FastDfsConfig;
import cn.cyberpeace.mp.base.enums.FdfsGroupEnum;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.fdfsclient.FastDFSClient;
import cn.cyberpeace.mp.situationaware.repository.ProbeScriptMapper;
import cn.cyberpeace.mp.situationaware.utils.FileUtils;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/4/27 18:19
 */
@Slf4j
@Service
public class ProbeScriptService {


    @Autowired
    private FastDfsConfig fastDfsConfig;

    @Autowired
    private FastDFSClient fastDFSClient;

    @Autowired
    private ProbeScriptMapper probeScriptMapper;


    public String getScriptChecksumByProbeId(String probeId) {
        return probeScriptMapper.getChecksumByProbeId(probeId);
    }

    /**
     * 将脚本内容上传FDFS，返回上传文件url
     * @param scriptContent
     * @return
     */
    public String uploadScriptContentToFDFS(String scriptContent, Integer scriptType) {
        // 处理探针脚本
        // todo 语法校验
        String suffix = "sh";
        //0:shell 1:python 2:bat
        if (Objects.nonNull(scriptType)) {
            if (scriptType == 0) {
                suffix = "sh";
            }

            if (scriptType == 0) {
                suffix = "py";
            }

            if (scriptType == 0) {
                suffix = "bat";
            }
        }

        InputStream is = new ByteArrayInputStream(scriptContent.getBytes(StandardCharsets.UTF_8));
        FastDfsConfig.Group group = fastDfsConfig.getGroupByName(FdfsGroupEnum.DEFAULT);
        String filePath = FsCommon.upload(is, suffix);
        String httpUrl = group.getFileServerAddr() + File.separator + filePath;
        return httpUrl;
    }


    /**
     * 导入脚本文件，返回脚本内容
     * @return 脚本内容
     */
    public String readScriptFileContent(MultipartFile scriptFile) {
        if (Objects.isNull(scriptFile)) {
            return "";
        }
        File file = null;
        String content = "";
        try {
            file = FileUtils.multipartToFile(scriptFile, scriptFile.getOriginalFilename());
            content = FileUtil.readUtf8String(file);
        } catch (IOException e) {
            log.error("error:{}", ExceptionUtil.stacktraceToString(e));
            throw new MpRuntimeException("parse script file error!");
        }
        file.deleteOnExit();
        return content;
    }

}
