package cn.cyberpeace.mp.auth.request.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description
 * @Author shgang
 * @Date 2022/11/3
 */
@Data
@NoArgsConstructor
public class FileCheckRequest {
    private Long maxSize;
    private List<String> suffixList;
    private List<String> magicHeaderList;
}
