package cn.evun.sweet.core.test.mq.ons;

import cn.evun.sweet.core.ons.OnsMQService;
import cn.evun.sweet.core.ons.OnsMessage;
import cn.evun.sweet.core.ons.bean.OnsConsumerBean;
import cn.evun.sweet.core.ons.bean.OnsProducerBean;
import cn.evun.sweet.core.ons.bean.OnsTransactionProducerBean;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionExecuter;
import com.aliyun.openservices.ons.api.transaction.TransactionStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.CountDownLatch;

/**
 * Created by Administrator on 2017/2/27.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:beans-test.xml"})
public class OnsMQServiceTester {

    @Autowired
    private OnsMQService onsMQService;

    /**
     * 测试普通消息
     *
     * @throws InterruptedException
     */
    @Test
    public void testSendReciveMessage() throws InterruptedException {
        String topic = "ssr";
        String tag = "test001";
        String message = "Hello JYC99!";
        OnsMessage onsMessage = new OnsMessage(topic, tag, message);
        OnsProducerBean producerBean = onsMQService.createProducerBean(null);
        OnsConsumerBean consumerBean = onsMQService.createConsumerBean(null, null);

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        consumerBean.subscribe(topic, tag, new MessageListener() {

            @Override
            public Action consume(Message message, ConsumeContext context) {
                System.out.println("----------consumer start----------");
                try {
                    System.out.println((String) onsMQService.deserialize(message.getBody()));
                } catch (Exception e) {
                    System.out.println("----------consumer error----------");
                }
                System.out.println("----------consumer end----------");
                countDownLatch.countDown();
                return Action.CommitMessage;
            }
        });
        producerBean.send(onsMessage);
        countDownLatch.await();
    }

    /**
     * 测试事务消息
     *
     * @throws InterruptedException
     */
    @Test
    public void testTransactionMessage() throws Exception {
        String topic = "ssr";
        String tag = "testTransaction001";
        String message = "测试，聚有财!";
        OnsMessage onsMessage = new OnsMessage(topic, tag, message);
        OnsTransactionProducerBean transactionProducerBean = onsMQService.createTransactionProducerBean(null);
        OnsConsumerBean consumerBean = onsMQService.createConsumerBean(null, null);

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        consumerBean.subscribe(topic, tag, new MessageListener() {

            @Override
            public Action consume(Message message, ConsumeContext context) {
                System.out.println("----------consumer start----------");
                try {
                    System.out.println((String) onsMQService.deserialize(message.getBody()));
                } catch (Exception e) {
                    System.out.println("----------consumer error----------");
                }
                System.out.println("----------consumer end----------");
                countDownLatch.countDown();
                return Action.CommitMessage;
            }
        });
        transactionProducerBean.send(onsMessage, new LocalTransactionExecuter() {

            @Override
            public TransactionStatus execute(Message msg, Object arg) {
                try {
                    System.out.println((String) onsMQService.deserialize(msg.getBody()));
                    System.out.println(arg.toString());
                } catch (Exception e) {
                    return TransactionStatus.RollbackTransaction;
                }

                return TransactionStatus.CommitTransaction;
            }
        }, "1234");
        countDownLatch.await();
    }

}
