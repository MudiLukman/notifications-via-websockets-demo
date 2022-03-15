package com.kontrol.websockets.security;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import java.util.Arrays;

public class WebsocketFilter extends ServerEndpointConfig.Configurator {

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        String[] queryParams = request.getQueryString().split("&");
        Arrays.stream(queryParams)
                .filter(queryParam -> queryParam.startsWith("code="))
                .findFirst()
                .ifPresent(accessCode -> {
                    accessCode = accessCode.substring(accessCode.indexOf('=') + 1); //Everything after 'code='
                    sec.getUserProperties().put("code", accessCode);
                });
    }

}
