# Korean Jaso Analyzer for elasticsearch 2.3.x

## ES Plugin change

>* AbstractPlugin => Plugin
>* [plugin-descriptor.properties]
(https://www.elastic.co/guide/en/elasticsearch/plugins/current/plugin-authors.html#plugin-authors) 프로퍼타 파일 형식 변경

*[https://github.com/imotov/elasticsearch-native-script-example](https://github.com/imotov/elasticsearch-native-script-example)
 
## changed

* ES 1.7x -> 2.3.1

## install

~~~shell
$ gradle build buildPluginZip
~~~

###### 자동완성용 한글 자소분석기입니다. elasticsearch 2.3.1 에서 테스트 되었습니다

###### *설치*
```
sudo ./bin/plugin install http://nonstop.pe.kr/elasticsearch/jaso-analyzer-plugin-1.0.2-plugin.zip
```

###### *삭제 (필요시)*
```
sudo bin/plugin remove jaso-analyzer
```

###### *인덱스 삭제 (필요시)*
```
curl -XDELETE 'http://localhost:9200/jaso'
```

###### *Korean Jaso Analyer 설정 및 인덱스 생성 (기본 자소검색용)*
```
curl -XPUT localhost:9200/jaso/ -d '{
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
curl -XPUT localhost:9200/jaso/ -d '{
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
curl -XPUT 'http://localhost:9200/jaso/_mapping/test' -d '{
  "properties": {
    "name": {
      "type": "string",
      "store": true,
      "index_analyzer": "suggest_index_analyzer",
      "search_analyzer": "suggest_search_analyzer"
    }
  }
}'
```


###### *인덱스타임 분석기 테스트*
```
curl -XGET 'localhost:9200/jaso/_analyze?analyzer=suggest_index_analyzer&pretty' -d '최일규 Hello'

```


###### *쿼리타임 분석기 테스트*
```
curl -XGET 'localhost:9200/jaso/_analyze?analyzer=suggest_search_analyzer&pretty' -d '최일규 Hello'
```


###### *문서생성*
```
curl -XPOST http://localhost:9200/jaso/test/ -d '{
    "name":"최일규 Hello"
}'

curl -XPOST http://localhost:9200/jaso/test/ -d '{
    "name":"초아"
}'
```

###### *문서검색*
```
curl -XPOST 'http://localhost:9200/jaso/test/_search?pretty' -d '{
    "query" : {
        "match" : { "name" : "초" }
    }
}'

curl -XPOST 'http://localhost:9200/jaso/test/_search?pretty' -d '{
    "query" : {
        "match" : { "name" : "ㅊㅇㄱ" }
    }
}'
```
