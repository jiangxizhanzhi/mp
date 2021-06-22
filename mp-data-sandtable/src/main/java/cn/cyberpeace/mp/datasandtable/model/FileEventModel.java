package cn.cyberpeace.mp.datasandtable.model;

import cn.cyberpeace.mp.datasandtable.model.emum.FileEventType;
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

    //文件大小
    private String size;

    //文件的时间
    private LocalDateTime updateTime;

    private String user;

    /**
     * 文件事件类型
     */
    private String action;

    /**
     * 文件事件类型
     */
    private FileEventType fileEventType;
}
