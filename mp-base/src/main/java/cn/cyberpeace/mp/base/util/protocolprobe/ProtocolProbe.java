package cn.cyberpeace.mp.base.util.protocolprobe;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.util.IOUtils;

import javax.net.SocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @Description
 * @Author maojun
 * @Date 2022/1/14
 */
@Slf4j
public abstract class ProtocolProbe {
    private String protocol;
    private String host;
    private int port;

    public ProtocolProbe(String protocol, String host, int port) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
    }

    public boolean probe() {
        // 建立socket连接
        Socket socket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            socket = SocketFactory.getDefault().createSocket();
            socket.setSoTimeout(2000);
            socket.connect(new InetSocketAddress(host, port));
            inputStream = socket.getInputStream();
            // 和服务端交换数据
            outputStream = socket.getOutputStream();
            outputStream.write(this.getClientInfo());
            outputStream.flush();
            // 对交换得到的数据进行校验
            byte[] result = new byte[this.getServerInfoByteNum()];
            inputStream.read(result);
            return this.checkServerInfo(result);
        } catch (Exception e) {
            log.error("failed to connect {}//{}:{}", protocol, host, port, e);
            return false;
        } finally {
            try {
                IOUtils.close(inputStream, outputStream, socket);
            } catch (IOException e) {
                log.error("failed to close socket resource,{}//{}:{}", protocol, host, port, e);
            }
        }
    }

    /**
     * 建立socket连接之后,给服务端发送的信息
     * @return
     */
    protected abstract byte[] getClientInfo();

    /**
     * 校验从服务端得到的信息
     * @param serverInfo
     * @return
     */
    protected abstract boolean checkServerInfo(byte[] serverInfo);

    /**
     * 从返回数据中取出用于结果判断的字节数量
     * @return
     */
    protected int getServerInfoByteNum() {
        return 20;
    }

}
