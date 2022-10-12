package com.rh.videostream;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class WebSocketClient {

    enum ClientState {
        CONNECTING,
        CONNECTED,
        CLOSED
    }

    private final DataOutputStream _outputStream;
    private final DataInputStream _inputStream;
    private final Socket _socket;
    private ClientState _state;

    public WebSocketClient(Socket socket) throws IOException {
        _socket = socket;
        _state = ClientState.CONNECTING;
        _inputStream = new DataInputStream(_socket.getInputStream());
        _outputStream = new DataOutputStream(_socket.getOutputStream());
    }

    public void setState(ClientState state) {
        _state = state;
    }

    public ClientState getState() {
        return _state;
    }

    public boolean isDataAvailable() throws IOException {
        return _inputStream.available() > 0;
    }

    public byte[] read() throws IOException {
        byte[] data = new byte[_inputStream.available()];
        _inputStream.readFully(data);
        return data;
    }

    public void write(byte[] data) throws IOException {
        _outputStream.write(data);
    }

    public void close() {
        try {
            _inputStream.close();
            _socket.close();
        } catch (IOException ioe) {
            Log.e("WebSocketClient", "Failed to close client");
        }
    }
}
