K-Means :

How to run:

1. To run K-Means, open the command prompt in the folder containing the kmeans.jar file along with the input data files.
2. Type in the following command (without quotes):
    
    "java -jar kmeans.jar"

3. The prompt would ask you to "Enter Input Filename",  type in the input filename and press enter. (NOTE: It is important that the file is present in the local input folder)

   For example, if the file you want to run Kmeans on is "cho.txt", then you need to type in cho.txt and press enter

4. The prompt would then ask you the number of clusters you want. Type in a positive Integer value and press enter.

5. Then the prompt would ask you to enter the actual values of initial clusters. Type in all clusters between the given range and press enter.

6. The Jaccard co-efficient, Rand Index, runtime and number of iterations taken will be displayed.

The result containing the clusters (line number and finally belonging to cluster number) would also be output to a file in the local folder, filename_KMEANS_OUT.txt, and another file, filename_KMEANS_PCA.txt which would act as input labels for the PCA visualization function (given in Python in the parent folder)