# Korean Jaso Analyzer for Elasticsearch 9.0.3 
  (자동완성 플러그인)

> 🔗 **Original Project**: [netcrazy/elasticsearch-jaso-analyzer](https://github.com/netcrazy/elasticsearch-jaso-analyzer)  
> 📝 **Original Author**: Choi ilkyu (최일규)  
> 🔄 **This Fork**: Updated for Elasticsearch 9.0.3 compatibility with Java 21+ support

## Build & Packaging

###### 터미널 환경에서 자바 버전은 21 이상이어야 합니다 (Java 23에서도 호환).
~~~shell
$ ./gradlew clean build buildPluginZip
~~~

###### 자동완성용 한글 자소분석기입니다. elasticsearch 9.0.3 에서 테스트 되었습니다

## 설치 방법

### 1. 직접 설치 (로컬 파일)
```bash
# 플러그인 ZIP 파일을 다운로드 후
/usr/share/elasticsearch/bin/elasticsearch-plugin install file:///path/to/jaso-analyzer-plugin-9.0.3-plugin.zip
```

### 2. GitHub Releases에서 설치
```bash
# GitHub Releases에서 직접 설치 (릴리스 후 사용 가능)
/usr/share/elasticsearch/bin/elasticsearch-plugin install https://github.com/YOUR_USERNAME/elasticsearch-jaso-analyzer/releases/download/v9.0.3/jaso-analyzer-plugin-9.0.3-plugin.zip
```

## 도커 컨테이너에서 elasticsearch, kibana 설치/실행
```
#플러그인이 자동으로 설치된다.
cd docker
docker-compose up -d
```

## 직접설치

###### *설치*
```
bin/elasticsearch-plugin install https://github.com/netcrazy/elasticsearch-jaso-analyzer/releases/download/v8.6.2/jaso-analyzer-plugin-8.6.2-plugin.zip
```

###### *삭제 (필요시)*
```
bin/elasticsearch-plugin remove jaso-analyzer
```

###### *인덱스 삭제 (필요시)*
```
curl -XDELETE 'http://localhost:9200/jaso'
```

###### *Korean Jaso Analyer 설정 및 인덱스 생성 (기본 자소검색용)*
```
curl -XPUT -H 'Content-Type: application/json' localhost:9200/jaso -d '{
  "settings": {
    "index": {
      "analysis": {
        "filter": {
          "suggest_filter": {
            "type": "edge_ngram",
            "min_gram": 1,
            "max_gram": 50
          }
        },
        "analyzer": {
          "suggest_search_analyzer": {
            "type": "custom",
            "tokenizer": "jaso_tokenizer"
          },
          "suggest_index_analyzer": {
            "type": "custom",
            "tokenizer": "jaso_tokenizer",
            "filter": [
              "suggest_filter"
            ]
          }
        }
      }
    }
  }
}'
```

###### *Korean Jaso Analyer 설정 및 인덱스 생성 (한,영오타 및 초성토큰 추출이 필요할 때..)*
```
curl -XPUT -H 'Content-Type: application/json' http://localhost:9200/jaso/ -d '{
  "settings": {
    "index": {
      "analysis": {
        "filter": {
          "suggest_filter": {
            "type": "edge_ngram",
            "min_gram": 1,
            "max_gram": 50
          }
        },
        "tokenizer": {
          "jaso_search_tokenizer": {
            "type": "jaso_tokenizer",
            "mistype": true,
            "chosung": false
          },
          "jaso_index_tokenizer": {
            "type": "jaso_tokenizer",
            "mistype": true,
            "chosung": true
          }
        },
        "analyzer": {
          "suggest_search_analyzer": {
            "type": "custom",
            "tokenizer": "jaso_search_tokenizer"
          },
          "suggest_index_analyzer": {
            "type": "custom",
            "tokenizer": "jaso_index_tokenizer",
            "filter": [
              "suggest_filter"
            ]
          }
        }
      }
    }
  }
}'
```

###### *인덱스 맵핑*
```
curl -XPUT -H 'Content-Type: application/json' http://localhost:9200/jaso/_mapping -d '{
  "properties": {
    "name": {
      "type": "text",
      "store": true,
      "analyzer": "suggest_index_analyzer",
      "search_analyzer": "suggest_search_analyzer"
    }
  }
}'
```


###### *인덱스타임 분석기 테스트*
```
curl -XPOST -H 'Content-Type: application/json' http://localhost:9200/jaso/_analyze?pretty=true -d '{
    "analyzer" : "suggest_index_analyzer",
    "text" : "최일규 Hello"
}'
```


###### *쿼리타임 분석기 테스트*
```
curl -XPOST -H 'Content-Type: application/json' http://localhost:9200/jaso/_analyze?pretty=true -d '{
    "analyzer" : "suggest_search_analyzer",
    "text" : "쵱"
}'
```


###### *문서생성*
```
curl -XPOST -H 'Content-Type: application/json' http://localhost:9200/jaso/_doc?pretty=true -d '{
    "name":"최일규 Hello"
}'

curl -XPOST -H 'Content-Type: application/json' http://localhost:9200/jaso/_doc?pretty=true -d '{
    "name":"초아"
}'
```

###### *문서검색*
```
curl -XPOST -H 'Content-Type: application/json' http://localhost:9200/jaso/_search?pretty=true -d '{
    "query" : {
        "match" : { "name" : "초" }
    }
}'

curl -XPOST -H 'Content-Type: application/json' http://localhost:9200/jaso/_search?pretty=true -d '{
    "query" : {
        "match" : { "name" : "ㅊㅇㄱ" }
    }
}'
```

## 테스트

### 단위 테스트 실행

```bash
# 모든 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests "org.elasticsearch.analysis.JasoTest"
./gradlew test --tests "org.elasticsearch.analysis.JasoTokenizerTest"

# 테스트 리포트 확인
open build/reports/tests/test/index.html
```

### 테스트 커버리지

현재 다음 기능들이 테스트됩니다:
- ✅ 자소 분해 기능 (`JasoTest`)
- ✅ 토크나이저 기능 (`JasoTokenizerTest`)
- ✅ 한영 오타 처리
- ✅ 초성 검색 토큰 생성
- ✅ 공백 및 특수문자 처리

### 자동화된 테스트

- **CI/CD**: 푸시/PR 시 자동 테스트 실행 (Java 21, 23)
- **릴리스**: 릴리스 전 자동 테스트 검증
- **커버리지**: 테스트 결과 리포트 자동 생성
## 개발자를 위한 배포 가이드

### 1. GitHub에 코드 푸시 및 릴리스 생성

```bash
# 코드 커밋 및 푸시
git add .
git commit -m "Update to Elasticsearch 9.0.3"
git push origin main

# 태그 생성 및 푸시 (자동 릴리스 트리거)
git tag v9.0.3
git push origin v9.0.3
```

### 2. 수동 릴리스 생성 (GitHub Actions 없는 경우)

1. GitHub 저장소에서 "Releases" 페이지로 이동
2. "Create a new release" 클릭
3. 태그: `v9.0.3`, 제목: `Elasticsearch 9.0.3 지원`
4. `build/distributions/jaso-analyzer-plugin-9.0.3-plugin.zip` 파일 업로드
5. "Publish release" 클릭

### 3. 사용자 설치 방법

```bash
# GitHub Releases에서 설치
/usr/share/elasticsearch/bin/elasticsearch-plugin install https://github.com/YOUR_USERNAME/elasticsearch-jaso-analyzer/releases/download/v9.0.3/jaso-analyzer-plugin-9.0.3-plugin.zip

# 설치 확인
/usr/share/elasticsearch/bin/elasticsearch-plugin list

# Elasticsearch 재시작 필요
sudo systemctl restart elasticsearch
```

### 4. Docker 환경에서 사용

```bash
cd docker
docker-compose up -d

# 플러그인 설치 확인
curl -X GET "localhost:9200/_nodes/plugins?pretty"
```

### 5. 플러그인 제거 (필요시)

```bash
/usr/share/elasticsearch/bin/elasticsearch-plugin remove jaso-analyzer
sudo systemctl restart elasticsearch
```

## 사용법

## 라이센스 및 크레딧

이 프로젝트는 [MIT 라이센스](LICENSE) 하에 배포됩니다.

### 원본 프로젝트
- **저장소**: [netcrazy/elasticsearch-jaso-analyzer](https://github.com/netcrazy/elasticsearch-jaso-analyzer)
- **원 저작자**: Choi ilkyu (최일규)
- **최초 버전**: Elasticsearch 8.6.2 지원

### 이 Fork의 기여사항
- Elasticsearch 9.0.3 호환성 업데이트
- Java 21+ 지원 (Java 23에서 테스트됨)
- GitHub Actions CI/CD 추가
- 자동화된 릴리스 프로세스
- 향상된 테스트 커버리지

### 기여하기
원본 프로젝트와 이 Fork 모두에 기여를 환영합니다. Pull Request를 보내주세요!
