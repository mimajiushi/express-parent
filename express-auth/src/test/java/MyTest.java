import com.cwj.express.auth.ExpressAuthApplication;
import com.cwj.express.auth.config.auth.AuthConfig;
import com.cwj.express.auth.service.AuthService;
import com.cwj.express.auth.service.SysUserService;
import com.cwj.express.domain.ucenter.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = ExpressAuthApplication.class)
@RunWith(SpringRunner.class)
@Slf4j
public class MyTest {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private AuthService authService;
    @Autowired
    private AuthConfig authConfig;

    @Test
    public void test1(){
        SysUser sysUser = sysUserService.getExtByUserName("chenwenjie");
        log.info(sysUser + "");

        String authToken =  authService.login("chenwenjie","mimajiushi",authConfig.getClientId(),authConfig.getClientSecret());
        log.info(authToken + "");
    }
}
