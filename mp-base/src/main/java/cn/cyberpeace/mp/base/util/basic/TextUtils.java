package cn.cyberpeace.mp.base.util.basic;

import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class TextUtils {
    private static final Pattern linePattern = Pattern.compile("_([a-z])");

    private static final Pattern humpPattern = Pattern.compile("\\B(\\p{Upper})(\\p{Lower}*)");

    private static final Pattern chinesePattern = Pattern.compile("[\u4E00-\u9FA5|\\！|\\，|\\。|\\（|\\）|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】]");

    private static final Base64.Decoder base64Decoder = Base64.getDecoder();

    private static final Base64.Encoder base64Encoder = Base64.getEncoder();

    private TextUtils() {}

    public static String emptyNull(String str) {
        return StringUtils.isEmpty(str) ? null : str;
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", StringUtils.EMPTY).toLowerCase();
    }

    public static String lineToHump(String str) {
        if (str == null) {
            return null;
        }

        var sb = new StringBuilder();
        var matcher = linePattern.matcher(str);
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String[] lineToHump(String[] strs) {
        if (strs == null) {
            return new String[0];
        }

        var result = new String[strs.length];
        for (var i = 0; i < strs.length; i++) {
            result[i] = lineToHump(strs[i]);
        }
        return result;
    }

    public static String humpToLine(String str) {
        if (str == null) {
            return null;
        }

        var sb = new StringBuilder();
        var matcher = humpPattern.matcher(str);
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String[] humpToLine(String[] strs) {
        if (strs == null) {
            return new String[0];
        }

        var result = new String[strs.length];
        for (var i = 0; i < strs.length; i++) {
            result[i] = humpToLine(strs[i]);
        }
        return result;
    }

    public static boolean containChinese(String str) {
        return chinesePattern.matcher(str).find();
    }

    public static byte[] rawEncodeBase64(byte[] bytes) {
        return base64Encoder.encode(bytes);
    }

    public static byte[] rawEncodeBase64(String str) {
        return base64Encoder.encode(str.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] rawDecodeBase64(byte[] bytes) {
        return base64Decoder.decode(bytes);
    }
    public static byte[] rawDecodeBase64(String str) {
        return base64Decoder.decode(str);
    }

    public static String encodeBase64(byte[] bytes) {
        return base64Encoder.encodeToString(bytes);
    }

    public static String encodeBase64(String str) {
        return base64Encoder.encodeToString(str.getBytes(StandardCharsets.UTF_8));
    }

    public static String decodeBase64(String str) {
        return new String(rawDecodeBase64(str), StandardCharsets.UTF_8);
    }

    public static String decodeBase64(byte[] bytes) {
        return new String(rawDecodeBase64(bytes), StandardCharsets.UTF_8);
    }

    public static byte[] rawCompress(byte[] bytesToCompress) {
        var deflater = new Deflater();
        deflater.setInput(bytesToCompress);
        deflater.finish();

        var bytesCompressed = new byte[Short.MAX_VALUE];
        int numberOfBytesAfterCompression = deflater.deflate(bytesCompressed);
        var returnValues = new byte[numberOfBytesAfterCompression];
        System.arraycopy(bytesCompressed, 0, returnValues, 0, numberOfBytesAfterCompression);

        return returnValues;
    }

    public static byte[] rawDecompress(byte[] bytesToDecompress) {
        byte[] returnValues = null;

        var inflater = new Inflater();
        int numberOfBytesToDecompress = bytesToDecompress.length;
        inflater.setInput(bytesToDecompress, 0, numberOfBytesToDecompress);
        int bufferSizeInBytes = numberOfBytesToDecompress;
        int numberOfBytesDecompressedSoFar = 0;
        List<Byte> bytesDecompressedSoFar = new ArrayList<>();
        try {
            while (!inflater.needsInput()) {
                var bytesDecompressedBuffer = new byte[bufferSizeInBytes];
                int numberOfBytesDecompressedThisTime = inflater.inflate(bytesDecompressedBuffer);
                numberOfBytesDecompressedSoFar += numberOfBytesDecompressedThisTime;
                for (var b = 0; b < numberOfBytesDecompressedThisTime; b++) {
                    bytesDecompressedSoFar.add(bytesDecompressedBuffer[b]);
                }
            }

            returnValues = new byte[bytesDecompressedSoFar.size()];
            for (var b = 0; b < returnValues.length; b++) {
                returnValues[b] = (byte)(bytesDecompressedSoFar.get(b));
            }
        } catch (DataFormatException dfe) {
            dfe.printStackTrace();
        }
        inflater.end();

        return returnValues;
    }

    public static byte[] compress(String str) {
        return rawCompress(str.getBytes(StandardCharsets.UTF_8));
    }

    public static String decompress(byte[] bytesToDecompress) {
        byte[] bytesDecompressed = rawDecompress(bytesToDecompress);
        return new String(bytesDecompressed, StandardCharsets.UTF_8);
    }

}

