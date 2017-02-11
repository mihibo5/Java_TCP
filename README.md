# Java_TCP
It supports several events such as even on server creation, on connection recieved, on connection lost failed.


In this document it is described how we can:<br />
     -install library<br />
     -create server<br />
     -use events<br />
     -read client message on server<br />
     -create client<br />
     -send message to server<br />


##Installation
-Download the release Jar file (<a href="https://github.com/mihibo5/Java_TCP/releases/download/1.0/LibTCP.jar">Download</a>)<br />
-Import Jar file into External Libraries<br />
-Follow the instructions below<br />


##Basics

Server supports methods such as method for disonnecting and restarting server. Client supports methods such as methods for establishing connection and disconnecting.

The whole server and client runs asynchronically. This will allow your code to keep running after the server starts or client establishes connection.

##Server

The whole project is written in Java. The usage of this library is simple. Here are an example of how to create server:

```java
import LibTCP.OnConnect;
import LibTCP.Server;

import java.io.IOException;
import java.util.HashMap;

public class MyServer {
    
    private static HashMap<String, String> data = new HashMap<>();
    
    public static void main (String args[]) throws IOException, InterruptedException {
        final Server server = new Server("192.168.1.100", 11000, data, new OnConnect() {
            @Override
            public void onCreate(HashMap<String, String> serverInfo) {
                //YOUR CODE HERE
            }

            @Override
            public void onConnected() {
                //YOUR CODE HERE
            }

            @Override
            public void onDisconnected(Object e) {
                //YOUR CODE HERE
            }

            @Override
            public void onConnectionFailed() {
                //YOUR CODE HERE
            }
        });
    }
}
```

When we ceate server we have to provide it machine IP and port it will be running on. For local TCP server we can simply use computer's local IP as it is shown in parameters in upper example. It generally doesn't matter which port we use as long as it is not taken already. After we successfully create Server object it will first run method onCreate. Here we can access server information from HashMap serverInfo. Currently it only contains server IP and port, but feel free to recommend what else it could contain. To read server info we have to do as it is shown is this example:

```java
String serverAddress = serverInfo.get("IP");            //gets server IP
int port = Integer.parseInt(serverInfo.get("Port"));    //gets server port
```

Each time a connection is established with this server it will run a method onConnected() and each time a client will be disconnected it will run method onDisconnected.

As server supports methods such as disconnecting server and restarting it, we must pay attention what we write in onConnectionFailed() and in onCreate(). When server gets disconnected it will run metho onConnectionFailed() and when we restart server it will run onCreate().

To disconnect and restart server we call methods as it is shown in an example:

```java
server.disconnect();    //disconnects server
server.restart();       //restarts server
```

In HashMap data we can find the data and message of the last connected client. Information can be read as it is shown in an example:

```java
String clientAddress = data.get("IP");
String message = data.get("Message");
```

If we wanted for example print IP and message of each client when they connect we would use this in method onConnected().

##Client

Now let's see an example of how to create a TCP client:

```java
import LibTCP.Client;
import LibTCP.OnConnect;

import java.io.IOException;
import java.util.HashMap;

public class MyClient {

    public static void main (String args[]) throws InterruptedException, IOException {
        Client client = new Client("192.168.1.10", 11000, new OnConnect() {
            @Override
            public void onCreate(HashMap<String, String> serverInfo) {
                //YOUR CODE HERE
            }

            @Override
            public void onConnected() {
                //YOUR CODE HERE
            }

            @Override
            public void onDisconnected(Object e) {
                //YOUR CODE HERE
            }

            @Override
            public void onConnectionFailed() {
                //YOUR CODE HERE
            }
        });
    }
}
```

This time we use server's IP address and port in parameters. methods onCreate(), onConnected(), onDisconnected() and onConnectionFailed() have the same properties as the server's ones and should run at the same time. In method onCreate() we can get server's IP address and port the same way as we can get it in server's class.

The client supports methods connect(String message) and disconnect(). Method connect contains parameter in which you put the message you want to send to server. Usages are displayed in next examples:

```java
while (true) {
    client.connect("connect me!");      //sending "connect me!" to server
    client.disconnect();                //disconnecting so we can send message again
    Thread.sleep(1000);                 //let's not have an overkill and send just one per second
}
```

or if we want to send only one message we do simply:

```java
client.connect("connect me!");
client.disconnect();
```

This must be written outside of inner class after we create a Client object.


##License
MIT License

Copyright (c) 2017 Miha Bogataj

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
