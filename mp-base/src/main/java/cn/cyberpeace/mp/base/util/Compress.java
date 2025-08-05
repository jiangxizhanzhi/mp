package cn.cyberpeace.mp.base.util;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.jpountz.lz4.LZ4FrameOutputStream;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@Slf4j
public class Compress {

    public static File compressZip(File inputFile, String password) {
        try {
            File zipFile = File.createTempFile("compressed-", ".zip");
            ZipFile zip = new ZipFile(zipFile);

            ZipParameters parameters = new ZipParameters();
            parameters.setEncryptFiles(true);
            parameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);
            if (password != null && !password.isEmpty()) {
                zip.setPassword(password.toCharArray());
            }

            if (inputFile.isDirectory()) {
                zip.addFolder(inputFile, parameters);
            } else {
                zip.addFile(inputFile, parameters);
            }

            return zipFile;
        } catch (IOException e) {
            log.info(e.getMessage(), e);
            throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("压缩文件异常" + e.getMessage()));
        }
    }


    public static File compressLZ4(File inputFile) {
        try {
            File lz4File = File.createTempFile("compressed-", ".lz4");

            try (
                    FileInputStream fis = new FileInputStream(inputFile);
                    FileOutputStream fos = new FileOutputStream(lz4File);
                    LZ4FrameOutputStream lz4Out = new LZ4FrameOutputStream(fos)
            ) {
                byte[] buffer = new byte[4096];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    lz4Out.write(buffer, 0, len);
                }
            }

            return lz4File;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("压缩文件异常" + e.getMessage()));
        }
    }
}
