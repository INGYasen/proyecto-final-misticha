package com.raymisupreme.mspagos;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "MP_ACCESS_TOKEN=TEST-placeholder",
        "MP_NOTIFICATION_URL=https://example.test/api/v1/pagos/mercadopago/webhook"
        ,"STRIPE_SECRET_KEY=sk_test_placeholder"
})
class MsPagosApplicationTests {

    @Test
    void contextLoads() {
    }
}
