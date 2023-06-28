# 🎁 재고 시스템으로 구현하는 동시성 이슈 문제 해결

## 요구사항

선착순 `100명`에게 할인쿠폰을 제공하는 이벤트를 진행하고자 한다.

- 선착순 `100명`에게만 지급되어야 한다.
- `101개` 이상이 지급되면 안된다.
- 순각적으로 몰리는 트래픽을 버틸 수 있어야 한다.

<br>

## 퀵 스타트

### 도커 설치

```shell
brew install docker
brew link docker
docker version
```

<br>

### 도커 MySQL 실행 명령어

```shell
docker pull mysql
docker run -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=1234 --name mysql mysql 
docker ps

-- mysql 접속
docker exec -it mysql bash
```