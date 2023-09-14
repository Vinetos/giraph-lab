# Creates pseudo distributed hadoop 2.4 running giraph
#
# docker build -t uwsampa/giraph .
FROM uwsampa/giraph-docker

MAINTAINER Vinetos

# Copy files
COPY run.sh /run.sh
RUN chmod a+x /run.sh
COPY graph.txt /graph.txt
RUN chmod 777 /graph.txt

COPY src/ /giraph-work
RUN cd /giraph-work && \
    javac -cp /usr/local/giraph/giraph-examples/target/giraph-examples-1.1.0-SNAPSHOT-for-hadoop-2.4.1-jar-with-dependencies.jar:$($HADOOP_HOME/bin/hadoop classpath) example/*.java && \
    jar uf /usr/local/giraph/giraph-examples/target/giraph-examples-1.1.0-SNAPSHOT-for-hadoop-2.4.1-jar-with-dependencies.jar example && \
    cd ..

# default command
CMD ["/etc/giraph-bootstrap.sh", "-d"]
