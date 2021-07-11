package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.situationaware.model.ProbeScriptModel;
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


@Slf4j
@Service
public class ProbeScriptService {

    @Autowired
    private ProbeScriptMapper probeScriptMapper;


    public String getScriptChecksumByProbeId(String probeId) {
        return probeScriptMapper.getChecksumByProbeId(probeId);
    }

    public ProbeScriptModel getScriptModelByProbeId(String probeId) {
        return probeScriptMapper.getScriptModelByProbeId(probeId);
    }

    
    public String uploadScriptContentToFDFS(String scriptContent, Integer scriptType) {
        
        
        String suffix = "sh";
        
        if (Objects.nonNull(scriptType)) {
            if (scriptType == 0) {
                suffix = "sh";
            }

            if (scriptType == 1) {
                suffix = "py";
            }

            if (scriptType == 2) {
                suffix = "bat";
            }
        }

        InputStream is = new ByteArrayInputStream(scriptContent.getBytes(StandardCharsets.UTF_8));
        String filePath = FsCommon.upload(is, suffix);
        String httpUrl = FsCommon.genHttpUrl(filePath);
        return httpUrl;
    }


    
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
