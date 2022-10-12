package com.rh.videostream;

import java.util.HashMap;
import java.util.Map;

public class HttpHeader {

    private final Map<String, String> _components;
    private final String _initialPart;

    public HttpHeader(String initialPart) {
        _components = new HashMap<>();
        _initialPart = initialPart;
    }

    public static HttpHeader parse(String value) {
        value = value.substring(0, value.length() - 4);
        String[] attributes = value.split("\r\n");
        HttpHeader header = new HttpHeader(attributes[0]);
        for (int i = 1; i < attributes.length; i++) {
            String[] keyValue = attributes[i].split(": ");
            if (keyValue.length == 2) {
                header._components.put(keyValue[0], keyValue[1]);
            }
        }
        return header;
    }

    public String getAttributeValue(HttpHeaderAttributes headerAttribute) {
        return _components.getOrDefault(headerAttribute.toString(), null);
    }

    public HttpHeader addAttribute(HttpHeaderAttributes headerAttribute, String value) {
        _components.put(headerAttribute.toString(), value);
        return this;
    }

    public String build() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(_initialPart)
                .append("\r\n");
        for (Map.Entry<String, String> entry : _components.entrySet()) {
            stringBuilder
                    .append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append("\r\n");
        }
        return stringBuilder
                .append("\r\n")
                .toString();
    }
}
