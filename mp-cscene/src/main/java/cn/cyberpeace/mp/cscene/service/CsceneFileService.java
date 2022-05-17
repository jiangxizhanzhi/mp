package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.common.FdfsCommon;
import cn.cyberpeace.mp.base.service.impl.BaseRemoteFileServiceImpl;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.constant.CommonErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * @Description
 * @Author maojun
 * @Date 2022/5/16
 */
@Service
public class CsceneFileService extends BaseRemoteFileServiceImpl {
    private static Set<String> LEGAL_ATTACHMENT_TYPE = new HashSet<>();

    static {
        LEGAL_ATTACHMENT_TYPE.add("zip");
    }

    @Autowired
    public CsceneFileService(FdfsCommon fdfsCommon) {
        setFileClient(FdfsCommon.fdfsClient);
    }

    public ServiceResult<String> uploadAttachment(InputStream is, String fileSuffix) {
        if (!LEGAL_ATTACHMENT_TYPE.contains(fileSuffix)) {
            return new ServiceResult<>(null, CommonErrorCode.UPLOAD_FILE_TYPE_ERROR);
        }
        return super.upload(is);
    }
}
