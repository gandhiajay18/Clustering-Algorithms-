Hadoop k-Means:

Requirements:

Hadoop
Java 7 or higher
Linux (Ubuntu or other versions)

TO RUN:

1. To run Hadoop k-Means, open terminal in the folder containing the kmeans.jar file.
2. Give permission to run the "run.sh" script by giving the command "chmod +x run.sh"
3. Type in the command "./run.sh" to run the given script on the newDataset. You can also specify number of clusters (optional), by typing "./run.sh 5", if you want 5 clusters. The output will be present in the local folder as newdata1_hadoop_out.txt
4. You can also run for other input files by modifying the shell script file and specifying the filename of the dataset, but it is crucial that the dataset file be also present in the "input" folder in the current directory, and also on the HDFS.
5. To run on another data set, for example cho.txt, first put cho.txt in the local "input" folder, then use hdfs dfs -put command to copy the folder to the HDFS,
and then run the following command

hadoop jar kmeans.jar hadoopkmeans ~/your/hadoopinput/filename.txt ~/youroutputfolder [number of clusters(default 5)]

You will be able to see the output file in the local folder, as filename_hadoop_out.txt, and another file, filename_hadoop_pca.txt, which can be used as the input for the visualization script, given in Python in a parent folder.



