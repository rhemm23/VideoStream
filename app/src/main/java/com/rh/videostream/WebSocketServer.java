package com.rh.videostream;

import android.util.Log;

import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;

public class WebSocketServer {

    private ArrayList<WebSocketClient> _clients;
    private Thread _clientListenerThread;
    private Thread _listenerThread;

    private ServerSocket _socket;
    private boolean _running;

    private final Runnable _runnableServerListener = () -> {
        while (_running) {
            try {
                Socket client = _socket.accept();
                _clients.add(new WebSocketClient(client));
            } catch (IOException ioe) {
                Log.e("WebSocketServer", "Failed to accept client");
            }
        }
    };

    private final Runnable _runnableClientListener = () -> {
        while (_running) {
            for (int i = _clients.size() - 1; i >= 0; i--) {
                WebSocketClient client = _clients.get(i);
                try {
                    if (client.isDataAvailable()) {
                        if (client.getState() == WebSocketClient.ClientState.CONNECTING) {
                            HttpHeader header = HttpHeader.parse(new String(client.read(), StandardCharsets.UTF_8));
                            String key = header.getAttributeValue(HttpHeaderAttributes.SEC_WEBSOCKET_KEY);
                            String unhashedValue = key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
                            String acceptValue = Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA1").digest(unhashedValue.getBytes(StandardCharsets.UTF_8)));

                            String response = new HttpHeader("HTTP/1.1 101 Switching Protocols")
                                    .addAttribute(HttpHeaderAttributes.UPGRADE, "websocket")
                                    .addAttribute(HttpHeaderAttributes.CONNECTION, "Upgrade")
                                    .addAttribute(HttpHeaderAttributes.SEC_WEBSOCKET_ACCEPT, acceptValue)
                                    .toString();

                            client.write(response.getBytes(StandardCharsets.UTF_8));
                            client.setState(WebSocketClient.ClientState.CONNECTED);
                        }
                    }
                } catch (IOException ioException) {
                    client.close();
                    _clients.remove(i);
                } catch (NoSuchAlgorithmException nsa) {
                    Log.e("WebSocketServer", "No SHA1");
                }
            }
        }
    };

    public WebSocketServer() {
        try {
            _clients = new ArrayList<>();
            _socket = new ServerSocket(0, 10);
            _listenerThread = new Thread(_runnableServerListener);
            _clientListenerThread = new Thread(_runnableClientListener);
        } catch (IOException ioe) {

        }
    }

    public void start() {
        _running = true;
        _listenerThread.start();
        _clientListenerThread.start();
    }

    public void stop() {

    }

    public int getPort() {
        return _socket.getLocalPort();
    }
}
