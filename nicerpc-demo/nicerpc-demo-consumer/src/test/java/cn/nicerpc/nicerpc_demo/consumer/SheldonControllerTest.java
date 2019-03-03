package cn.nicerpc.nicerpc_demo.consumer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SheldonControllerTest.class)
@ComponentScan("cn.nicerpc.consumer,cn.nicerpc.nicerpc_demo.consumer")
public class SheldonControllerTest {

    @Autowired
    SheldonController sheldonController;

    @Test
    public void test1() {
        sheldonController.testService();
    }
}
