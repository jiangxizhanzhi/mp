package cn.cyberpeace.mp.service;

import cn.cyberpeace.mp.base.util.http.SnakeHttpData;
import lombok.Data;

@Data
public class VendorSecondVmDeleteHttpData implements SnakeHttpData {
    private int force = 1;
}
