package cn.coderap.goods.controller;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/redisson")
public class TestController {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 一、redisson解决了两个问题：
     * 1、锁的续期：如果业务代码很耗时，运行期间不断将锁的过期时间续期为30s（默认），即不用担心业务代码执行期间锁过期的问题
     * 2、锁有过期时间（默认30s）：加锁的业务只要运行完成，看门狗就不会给该锁继续续期（程序挂掉时，看门狗没有了，也就无法继续续期），这样即使不手动解锁，那么默认30s后该锁也会被释放
     *
     * 二、尽量不要使用lock.lock(10,TimeUnit.SECONDS)方法，因为该锁10s过期后，看门狗不会给该锁自动续期。原因是：
     * 1、如果传递了超时时间，就发送给redis脚本进行占锁，如果占锁成功，该锁的超时时间就是我们指定的时间；
     * 2、如果未指定超时时间，就使用该锁的看门狗过期时间lockWatchDogTimeout(默认为30s)，如果占锁成功，
     *
     * TODO 以上两个问题，都可以通过查询lock.lock()方法的源码获得，具体调试后面详细看
     */
    @GetMapping
    public String testRedisson() {
        RLock lock = redissonClient.getLock("test-lock");
//        lock.lock(10, TimeUnit.SECONDS);
        lock.lock();
        try {
            System.out.println("加锁成功,开始执行业务..." + Thread.currentThread().getId());
            Thread.sleep(60000); // 模拟耗时业务代码
        }catch(Exception e) {

        }finally {
            // 问题：如果程序在这里闪断，释放锁的程序没有执行，那么redisson会不会出现死锁？
            // 验证：在7998和7999两个端口启动程序，然后向两个端口分别发送/redisson请求，在7998执行耗时业务代码时直接将程序停掉，看7999处的程序能够获取到锁即可
            // 结论：不会出现死锁，原因程序挂掉时，看门狗没有了，也就无法继续续期，这样即使不手动解锁，那么默认30s后该锁也会过期
            lock.unlock();
            System.out.println("释放锁成功..." + Thread.currentThread().getId());
        }
        return "redisson ok";
    }
}
