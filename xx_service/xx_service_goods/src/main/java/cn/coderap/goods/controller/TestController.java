package cn.coderap.goods.controller;

import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/redisson")
public class TestController {

    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 可重入锁
     *
     * 一、redisson解决了两个问题：
     * 1、锁的续期：如果业务代码很耗时，运行期间不断将锁的过期时间续期为30s（默认），即不用担心业务代码执行期间锁过期的问题
     * 2、锁有过期时间（默认30s）：正常情况下，加锁的业务只要运行完成，就会调用unlock方法释放锁（取消看门狗的自动续期也在这里完成）；
     *    如果程序挂掉时，看门狗没有了，也就无法继续续期，这样即使不手动解锁，那么默认30s后该锁也会被释放。
     *
     * 二、lock.lock(10,TimeUnit.SECONDS)，该锁10s过期后，看门狗不会给该锁自动续期（感觉这可能是个bug，高版本进行了修改）。原因是：
     * 1、如果传递了超时时间，就发送给redis脚本进行占锁，如果占锁成功，默认超时时间就是我们指定的时间，没做其他；
     * 2、如果未指定超时时间，就使用该锁的看门狗过期时间lockWatchDogTimeout(默认为30s)，如果占锁成功，在Redisson#renewExpiration就会启动一个
     *    延时任务（异步重置锁过期时间为lockWatchDogTimeout，完成后递归调用Redisson#renewExpiration启动一个延时任务)，该延时任务 lockWatchDogTimeout/3 后执行。
     *    也就是说，默认10s续期一次。
     *
     * 以上两个问题，都可以通过查询 lock.lock()->RedissonLock#tryAcquire 的源码获得，调试后面再进行
     *
     * 最佳实践：使用lock.lock(30,TimeUnit.SECONDS)，明确超时时间，省掉续期操作，手动解锁（最慢30s后自动解锁）。注意：如果业务超过该时间，说明业务本身有问题，
     *          不仅如此，还可能导致数据库连接等问题。
     *
     * 公平锁与非公平锁
     * 公平锁是有先来后到顺序的，非公平锁是抢占式的
     *
     * 读（共享）写（排它）锁
     * 读 + 读，读锁可以并发
     * 写 + 读，读锁必须等待写锁释放
     * 写 + 写，后面的写锁必须等待前面写锁的释放
     * 读 + 写，先读锁，写锁也要等待读锁释放
     * 使用场景：一个线程写，多个线程读，为了保证读线程都能读到最新的数据，可以使用读写锁，即写完后才可以读。
     *
     * 信号量
     * 使用场景1：停车场停车问题，即停车场一共信号量大小个车位，当车位充足时，可以停放（acquire成功）；当车位满时，就等待（acquire阻塞），直到有一辆开走（release成功）。
     * 使用场景2：分布式限流，比如某个微服务能承受10000并发请求，那么每个请求先去获取一个信号量，如果acquire成功，那么当前请求可以被处理；如果acquire失败，当前请求阻塞等待，
     * 直接某个请求处理完（release信号量），当前请求才能被处理。
     *
     * 注意：tryAcquire，不会阻塞，即看一眼是否有车位就停放，没有车位就放弃了，不会等待。
     *
     * 闭锁（CountDownLatch)
     * 使用场景：学校的5个班学生都走完了才能锁大门
     */

    @GetMapping
    public String testReentrantLock() {
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

    @GetMapping("/write")
    public String testWriteLock() {
        RReadWriteLock lock = redissonClient.getReadWriteLock("test-read-write-lock");
        RLock writeLock = lock.writeLock();
        writeLock.lock();
        String s = UUID.randomUUID().toString();
        try {
            System.out.println("加写锁成功,开始执行业务..." + Thread.currentThread().getId());
            Thread.sleep(30000); // 模拟耗时业务代码
            stringRedisTemplate.opsForValue().set("write-value", s);
        }catch(Exception e) {
            e.printStackTrace();
        }finally {
            writeLock.unlock();
            System.out.println("释放写锁成功..." + Thread.currentThread().getId());
        }
        return s;
    }

    @GetMapping("/read")
    public String testReadLock() {
        RReadWriteLock lock = redissonClient.getReadWriteLock("test-read-write-lock");
        RLock readLock = lock.readLock();
        readLock.lock();
        String s = null;
        try {
            System.out.println("加读锁成功,开始执行业务..." + Thread.currentThread().getId());
            s = stringRedisTemplate.opsForValue().get("write-value");
            Thread.sleep(30000);
        }catch(Exception e) {
            e.printStackTrace();
        }finally {
            readLock.unlock();
            System.out.println("释放读锁成功..." + Thread.currentThread().getId());
        }
        return s;
    }

    // 需要提前在redis中创建3个车位
    @GetMapping("/park")
    public String park() throws InterruptedException {
        RSemaphore park = redissonClient.getSemaphore("park");
        park.acquire();
        return "park";
    }

    @GetMapping("/unpark")
    public String unpark() throws InterruptedException {
        RSemaphore park = redissonClient.getSemaphore("park");
        park.release();
        return "unpark";
    }

    @GetMapping("/door")
    public String door() throws InterruptedException {
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        door.trySetCount(3);
        door.await();
        return "放假了...";
    }

    @GetMapping("/go/{id}")
    public String go(@PathVariable Long id) {
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        door.countDown();
        return id + "班的人都走了...";
    }
}
