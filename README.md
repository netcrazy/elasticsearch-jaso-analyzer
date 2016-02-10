# EJ Analyzer (elasticsearch-jaso-analyzer)

###### 자동완성용 한글 자소분석기입니다. elasticsearch 1.7.x 에서 테스트 되었습니다

###### 설치

```
bin/plugin --url http://nonstop.pe.kr/elasticsearch/elasticsearch-jaso-analyzer-1.0.0.zip --install jaso-analyzer
```

###### jaso Analyer 설정 및 인덱스 생성
```
curl -XPUT localhost:9200/jaso/ -d '{
  "settings": {
    "index": {
      "analysis": {
        "filter": {
          "autocomplete_filter": {
            "type": "edge_ngram",
            "min_gram": 1,
            "max_gram": 50
          }
        },
        "analyzer": {
          "jaso_search": {
            "type": "custom",
            "tokenizer": "jaso_tokenizer",
            "filter": [
              "lowercase"
            ]
          },
          "jaso_index": {
            "type": "custom",
            "tokenizer": "jaso_tokenizer",
            "filter": [
              "lowercase",
              "autocomplete_filter"
            ]
          }
        }
      }
    }
  }
}'

###### 인덱스 맵핑
```
curl -XPUT 'http://localhost:9200/jaso/_mapping/test' -d '{
  "test": {
    "properties": {
      "name": {
        "type": "string",
        "store": true,
        "index_analyzer": "jaso_index",
        "search_analyzer": "jaso_search"
      }
    }
  }
}'
```

###### 인덱스타임 분석기 테스트
```
curl -XGET 'localhost:9200/jaso/_analyze?analyzer=jaso_search&pretty' -d '최일규 Hello'

```

###### 쿼리타임 분석기 테스트
```
curl -XGET 'localhost:9200/jaso/_analyze?analyzer=jaso_index&pretty' -d '최일규 Hello'
```


###### 문서생성
```
curl -XPOST http://localhost:9200/jaso/test/ -d '{
    "name":"최일규 Hello"
}'

curl -XPOST http://localhost:9200/jaso/test/ -d '{
    "name":"초아"
}'
```

###### 문서검색
```
curl -XPOST 'http://localhost:9200/jaso/test/_search?pretty' -d '{
    "query" : {
        "match" : { "name" : "총" }
    }
}'
```