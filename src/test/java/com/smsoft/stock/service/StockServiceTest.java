package com.smsoft.stock.service;


import com.smsoft.stock.domain.Stock;
import com.smsoft.stock.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class StockServiceTest {
    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    public void before(){
        Stock stock = new Stock(1L, 100L);

        stockRepository.saveAndFlush(stock);
    }

    @AfterEach
    public void after(){
        stockRepository.deleteAll();
    }

    @Test
    public void stock_decrese(){
        stockService.decrease(1L, 1L);

        // stcok 100 -> -1 -> 99
        Stock stock = stockRepository.findById(1L).orElseThrow();

        assertEquals(99, stock.getQuantity());
    }

    @Test
    public void 동시에_100번_요청() throws InterruptedException {
        int threadCount = 100;

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for(int i=0; i < threadCount; i++){
            executorService.submit(()->{
                try{
                    stockService.decrease(1L, 1L);
                }finally {
                    latch.countDown();
                }

            });
        }

        latch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow();

        // stcok 100 -> -(1*100) -> 0
        assertEquals(0L, stock.getQuantity());

    }
}