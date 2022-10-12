package com.rh.videostream;

import androidx.annotation.NonNull;

public enum HttpHeaderAttributes {
    UPGRADE("Upgrade"),
    CONNECTION("Connection"),
    SEC_WEBSOCKET_KEY("Sec-WebSocket-Key"),
    SEC_WEBSOCKET_ACCEPT("Sec-WebSocket-Accept");

    private String _value;

    HttpHeaderAttributes(String value) {
        _value = value;
    }

    @NonNull
    public String toString() {
        return _value;
    }
}
