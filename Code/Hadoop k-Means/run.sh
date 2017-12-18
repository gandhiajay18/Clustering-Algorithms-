#!/bin/bash

hdfs dfs -rm -r ~/inputKMEANS/
# clear
hdfs dfs -mkdir -p ~/inputKMEANS/
# clear
hdfs dfs -put ./input/ ~/inputKMEANS/

if [ -z "$1" ]
then
        #hadoop jar kmeans.jar hadoopkmeans ~/inputKMEANS/input/cho.txt ~/output 5
	#hadoop jar kmeans.jar hadoopkmeans ~/inputKMEANS/input/iyer.txt ~/output 10
	hadoop jar kmeans.jar hadoopkmeans ~/inputKMEANS/input/newdata1.txt ~/output 3
else
 	#hadoop jar kmeans.jar hadoopkmeans ~/inputKMEANS/input/cho.txt ~/output "$1"
  	#hadoop jar kmeans.jar hadoopkmeans ~/inputKMEANS/input/iyer.txt ~/output "$1"
 	hadoop jar kmeans.jar hadoopkmeans ~/inputKMEANS/input/newdata1.txt ~/output "$1"
fi

hdfs dfs -rm -r ~/inputKMEANS/
# clear