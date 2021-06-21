package cn.cyberpeace.mp.datasandtable.model;

import java.time.LocalDateTime;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
public class FileEventModel {

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 变更枚举
     */
    private int changes;

    //文件大小
    private int size;

    //文件的时间
    private LocalDateTime localDateTime;
}
