package cn.cyberpeace.mp.auth.request.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description 封装文件验证参数
 * @Author shgang
 * @Date 2022/10/27
 */
@Data
@NoArgsConstructor
public class FileCheckRequest {
    private Long maxSize;
    private List<String> suffixList;
    private List<String> magicHeaderList;
}
