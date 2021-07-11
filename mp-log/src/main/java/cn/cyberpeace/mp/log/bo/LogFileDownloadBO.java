package cn.cyberpeace.mp.log.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.ByteArrayInputStream;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogFileDownloadBO {

    
    private ByteArrayInputStream inputStream;

    
    private String downLoadFileName;

    
    @Builder.Default
    private String contentType = "application/zip";
}
