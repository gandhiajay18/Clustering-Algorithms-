import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class HAC {

 static List < String > rawData;
 static Map < Integer, List < Double >> data = new HashMap < > ();
 static Map < Integer, List < Integer >> clustMap = new HashMap < > ();

 public static void main(String[] args) {
  // TODO Auto-generated method stub
//Take in input from user for input file and number of clusters
  String inFile = "";
  int k = 0;
  Scanner s = new Scanner(System.in);
  System.out.println("Please Enter Input File Name: ");
  inFile = s.nextLine();
  System.out.println("Please Enter Number of Clusters: ");
  k = s.nextInt();
  long startTime = System.currentTimeMillis();

//Start HAC
  runHeirch(inFile, k);
  long endTime = System.currentTimeMillis();
  long totalTime = endTime - startTime;
  System.out.println("Total time taken is " + (totalTime) + "ms");

 }

 public static void runHeirch(String inFile, int k) {
  // TODO Auto-generated method stub
  List < Integer > gTruth = new ArrayList < > ();
//Read input data into rawData
  try {
   rawData = Files.readAllLines(Paths.get(inFile), StandardCharsets.UTF_8);

   for (String s: rawData) {
    String[] row = s.split("\t");
    int key = Integer.parseInt(row[0]);
    List < Double > li = new ArrayList < > ();
    gTruth.add(Integer.parseInt(row[1]));
    for (int i = 2; i < row.length; i++) {
     li.add(Double.valueOf(row[i]));
    }
    data.put(key, li);
    List < Integer > curr = new ArrayList < > ();
    curr.add(key);
    clustMap.put(key, curr);
   }

  } catch (IOException e) {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }

  //Computer distance matrix distMat[], and add values for the closest clusters for
  //each row in closest[]
  double[][] distMat = new double[data.size() + 1][data.size() + 1];

  int closest[] = new int[data.size() + 1];

  for (int i = 1; i <= data.size(); i++) {

   List < Double > c1 = data.get(i);
   double min = Double.MAX_VALUE;
   int minInd = -1;
   for (int j = 1; j <= data.size(); j++) {
    if (i == j) {
     distMat[i][j] = Double.MAX_VALUE;
     continue;
    }

    List < Double > c2 = data.get(j);
    double dist = getDist(c1, c2);
    distMat[i][j] = dist;
    if (dist < min) {
     min = dist;
     minInd = j;
    }
    // String k1 = i + ":" + j;
    // String k2 = j + ":" + i;
    // distMap.put(k1,dist);
    // distMap.put(k1,dist);

   }
   closest[i] = minInd;

  }

  int size = clustMap.size();
  int len = data.size();
  int iterations = 0;
  while (size > k) {
   iterations++;
   int c1 = 1;
   for (int i = 1; i <= len; i++) {

    if (distMat[i][closest[i]] < distMat[c1][closest[c1]]) {
     c1 = i;
    }
   }
   int c2 = closest[c1];

   for (int j = 1; j <= len; j++) {
    if (distMat[c1][j] < distMat[c2][j]) {
     distMat[c2][j] = distMat[c1][j];
     distMat[j][c2] = distMat[c1][j];
    }
    // distMat[c2][c2] = Double.MAX_VALUE;
   }

   distMat[c2][c2] = Double.MAX_VALUE;

   for (int i = 1; i <= len; i++) {
    distMat[c1][i] = Double.MAX_VALUE;
    distMat[i][c1] = Double.MAX_VALUE;
   }

   for (int j = 1; j <= len; j++) {
    if (closest[j] == c1) {
     closest[j] = c2;
    }
    if (distMat[c2][j] < distMat[c2][closest[c2]]) {
     closest[c2] = j;
    }
   }

   List < Integer > initClust = clustMap.get(c1);
   List < Integer > nextClust = clustMap.get(c2);

   // Merge both clusters into next
   nextClust.addAll(initClust);
   // Remove initClust from clusterMap
   clustMap.remove(c1);
   size--;

  }

  int count = 1;

  // for(List<Integer> l : clustMap.values()){
  // count++;
  // System.out.println("Cluster "+count+ " contains "+l.toString());
  // }
  // List<Integer> finalOut = new ArrayList<>(data.size());
  
  //Prepare final output and compute required co-efficients
  Integer[] finalOut = new Integer[data.size()];
  for (Integer cl: clustMap.keySet()) {
   for (int rowID: clustMap.get(cl)) {

    // finalOut.add(rowID-1, cl);
    finalOut[rowID - 1] = count;

   }
   System.out.println("Cluster Number " + count++ + " contains " + clustMap.get(cl).toString());

  }

  List < Integer > outList = Arrays.asList(finalOut);
  Index ind = new Index(gTruth, outList);
  double[] stat = ind.CalculateIndex();

  // for(double dot: stat){
  // System.out.println(dot);
  // }
  // int i = 0;
  // for(int te : finalOut){
  // i++;
  // System.out.println("Row "+i+ " belongs to Cluster "+te);
  //
  //
  // }

  printOutPCA(stat, outList, inFile);
  System.out.println("Total Number of iterations :" + iterations);

 }

 public static void printOutPCA(double[] stat, List < Integer > outList, String file) {

  String outFile = file.substring(0, file.length() - 4) + "_HAC_Out.txt";
  String outPCA = file.substring(0, file.length() - 4) + "_HAC_PCA.txt";

  try {
   FileWriter fw1 = new FileWriter(outFile);
   BufferedWriter bw1 = new BufferedWriter(fw1);
   FileWriter fw2 = new FileWriter(outPCA);
   BufferedWriter bw2 = new BufferedWriter(fw2);
   bw1.write("Jaccard Coefficient: " + stat[0] + "\n");
   bw1.write("Rand Index: " + stat[1] + "\n");
   bw1.write("Row" + "\t" + "Cluster" + "\n");

   for (int i = 0; i < outList.size(); i++) {
    bw1.write(i + 1 + "\t" + outList.get(i) + "\n");
    bw2.write(outList.get(i) + "\n");
   }

   bw1.close();
   bw2.close();
   fw1.close();
   fw2.close();

  } catch (IOException e) {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }

 }

 //Function to compute the distance between two points(rows)
 public static double getDist(List < Double > centroid, List < Double > attrib) {

  double sum = 0;

  for (int i = 0; i < centroid.size(); i++) {

   double res = centroid.get(i) - attrib.get(i);
   sum += (res * res);
  }

  sum = Math.sqrt(sum);

  return sum;

 }

}