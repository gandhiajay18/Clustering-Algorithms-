import numpy as np
import re
import matplotlib.pyplot as plt
import os
from sklearn.decomposition import PCA

#PLOT Implementation
class Plot(object):
    def __init__(self):
        self.colors = {-1: "orange", 1: "red", 2: "blue", 3: "green", 4: "brown", 5: "cyan", 6: "magenta", 7: "pink",8:"gray",9:"black",10:"purple",11:"olive",12:"yellow"}
        self.name = ""
        self.labels = []

    def plot(self,data,labels,name1,name2):
        print("Plotting: "+name1+" "+name2)
        fig = plt.figure(figsize=(20,10))
        ax = fig.add_subplot(1, 1, 1)
        xlabels = []
        for i in range(0,len(data)):
            if not labels[i] in xlabels:
                ax.scatter(data[i][0],data[i][1],alpha = 0.8, c = self.colors.get((labels[i])), edgecolors = 'none',s = 30, label = labels[i])
                xlabels.append(labels[i])
            else:
                ax.scatter(data[i][0], data[i][1], alpha=0.8, c=self.colors.get((labels[i])), edgecolors='none', s=30)

        plt.title(name2+"  ("+name1+")")
        plt.legend(loc='upper center', bbox_to_anchor=(0.5, -0.05),fancybox=True, shadow=True, ncol=14)
        fname = "visualizations/"+name1+"_"+name2+".jpg"
        plt.savefig(fname)

dict1 = {}
directory = os.path.normpath("input")
for subdir, dirs, files in os.walk(directory):
    for filename in files:
        if filename.endswith(".txt"):
            #Reads Dimensions
            num_rows = sum(1 for line in open("input/"+filename))
            with open("input/"+filename) as f:
                for i in f:
                    n = re.split(r'\t+', i)
                    cols = len(n) - 2
                    break


            #READS DATA
            data = np.zeros((num_rows,cols))
            with open("input/"+filename) as f:
                count = 0
                for i in f:
                    n = re.split(r'\t+', i)
                    data[count] = list(map(float,n[2:]))

                    count += 1

            pca = PCA(n_components=2)
            reduced_data = pca.fit_transform(data)
            print("Reducing Dimensions: "+filename.split('.')[0])
            dict1[filename.split('.')[0]] = reduced_data


directory = os.path.normpath("")
rootDir = ""
for subdir, dirs, files in os.walk(directory):
    for filename in files:
        relDir = os.path.relpath(subdir,rootDir)
        relFile = os.path.join(relDir,filename)
        if filename.endswith("pca.txt") or filename.endswith("PCA.txt"):
            n = filename.split("_")
            fname = n[0]
            alg = n[1]
            ob = Plot()
            ob.name = alg

            #READS DATA
            labels = []
            with open(relFile) as f:
                for i in f:
                    i = i.rstrip()
                    labels.append(int(i))



            ob.plot(dict1.get(fname),labels,fname,alg)
print("Done.")




