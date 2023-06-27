package com.smsoft.stock.repository;

import com.smsoft.stock.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/* named lock은 실무에서는 별도의 JDBC사용해야함 -> 커넥션풀 부족으로 다른 서비스에 영향을 줄 수 있음 */
public interface LockRepository extends JpaRepository<Stock, Long> {
    @Query(value = "select get_lock(:key, 3000)", nativeQuery = true)
    void getLock(String key);

    @Query(value = "select release_lock(:key)", nativeQuery = true)
    void releaseLock(String key);
}
