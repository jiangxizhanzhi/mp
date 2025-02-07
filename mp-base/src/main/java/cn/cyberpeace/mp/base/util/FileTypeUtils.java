package cn.cyberpeace.mp.base.util;

public class FileTypeUtils {
    public static boolean isPng(byte[] content) {
        if (content == null || content.length < 8) {
            return false;
        }
        int[] pngHeaders = new int[]{0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a};
        for (int i = 0; i < pngHeaders.length; i++) {
            if (Byte.toUnsignedInt(content[i]) != pngHeaders[i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean isSvg(byte[] content) {
        if (content == null || content.length < 4) {
            return false;
        }
        byte[] header = new byte[4];
        for (int i = 0; i < 4; i++) {
            header[i] = content[i];
        }
        return new String(header).equals("<svg") || new String(header).equals("<?xm");
    }
}
