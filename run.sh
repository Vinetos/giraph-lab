#!/bin/bash

# FILE=example.DummyComputation (for debug only, should be set in docker-compose.yml)

set -e # Exit if any command fails

/etc/bootstrap.sh

echo "Starting Zookeeper...."
"$ZOOKEEPER_PREFIX"/bin/zkServer.sh start


echo "Copying input data to HDFS..."
cd "$GIRAPH_HOME"
until "$HADOOP_HOME"/bin/hdfs dfs -put /graph.txt /user/root/input/tiny-graph.txt
do
  echo "Retrying to copy input data to HDFS..."
  sleep 1
done

echo "Running Giraph ($FILE)..."
"$HADOOP_HOME"/bin/hadoop jar "$GIRAPH_HOME"/giraph-examples/target/giraph-examples-1.1.0-SNAPSHOT-for-hadoop-2.4.1-jar-with-dependencies.jar org.apache.giraph.GiraphRunner "$FILE" --yarnjars giraph-examples-1.1.0-SNAPSHOT-for-hadoop-2.4.1-jar-with-dependencies.jar --workers 1 --vertexInputFormat org.apache.giraph.io.formats.JsonLongDoubleFloatDoubleVertexInputFormat --vertexInputPath /user/root/input/tiny-graph.txt -vertexOutputFormat org.apache.giraph.io.formats.IdWithValueTextOutputFormat --outputPath /user/root/output

echo "======================================"
"$HADOOP_HOME"/bin/hdfs dfs -cat /user/root/output/part-m-00001