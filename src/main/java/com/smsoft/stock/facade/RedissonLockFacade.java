package com.smsoft.stock.facade;

import com.smsoft.stock.service.StockService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedissonLockFacade {
    private RedissonClient redissonClient;

    private StockService stockService;

    public RedissonLockFacade(RedissonClient redissonClient, StockService stockService) {
        this.redissonClient = redissonClient;
        this.stockService = stockService;
    }

    public void decrease(Long key, Long quantity){
        RLock lock = redissonClient.getLock(key.toString());

        try{
            //1번 파라미터 : 몇초간 시도 할 것인지
            //2번 파라미터 : 몇초간 점유 할 것인
            boolean available = lock.tryLock(5, 1, TimeUnit.SECONDS);

            if(!available){
                System.out.printf("락 획득 실패");
                return;
            }

            stockService.decrease(key, quantity);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
