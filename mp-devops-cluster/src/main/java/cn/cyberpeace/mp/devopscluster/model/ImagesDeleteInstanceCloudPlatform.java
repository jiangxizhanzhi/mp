package cn.cyberpeace.mp.devopscluster.model;

import cn.cyberpeace.mp.devopscluster.request.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImagesDeleteInstanceCloudPlatform extends BaseRequest {


    
    private String deviceUuid;

    
    private String imageType;

}
