package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.AvatarModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.util.fdfsclient.FileResponseData;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AvatarService extends BaseModelService<AvatarModel, QueryParams> {

    List<AvatarModel> listAll();

    boolean deleteAll();

    FileResponseData upload(MultipartFile file);

    String uploadByPath(String filePath);

}
