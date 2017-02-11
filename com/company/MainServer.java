/**
 * The MainServer program implements a simple usage of LibTCP library.
 *
 * @author  Miha Bogataj
 * @version 1.0
 * @since   2016-02-11
 */

package com.company;

import LibTCP.OnConnect;
import LibTCP.Server;

import java.io.IOException;
import java.util.HashMap;

public class MainServer {

    private static HashMap<String, String> data = new HashMap<>();

    public static void main(String[] args) throws IOException, InterruptedException {
        final Server server = new Server("192.168.2.140", 1337, data, new OnConnect() {
            @Override
            public void onCreate(HashMap<String, String> address) {
                System.out.format("Server is listening at " + address.get("IP") + ":" + address.get("Port"));
            }

            @Override
            public void onConnected() {
                System.out.println("\n\nCONNECTED!");
                System.out.println(data.get("IP"));
                System.out.println(data.get("Message"));
            }

            @Override
            public void onDisconnected(Object e) {
                System.out.println("\n\nDISCONNECTED!");
                System.out.println(data.get("IP"));
            }

            @Override
            public void onConnectionFailed() {
                System.out.println("FAILED TO ACCEPT CONNECTION!");
            }
        });
    }

}
