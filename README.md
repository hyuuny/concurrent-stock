# 🎁 재고 시스템으로 구현하는 동시성 이슈 문제 해결

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