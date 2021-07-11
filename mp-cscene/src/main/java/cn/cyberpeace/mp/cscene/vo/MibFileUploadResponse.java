package cn.cyberpeace.mp.cscene.vo;

import cn.cyberpeace.mp.cscene.dto.protocolcollection.MibFileInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class MibFileUploadResponse {
    private SceneFileUploadResponse fileInfo;
    private List<MibFileInfo> mib;
}
