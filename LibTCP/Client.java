/**
 * The Server program implements a TCP client. Usage is displayed in MainClient.java.
 *
 * @author  Miha Bogataj
 * @version 1.0
 * @since   2016-02-11
 */

package LibTCP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class Client {

    private String serverAddress;
    private int serverPort;
    private OnConnect onConnect;

    private HashMap<String, String> serverInfo = new HashMap<>();
    private Socket s = null;

    public Client(String IP, int port, OnConnect todo) {
        serverAddress = IP;
        serverPort = port;
        onConnect = todo;
        serverInfo.put("IP", serverAddress);
        serverInfo.put("Port", Integer.toString(serverPort));

        onConnect.onCreate(serverInfo);
    }

    public void connect(final String message) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    onConnect.onConnected();
                    s = new Socket(serverAddress, serverPort);
                    DataInputStream input = new DataInputStream(s.getInputStream());
                    DataOutputStream output = new DataOutputStream( s.getOutputStream());

                    output.writeInt(message.length());
                    output.writeBytes(message);


                    /**
                     * Obsolete: disables disconnection due to never ending reading.
                     * */
                    /*
                    int nb = input.readInt();
                    byte[] digit = new byte[nb];
                    for(int i = 0; i < nb; i++) {
                        digit[i] = input.readByte();
                    }
                    */

                    input.close();
                    output.close();

                } catch (Exception e) {
                    onConnect.onDisconnected(e);
                } finally {
                    if (s != null) {
                        try {
                            s.close();
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }
            }
        });
        thread.run();
    }

    public void disconnect() throws IOException {
        s.close();
    }

}
