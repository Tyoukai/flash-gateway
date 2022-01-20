package flashgateway;

import com.fast.gateway.Application;
import com.fast.gateway.entity.ApiQuotaLimitDO;
import com.fast.gateway.service.ApiQuotaLimitService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class FlashGatewayApplicationTests {

    @Autowired
    private ApiQuotaLimitService apiQuotaLimitService;

    @Test
    public void listAllQuotaLimitConfigTest() {
        Map<String, ApiQuotaLimitDO> map = apiQuotaLimitService.listAllQuotaLimitConfig();
    }

}
