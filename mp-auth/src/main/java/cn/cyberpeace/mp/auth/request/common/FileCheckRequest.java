package cn.cyberpeace.mp.auth.request.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
public class FileCheckRequest {
    private Long maxSize;
    private List<String> suffixList;
    private List<String> magicHeaderList;
}
