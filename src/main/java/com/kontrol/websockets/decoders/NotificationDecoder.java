package com.kontrol.websockets.decoders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kontrol.websockets.model.Notification;

import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class NotificationDecoder implements Decoder.Text<Notification> {
    @Override
    public Notification decode(String s){
        try {
            return new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .readValue(s, Notification.class);
        } catch (JsonProcessingException ex) {
            System.out.println("Error decoding: " + s);
            throw new IllegalArgumentException(ex);
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
