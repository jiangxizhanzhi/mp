package cn.cyberpeace.mp.base.util.protocolprobe;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.util.IOUtils;

import javax.net.SocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;


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
        
        Socket socket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            socket = SocketFactory.getDefault().createSocket();
            socket.setSoTimeout(2000);
            socket.connect(new InetSocketAddress(host, port));
            inputStream = socket.getInputStream();
            
            outputStream = socket.getOutputStream();
            outputStream.write(this.getClientInfo());
            outputStream.flush();
            
            byte[] result = new byte[this.getServerInfoByteNum()];
            inputStream.read(result);
            return this.checkServerInfo(result);
        } catch (SocketTimeoutException e) {
            log.info("failed to connect {}//{}:{} , read timeout", protocol, host, port);
            return false;
        } catch (ConnectException e) {
            log.info("failed to connect {}//{}:{} , connect exception", protocol, host, port);
            return false;
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

    
    protected abstract byte[] getClientInfo();

    
    protected abstract boolean checkServerInfo(byte[] serverInfo);

    
    protected int getServerInfoByteNum() {
        return 20;
    }

}
