package websockets.test_helper;

import com.kontrol.websockets.decoders.NotificationDecoder;
import com.kontrol.websockets.model.Notification;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.ClientEndpoint;
import javax.websocket.OnMessage;

@ApplicationScoped
@ClientEndpoint(decoders = NotificationDecoder.class)
public class WebsocketClient {

    private Notification notification;

    @OnMessage
    public void onMessage(Notification notification) {
        this.notification = notification;
    }

    public Notification getNotification() {
        return notification;
    }
}
