package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.AvatarModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.util.fdfsclient.FileResponseData;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
public interface AvatarService extends BaseModelService<AvatarModel, QueryParams> {

    List<AvatarModel> listAll();

    boolean deleteAll();

    FileResponseData upload(MultipartFile file);

    String uploadByPath(String filePath);

    String uploadIs(InputStream is, String fileName, Map<String, String> desc);

    boolean initDefaultAvatars();

}
