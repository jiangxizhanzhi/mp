package cn.cyberpeace.mp.cscene.dto.flow;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FlowPersistenceFileDto {

    private String fileName;

    private String fileSize;

    private String modifyTime;
}
