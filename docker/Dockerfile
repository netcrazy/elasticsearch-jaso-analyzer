FROM docker.elastic.co/elasticsearch/elasticsearch:9.0.3

COPY jaso-analyzer-plugin-9.0.3-plugin.zip /tmp/
RUN /usr/share/elasticsearch/bin/elasticsearch-plugin install file:///tmp/jaso-analyzer-plugin-9.0.3-plugin.zip