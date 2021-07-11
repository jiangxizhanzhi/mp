package cn.cyberpeace.mp.sceneplugincloud.util;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;


public final class SnapshotAddressUtils {

    public static String buildAddress(String imageName, String snapshotUuid) {
        return StringUtils.joinWith(",", imageName, snapshotUuid);
    }

    public static SnapshotAddressInfo parseAddress(String address) {
        String[] infos = StringUtils.split(address, ",");
        SnapshotAddressInfo result = new SnapshotAddressInfo();
        if (infos != null) {
            if (infos.length >= 1) {
                result.setImageName(infos[0]);
            }
            if (infos.length >= 2) {
                result.setSnapshotUuid(infos[1]);
            }
        }
        return result;
    }

    public static String parseImageName(String imageName, String imageUuid) {
        if (StringUtils.startsWith(imageName, imageUuid)) {
            return StringUtils.replaceOnce(imageName, imageUuid, StringUtils.EMPTY);
        }
        return imageName;
    }

    @Getter
    @Setter
    public static class SnapshotAddressInfo {
        private String imageName;
        private String snapshotUuid;
    }
}
