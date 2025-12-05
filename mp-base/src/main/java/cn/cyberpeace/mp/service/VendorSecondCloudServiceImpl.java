package cn.cyberpeace.mp.service;

import cn.cyberpeace.mp.base.util.http.HttpClient;

import java.util.List;
import java.util.Map;

public class VendorSecondCloudServiceImpl implements MultiCloudService {
    @Override
    public Map<String, String> getHeaders() {
        return Map.of();
    }

    @Override
    public HttpClient getClient() {
        return null;
    }

    @Override
    public List<String> listAllDevices() {
        return List.of();
    }

    @Override
    public int getVendor() {
        return 0;
    }

    @Override
    public String createVm(String imageUuid) {
        return "";
    }

    @Override
    public String[] getVmIpAndMac(String vmUuid) {
        return new String[0];
    }

    @Override
    public void deleteVm(String vmUuid) {

    }

    @Override
    public String getConnection(String vmUuid) {
        return "";
    }
}
