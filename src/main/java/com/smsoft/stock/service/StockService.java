package com.smsoft.stock.service;

import com.smsoft.stock.domain.Stock;
import com.smsoft.stock.repository.StockRepository;
import org.springframework.stereotype.Service;

@Service
public class StockService {
    private StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    /* decrease 펑션 로직
       1. stock 객체 전체 가져와서 quantity값 뽑아내기
       2. 재고감소시키기
       3. 저장
    */
    public void decrease(Long id, Long quantity){
        Stock stock = stockRepository.findById(id).orElseThrow();
        stock.decrease(quantity);

        stockRepository.saveAndFlush(stock);
    }
}
