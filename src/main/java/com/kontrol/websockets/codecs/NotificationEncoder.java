package com.kontrol.websockets.codecs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kontrol.websockets.model.Notification;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class NotificationEncoder implements Encoder.Text<Notification> {
    @Override
    public String encode(Notification notification) throws EncodeException {
        try {
            return new ObjectMapper().writeValueAsString(notification);
        } catch (JsonProcessingException ex) {
            System.out.println("error: " + ex.getMessage() + " encoding: " + notification);
            throw new EncodeException(notification, "Unable to encode " + notification);
        }
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
    }

    @Override
    public void destroy() {
    }
}
