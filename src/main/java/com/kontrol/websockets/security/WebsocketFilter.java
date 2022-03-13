package com.kontrol.websockets.security;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

public class WebsocketFilter extends ServerEndpointConfig.Configurator {

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        String queryString = request.getQueryString(); //to be revisited for multiple query params
        String accessCode;
        if (queryString != null) {
            accessCode = queryString.substring(queryString.indexOf('=') + 1); //Everything after 'code='
            sec.getUserProperties().put("code", accessCode);
        }
    }

}
