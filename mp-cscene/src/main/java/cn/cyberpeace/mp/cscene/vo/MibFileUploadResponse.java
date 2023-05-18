package cn.cyberpeace.mp.cscene.vo;

import cn.cyberpeace.mp.cscene.dto.protocolcollection.MibFileInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/*
 * @BelongsPackage: cn.cyberpeace.mp.cscene.vo
 * @Author: hhuang
 * @CreateTime: 2023/5/18 15:16
 * @Description: mib上传给前端的响应
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MibFileUploadResponse {
    private SceneFileUploadResponse fileInfo;
    private List<MibFileInfo> mib;
}
