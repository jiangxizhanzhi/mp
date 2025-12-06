package cn.cyberpeace.mp.service;

import cn.cyberpeace.mp.base.util.http.HttpData;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VendorSecondClientContext {
    private String url;
    private String method;
    private HttpData body;
}
