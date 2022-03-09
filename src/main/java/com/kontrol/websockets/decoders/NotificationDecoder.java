package com.kontrol.websockets.decoders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kontrol.websockets.model.Notification;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class NotificationDecoder implements Decoder.Text<Notification> {
    @Override
    public Notification decode(String s) throws DecodeException {
        try {
            return new ObjectMapper().convertValue(s, Notification.class);
        } catch (IllegalArgumentException ex) {
            System.out.println("error: " + ex.getMessage() + " decoding: " + s);
            throw new DecodeException(s, ex.getMessage());
        }
    }

    @Override
    public boolean willDecode(String s) {
        return true;
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
    }

    @Override
    public void destroy() {
    }
}
