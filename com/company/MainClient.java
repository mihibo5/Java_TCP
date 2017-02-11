/**
 * The MainClient program implements a simple usage of LibTCP library.
 *
 * @author  Miha Bogataj
 * @version 1.0
 * @since   2016-02-11
 */

package com.company;

import LibTCP.Client;
import LibTCP.OnConnect;

import java.io.IOException;
import java.util.HashMap;

public class MainClient {

    public static void main (String args[]) throws InterruptedException, IOException {
        Client client = new Client("192.168.2.140", 1337, new OnConnect() {
            @Override
            public void onCreate(HashMap<String, String> serverInfo) {
                System.out.println("Connecting to: " + serverInfo.get("IP") + ":" + serverInfo.get("Port"));
            }

            @Override
            public void onConnected() {
                System.out.println("Connection established!");
            }

            @Override
            public void onDisconnected(Object e) {
                System.out.println("Connection broken off!");
            }

            @Override
            public void onConnectionFailed() {
                System.out.println("Failed connecting to server!");
            }
        });

        for (int i = 0; true; i++) {
            client.connect("test " + i);
            client.disconnect();
            System.out.println("\n");
            Thread.sleep(1000);
        }
    }
}
