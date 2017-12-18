import numpy as np
import re
import matplotlib.pyplot as plt
import os
from sklearn.cluster import DBSCAN


class Clustering(object):
    def __init__(self):
        self.name = ""
        self.cols = 0
        self.labels = []
        self.cluster_count = 0
        self.name = ""
        self.visited = []
        self.ground_truth = []

    def metrics(self,data):
        m11, m10, m01, m00 = 0.0, 0.0, 0.0, 0.0
        for i in range(len(data)):
            for j in range(len(data)):
                #if i == j:
                 #   continue
                if self.ground_truth[i] == self.ground_truth[j] and self.labels[i] == self.labels[j]:
                    m11 += 1.0
                elif self.ground_truth[i] != self.ground_truth[j] and self.labels[i] == self.labels[j]:
                    m10 += 1.0
                elif self.ground_truth[i] == self.ground_truth[j] and self.labels[i] != self.labels[j]:
                    m01 += 1.0
                elif self.ground_truth[i] != self.ground_truth[j] and self.labels[i] != self.labels[j]:
                    m00 += 1.0

        rand = float((m11 + m00)/(m00+m01+m10+m11))
        jaccard = float(m11/(m11+m10+m01))

        return rand, jaccard


    def plot(self, data, labels,name1, name2):
        fig = plt.figure(figsize=(10, 10))
        ax = fig.add_subplot(1, 1, 1, facecolor="1.0")
        xlabels = []
        for i in range(0, len(data)):
            if not labels[i] in xlabels:
                ax.scatter(data[i][0], data[i][1], alpha=0.8, c=self.colors.get((labels[i])),
                           edgecolors='none', s=30, label=labels[i])
                xlabels.append(labels[i])
            else:
                ax.scatter(data[i][0], data[i][1], alpha=0.8, c=self.colors.get((labels[i])),
                           edgecolors='none', s=30)

        plt.title(name2 + "  (" + name1 + ")")
        plt.legend(loc='upper center', bbox_to_anchor=(0.5, -0.05), fancybox=True, shadow=True, ncol=5)
        fname = "output/" + name1 + "_" + name2 + ".jpg"
        plt.savefig(fname)

    def dist(self, p, q):
        return np.linalg.norm(p - q)

    def region_query(self, point, data, eps):
        points = []
        for i in range(len(data)):
            dis = self.dist(point, data[i])
            if dis <= eps:
                points.append(i)

        return points

    def expand_cluster(self, data, points, eps, minpts, i):
        self.labels[i] = self.cluster_count
        for index in points:
            if self.visited[index] == False:
                self.visited[index] = True
                points2 = self.region_query(data[index], data, eps)
                if len(points2) >= minpts:
                    points += points2  # check for errors
            if self.labels[index] == 0 or self.labels[index] == -1:
                self.labels[index] = self.cluster_count

    def dbscan(self, data, eps, minpts):
        for i in range(len(data)):
            if self.visited[i] == False:
                curr = data[i]
                self.visited[i] = True
                points = self.region_query(curr, data, eps)
                if len(points) < minpts:
                    self.labels[i] = -1
                else:
                    self.cluster_count += 1
                    self.expand_cluster(data, points, eps, minpts, i)


        f = open("labels/"+self.name.split('.')[0]+"_"+"dbscan_PCA.txt",'a')
        for i in self.labels:
            f.write(str(i)+"\n")
        f.close()


eps = float(input("Enter Epsilon Value: ") or "1.03")
minpts = int(input("Enter Minimum Number of Neighbors: ") or "4")
directory = os.path.normpath("input")
for subdir, dirs, files in os.walk(directory):
    for filename in files:
        if filename.endswith(".txt"):
            ob = Clustering()
            ob.name = filename
            #Reads Dimensions
            num_rows = sum(1 for line in open("input/"+filename))
            with open("input/"+filename) as f:
                for i in f:
                    n = re.split(r'\t+', i)
                    ob.cols = len(n) - 2
                    break


            #READS DATA
            data = np.zeros((num_rows, ob.cols))
            with open("input/"+filename) as f:
                count = 0
                clabel = 0
                for i in f:
                    n = re.split(r'\t+', i)
                    data[count] = list(map(float,n[2:]))
                    ob.labels.append(0)
                    ob.visited.append(False)
                    ob.ground_truth.append(int(n[1]))
                    count += 1

            ob.dbscan(data,eps,minpts)
            print(ob.name)
            print("Number of Clusters: "+str(ob.cluster_count))
            rand, jaccard = ob.metrics(data)
            print("Rand: "+str(rand))
            print("Jaccard: "+str(jaccard))
            print("\n")
			
            g = open("output/"+ob.name.split('.')[0]+"_"+"dbscan_output.txt",'a')
            for i in range(1,len(data)+1):
                g.write(str(i)+"  "+str(ob.labels[i-1])+"\n")
            g.close()


