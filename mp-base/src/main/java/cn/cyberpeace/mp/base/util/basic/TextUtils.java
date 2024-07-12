 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TextUtils.java.
 // File size 8399.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.util.basic;

import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * @ClassName TextUtils
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public class TextUtils {
    private static final Pattern linePattern = Pattern.compile("_([a-z])");

    private static final Pattern humpPattern = Pattern.compile("\\B(\\p{Upper})(\\p{Lower}*)");

    private static final Pattern chinesePattern = Pattern.compile("[\u4E00-\u9FA5|\\！|\\，|\\。|\\（|\\）|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】]");

    private static final Base64.Decoder base64Decoder = Base64.getDecoder();

    private static final Base64.Encoder base64Encoder = Base64.getEncoder();

    /**
     * TextUtils
     */
    private TextUtils() {}

    /**
     * emptyNull
     *
     * @param str
     * @return
     */
    public static String emptyNull(String str) {
        return StringUtils.isEmpty(str) ? null : str;
    }

    /**
     * uuid
     *
     * @return
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", StringUtils.EMPTY).toLowerCase();
    }

    /**
     * coupleStrip
     *
     * @param str
     * @param stripChars
     * @return
     */
    public static String coupleStrip(String str, final String stripChars) {
        String result;
        int strLength = str.length();
        int stripCharsLength = stripChars.length();
        if (strLength >= stripCharsLength * 2 && str.startsWith(stripChars) && str.endsWith(stripChars)) {
            result = str.substring(stripCharsLength, strLength - stripCharsLength);
        } else {
            result = str;
        }
        return result;
    }

    /**
     * lineToHump
     *
     * @param str
     * @return
     */
    public static String lineToHump(String str) {
        if (str == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        Matcher matcher = linePattern.matcher(str);
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * lineToHump
     *
     * @param strs
     * @return
     */
    public static String[] lineToHump(String[] strs) {
        if (strs == null) {
            return new String[0];
        }

        String[] result = new String[strs.length];
        for (int i = 0; i < strs.length; i++) {
            result[i] = lineToHump(strs[i]);
        }
        return result;
    }

    /**
     * humpToLine
     *
     * @param str
     * @return
     */
    public static String humpToLine(String str) {
        if (str == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        Matcher matcher = humpPattern.matcher(str);
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * humpToLine
     *
     * @param strs
     * @return
     */
    public static String[] humpToLine(String[] strs) {
        if (strs == null) {
            return new String[0];
        }

        String[] result = new String[strs.length];
        for (int i = 0; i < strs.length; i++) {
            result[i] = humpToLine(strs[i]);
        }
        return result;
    }

    /**
     * containChinese
     *
     * @param str
     * @return boolean
     */
    public static boolean containChinese(String str) {
        return chinesePattern.matcher(str).find();
    }

    /**
     * rawEncodeBase64
     *
     * @param bytes
     * @return
     */
    public static byte[] rawEncodeBase64(byte[] bytes) {
        return base64Encoder.encode(bytes);
    }

    /**
     * rawEncodeBase64
     *
     * @param str
     * @return
     */
    public static byte[] rawEncodeBase64(String str) {
        return base64Encoder.encode(str.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * rawDecodeBase64
     *
     * @param bytes
     * @return
     */
    public static byte[] rawDecodeBase64(byte[] bytes) {
        return base64Decoder.decode(bytes);
    }

    /**
     * rawDecodeBase64
     *
     * @param str
     * @return
     */
    public static byte[] rawDecodeBase64(String str) {
        return base64Decoder.decode(str);
    }

    /**
     * encodeBase64
     *
     * @param bytes
     * @return
     */
    public static String encodeBase64(byte[] bytes) {
        return base64Encoder.encodeToString(bytes);
    }

    /**
     * encodeBase64
     *
     * @param str
     * @return
     */
    public static String encodeBase64(String str) {
        return base64Encoder.encodeToString(str.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * decodeBase64
     *
     * @param str
     * @return
     */
    public static String decodeBase64(String str) {
        return new String(rawDecodeBase64(str), StandardCharsets.UTF_8);
    }

    /**
     * decodeBase64
     *
     * @param bytes
     * @return
     */
    public static String decodeBase64(byte[] bytes) {
        return new String(rawDecodeBase64(bytes), StandardCharsets.UTF_8);
    }

    /**
     * rawCompress
     *
     * @param bytesToCompress
     * @return
     */
    public static byte[] rawCompress(byte[] bytesToCompress) {
        Deflater deflater = new Deflater();
        deflater.setInput(bytesToCompress);
        deflater.finish();

        byte[] bytesCompressed = new byte[Short.MAX_VALUE];
        int numberOfBytesAfterCompression = deflater.deflate(bytesCompressed);
        byte[] returnValues = new byte[numberOfBytesAfterCompression];
        System.arraycopy(bytesCompressed, 0, returnValues, 0, numberOfBytesAfterCompression);

        return returnValues;
    }

    /**
     * rawDecompress
     *
     * @param bytesToDecompress
     * @return
     */
    public static byte[] rawDecompress(byte[] bytesToDecompress) {
        byte[] returnValues = null;

        Inflater inflater = new Inflater();
        int numberOfBytesToDecompress = bytesToDecompress.length;
        inflater.setInput(bytesToDecompress, 0, numberOfBytesToDecompress);
        int bufferSizeInBytes = numberOfBytesToDecompress;
        int numberOfBytesDecompressedSoFar = 0;
        List<Byte> bytesDecompressedSoFar = new ArrayList<>();
        try {
            while (!inflater.needsInput()) {
                byte[] bytesDecompressedBuffer = new byte[bufferSizeInBytes];
                int numberOfBytesDecompressedThisTime = inflater.inflate(bytesDecompressedBuffer);
                numberOfBytesDecompressedSoFar += numberOfBytesDecompressedThisTime;
                for (int b = 0; b < numberOfBytesDecompressedThisTime; b++) {
                    bytesDecompressedSoFar.add(bytesDecompressedBuffer[b]);
                }
            }

            returnValues = new byte[bytesDecompressedSoFar.size()];
            for (int b = 0; b < returnValues.length; b++) {
                returnValues[b] = (byte)(bytesDecompressedSoFar.get(b));
            }
        } catch (DataFormatException dfe) {
            dfe.printStackTrace();
        }
        inflater.end();

        return returnValues;
    }

    /**
     * compress
     *
     * @param str
     * @return
     */
    public static byte[] compress(String str) {
        return rawCompress(str.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * decompress
     *
     * @param bytesToDecompress
     * @return
     */
    public static String decompress(byte[] bytesToDecompress) {
        byte[] bytesDecompressed = rawDecompress(bytesToDecompress);
        return new String(bytesDecompressed, StandardCharsets.UTF_8);
    }

}

