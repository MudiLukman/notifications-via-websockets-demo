package websockets.test_helper;

import com.kontrol.users.model.UserDTO;
import com.kontrol.websockets.AppNotificationService;
import io.quarkus.scheduler.Scheduled;
import io.quarkus.test.Mock;
import io.quarkus.vertx.ConsumeEvent;

import javax.enterprise.context.ApplicationScoped;

@Mock
@ApplicationScoped
public class AppNotificationServiceMock extends AppNotificationService {

    @Override
    @ConsumeEvent("ws-new-user")
    public void consumeNewUser(UserDTO userDTO) {
        super.consumeNewUser(userDTO);
    }

    @Override
    @Scheduled(every = "1s")
    public void computeSummary() {
        super.computeSummary();
    }

}
