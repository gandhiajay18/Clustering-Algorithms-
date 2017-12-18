Hierarchical Agglomerative Clustering (HAC) :

How to run:

1. To run HAC, open the command prompt in the folder containing the hac.jar file along with the input data files.
2. Type in the following command (without quotes):
    
    "java -jar hac.jar"

3. The prompt would ask you to "Enter Input Filename",  type in the input filename and press enter. (NOTE: It is important that the file is present in the local folder)

   For example, if the file you want to run HAC on is "cho.txt", then you need to type in cho.txt and press enter

4. The prompt would then ask you the number of clusters you want. Type in a positive Integer value and press enter.

The resultant clusters, their components, the Jaccard co-efficient, Rand Index, runtime and number of iterations taken will be displayed.

The result would also be output to a file in the local folder, filename_HAC_OUT.txt, and another file, filename_HAC_PCA.txt which would act as input
labels for the PCA visualization function (given in Python in the parent folder)