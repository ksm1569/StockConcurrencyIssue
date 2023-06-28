# 동시성 이슈에 대한 고찰과 해결방법
### ✨ 개발 목적 및 개요
여태까지 개발을 할때 다른회사에서는 동시성이슈에 대해서

어떤 방법으로 해결을 할까 궁금증은 있었으나 찾아보거나 하진 않았던 것 같다.

동시성 관련 강의에 도움을 받아서, 내용을 이해하고 까먹을 때를 대비하여 정리 해놓으려는 목적이다. 

- Race Condition에 대한 정확한 이해
- Race Condition을 해결 하기 위한 다양한 방법 고찰

  - Mysql의 Lock과 예제를 통한 장점, 단점, 한계 이해와 인지
  - Lettuce과 Redisson 라이브러리로 해결하는 기법
  - 위의 여러가지 방법을 통해 회사 상황에 맞는 방법을 선택하는 판단력 기르기가 목표
  
- 다중 스레드사용과 테스트코드 작성 


### 🔨 사용한 프레임웍, 라이브러리 및 툴
- IntelliJ Ultimate
- Docker
- JAVA 11
- Spring Boot
- MySQL
- Redis


### ✍ Flow
- 재고관리 테이블 생성(stock_example), 데이터 입력, 재고감소 로직 추가, junit으로 재고감소 로직 테스트완료
- 다중스레드를 발생시켜(100개) 동시성 테스트
- 자바의 synchronized 사용해보기

  서버가 여러대 일 경우 문제 발생, Transactional 특성상 충돌 인식
- Mysql로 해결
  
  - Pessimistic Lock
  
    테이블과 데이터에 락을 걸어서 정합성을 맞추는 방법 - 데드락 주의 필요
  - Optimistic Lock
  
    버전컬럼을 이용해서 update 하기전에 버전체크를 한다
  - Named Lock - 이름을 가진 metadata Locking 방법
  
    이름을 가진 락을 획득한 후 해제할때까지 다른 세션은 이 lock 을 획득할 수 없도록 한다 (별도 해제필요)
 
- 라이브러리로 해결

  - Lettuce
  
    setnx (set if not exist) - key value 값을 set할때, 기존의 값이 없을때만 set한다
    
    spin lock 방식 - 락 획득 할때 까지 재시도 하는 로직(retry)을 개발자가 직접구현해야함
  - Redisson
    
    pub-sub 기반 lock제공 - 별도 라이브러리가 설치가 필요함

    채널을 하나 두어서 스레드가 락종료시 알려준다

  -> 재시도가 필요하지 않은 lock 은 lettuce 활용

  -> 재시도가 필요한 경우에는 redisson 를 활용



### 👊 Mysql vs Redis

- Mysql
  - 이미 Mysql 을 사용하고 있다면 별도의 비용없이 사용가능하다.
  - 어느정도의 트래픽까지는 문제없이 활용이 가능하다.
  - Redis 보다는 성능이 좋지않다.

- Redis
  - 활용중인 Redis 가 없다면 별도의 구축비용과 인프라 관리비용이 발생한다.
  - Mysql 보다 성능이 좋다.

### 📸 테스트 스크린샷

  <details>
  <summary>접기/펼치기 버튼</summary>
  <div markdown="1">
  
  <img width="600" src="https://github.com/ksm1569/DevBlog/assets/34292113/9c9ab9a0-4670-416e-84e0-fee5257bc625">
  
  <img width="600" src="https://github.com/ksm1569/DevBlog/assets/34292113/1e5e39f6-6b19-45cd-9270-3437eb396308">
  
  <img width="600" src="https://github.com/ksm1569/DevBlog/assets/34292113/17071503-2671-4c34-9688-1fd410b0a210">
  
  </div>
  </details>


### 📝 각종 셋팅 메모
  <details>
  <summary>접기/펼치기 버튼</summary>
  <div markdown="1">

  ```Shell
    #mysql
  
    docker pull mysql
    docker run -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=1234 --name mysql mysql
    docker ps
  ```

  ```Shell
    #mysql bash로 진입하여 데이터베이스 생성
  
    docker exec -it mysql bash
    mysql -u root -p
    password 입력
    create database stock_example
    use stock_example
  ```
  
  ```Shell
    #redis
  
    docker pull redis
  
    docker run --name myredis -d -p 6379:6379 redis
  ```
  
  ```Shell
    #터미널 테스트
  
     1. docker ps 로 redis 컨테이너명 가져온다음
     2. docker exec -it 컨테이너명 redis-cli 입력하면 내부로 진입가능
     3. 내부에서 채널1 구독해본다. subscribe ch1
     4. 터미널 하나 더 열어서 publish ch1 hi 라는 메세지를 보내본다
  ```
  
  </div>
  </details>

### ✌ 마치며
Race Condition의 다양한 해결방법을 공부할 수 있었으며,

여러가지 상황에서 어떤 해결법을 써야 효율적인지 알게 되었다.

앞으로도 더 좋은 방법들이 나올 것이므로, 꾸준히 주위를 둘러보고 공부해야겠다. 


