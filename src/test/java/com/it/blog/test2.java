package com.it.blog;

import com.it.blog.pojo.SobUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class test2 {
    @Test
    public void test1() {
        SobUser sobUser = new SobUser();
        sobUser.setPassword("123456");
        String password = sobUser.getPassword();
        log.info("password ===>" + password);
    }
    @Test
    public void test() {
        SobUser sobUser = new SobUser();
        sobUser.setPassword("123456");
        String password = sobUser.getPassword();
        log.info("password ===>" + password);
    }
}
