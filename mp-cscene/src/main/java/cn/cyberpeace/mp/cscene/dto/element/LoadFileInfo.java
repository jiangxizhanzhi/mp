package cn.cyberpeace.mp.cscene.dto.element;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "文件上传返回信息")
public class LoadFileInfo {
    private String fileName;

    private String filePath;

    private String fileContent;
}
