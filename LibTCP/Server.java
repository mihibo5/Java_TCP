/**
 * The Server program implements a TCP server. Usage is displayed in MainServer.java.
 *
 * @author  Miha Bogataj
 * @version 1.0
 * @since   2016-02-11
 */

package LibTCP;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.HashMap;

public class Server {
    private String serverAddress;
    private int serverPort;
    private OnConnect onConnect;

    private HashMap<String, String> serverInfo = new HashMap<>();
    private AsynchronousServerSocketChannel server;

    HashMap<String, String> message;

    public Server(String IP, int port, OnConnect todo) throws IOException, InterruptedException {
        serverAddress = IP;
        serverPort = port;
        onConnect = todo;
        serverInfo.put("IP", IP);
        serverInfo.put("Port", Integer.toString(port));

        this.startServer();
    }

    public Server(String IP, int port, HashMap<String, String> returner, OnConnect todo) throws IOException, InterruptedException {
        serverAddress = IP;
        serverPort = port;
        onConnect = todo;
        serverInfo.put("IP", IP);
        serverInfo.put("Port", Integer.toString(port));

        message = returner;

        this.startServer();
    }

    /**
     * Obsolete! Displayer can be now used by onCreate/onConnect/onDisconnect events by user himself
     * **/
    /*public Server(String IP, int port, Object displayer, OnConnect todo) throws IOException, InterruptedException {
        serverAddress = IP;
        serverPort = port;
        onConnect = todo;
        serverInfo.put("IP", IP);
        serverInfo.put("Port", Integer.toString(port));

        this.startServer();
    }*/

    private void startServer() throws IOException, InterruptedException {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Data data = new Data(message);

                    server = AsynchronousServerSocketChannel.open();

                    String host = serverAddress;
                    int port = serverPort;

                    InetSocketAddress sAddress = new InetSocketAddress(host, port);
                    server.bind(sAddress);

                    onConnect.onCreate(serverInfo);

                    Attachment attach = new Attachment();
                    attach.server = server;
                    server.accept(attach, new ConnectionHandler(data, onConnect));

                    Thread.currentThread().join();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void disconnect() throws IOException {
        this.server.close();
    }

    public void restart() throws IOException, InterruptedException {
        this.server.close();
        this.startServer();
    }
}


class Attachment {
    AsynchronousServerSocketChannel server;
    AsynchronousSocketChannel client;
    ByteBuffer buffer;
    SocketAddress clientAddress;
    boolean isRead;
}

class ConnectionHandler implements CompletionHandler<AsynchronousSocketChannel, Attachment> {

    private Data data;
    private OnConnect onConnect;

    ConnectionHandler(Data d, OnConnect todo) {
        data = d;
        onConnect = todo;
    }

    @Override
    public void completed(AsynchronousSocketChannel client, Attachment attach) {
        try {
            SocketAddress clientAddr = client.getRemoteAddress();
            data.data.put("IP", clientAddr.toString());
            attach.server.accept(attach, this);
            ReadWriteHandler rwHandler = new ReadWriteHandler(data, onConnect);

            Attachment newAttach = new Attachment();
            newAttach.server = attach.server;
            newAttach.client = client;
            newAttach.buffer = ByteBuffer.allocate(2048);
            newAttach.isRead = true;
            newAttach.clientAddress = clientAddr;
            client.read(newAttach.buffer, newAttach, rwHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failed(Throwable e, Attachment attach) {
        onConnect.onConnectionFailed();
    }
}

class ReadWriteHandler implements CompletionHandler<Integer, Attachment> {

    private Data data;
    private OnConnect onConnect;

    ReadWriteHandler(Data d, OnConnect todo) {
        data = d;
        onConnect = todo;
    }

    @Override
    public void completed(Integer result, Attachment attach) {
        if (result == -1) {
            try {
                attach.client.close();
                onConnect.onDisconnected(null);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return;
        }

        if (attach.isRead) {
            attach.buffer.flip();
            int limits = attach.buffer.limit();
            byte bytes[] = new byte[limits];
            attach.buffer.get(bytes, 0, limits);
            Charset cs = Charset.forName("UTF-8");
            String msg = new String(bytes, cs);
            data.data.put("Message", msg);
            attach.isRead = false;
            attach.buffer.rewind();

            onConnect.onConnected();

        } else {
            // Write to the client
            attach.client.write(attach.buffer, attach, this);
            attach.isRead = true;
            attach.buffer.clear();
            attach.client.read(attach.buffer, attach, this);
        }
    }

    @Override
    public void failed(Throwable e, Attachment attach) {
        e.printStackTrace();
    }
}

class Data {
    HashMap<String, String> data;

    Data(HashMap<String, String> d) {
        data = d;
    }

}