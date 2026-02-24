package cz.gyarabProject.api.cs;

import cz.gyarabProject.__3e_VirtualniBankovnictvi.Application;
import cz.gyarabProject.api.cs.account.Notification;
import cz.gyarabProject.api.cs.account.Payment;
import cz.gyarabProject.api.cs.datatype.notification.NotificationInfo;
import cz.gyarabProject.api.cs.datatype.payment.AccountInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest(classes = Application.class)
public class NotificationTest {
    @Autowired private Notification notification;
    @Autowired private Payment payment;
    private String account;
    private String notificationAccountId = null;
    private String notificationCardId = null;

    @BeforeEach
    public void setUp() throws IOException, InterruptedException {
        List<AccountInfo> accounts = payment.getAccountInfo(42, 2, "iban", "desc").items();
        assert accounts != null && !accounts.isEmpty();
        this.account = accounts.getFirst().id();
    }

    public boolean create() throws IOException, InterruptedException {
        notificationAccountId = notification.create(account, Notification.Specification.account).notificationId();
        notificationCardId = notification.create(account, Notification.Specification.account).notificationId();
        return notificationAccountId != null && notificationCardId != null;
    }

    public boolean list() throws IOException, InterruptedException {
        boolean result = false;
        for (NotificationInfo n : notification.list(account, Notification.Specification.account)) {
            if (n.notificationId().equals(notificationAccountId)) {
                result = true;
                break;
            }
        }
        if (!result) {
            return false;
        }
        result = false;
        for (NotificationInfo n : notification.list(account, Notification.Specification.card)) {
            if (n.notificationId().equals(notificationCardId)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public void delete() throws IOException, InterruptedException {
        notification.delete(account, notificationAccountId, Notification.Specification.account);
        notificationAccountId = null;
        notification.delete(account, notificationCardId, Notification.Specification.card);
        notificationCardId = null;
    }

    @Test
    public void test() {
        try {
            if (!create()) {
                System.out.println("Create!");
            }
            if (!list()) {
                System.out.println("List!");
            }
            delete();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
