/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datamining;

//import datamining.DataMining.Index;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author Ajay-Pc
 */
public class DataMining {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here
        
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter File Name from Input Folder: ");
        String file = br.readLine();
        String fileName = "input/" + file;
        List<String> fileData = Files.readAllLines(Paths.get(fileName));

        int lines = fileData.size();                        //number of lines
        //System.out.println("m = "+lines);
        int columns = fileData.get(0).split("\t").length;     //number of attributes
        //System.out.println("n= "+columns);
        int attributes = columns-2;
        double[][] data = new double[lines][columns+1];           //2D array
        //System.out.println("Data is :");
        for(int i = 0; i < lines; i++)
        {
            String[] line = fileData.get(i).split("\t");    //iterate over each line
            for (int j = 0; j < columns; j++)
            {
                data[i][j] = Double.parseDouble(line[j]);   //store data into a 2D array
            //    System.out.print(data[i][j]+ " ");
            }
        //    System.out.println("");
        }
        
       
        
   
        System.out.println("Enter Number of Clusters");                         //take number of initial clusters from user
        int numOfCentroids = Integer.parseInt(br.readLine());
        double[][] initCentroids = new double[numOfCentroids][attributes];
        for (int i = 0; i < numOfCentroids; i++)
        {
            System.out.println("Enter ID of Centroid " + (i+1) + " between 1 and "+lines);      //initial centroid values
            int line = Integer.parseInt(br.readLine());
            for (int j = 0; j < attributes; j++)
                initCentroids[i][j] = data[line - 1][j+2];                                      //assign initial attributes from the line to initial centroid
        }
//        System.out.println("Initial Centroids are:");
//        for(int i=0;i<initCentroids.length;i++)
//        {
//            for(int j=0;j<attributes;j++)
//            System.out.print(initCentroids[i][j]);
//            System.out.println("");
//        }
        
        System.out.println("Enter Max number of Iterations");                   //take from user max number of iterations
        int numOfIter = Integer.parseInt(br.readLine());
        boolean hasConverged = false;
        int iter = 0;
        long startTime = System.currentTimeMillis();                            //to calculate time required
        
        while(!hasConverged && iter<numOfIter)                                  //repeat until clusters converge or max iterations reached
        {
            for (int i = 0; i < lines; i++) {
                double min = Double.MAX_VALUE;
                double dist = 0;
                int belongsTo = 0;
                double sqrtdist = 0;
                for (int k = 0; k < numOfCentroids; k++)
                {
                    for (int j = 0; j < attributes; j++)
                    {
                        dist += Math.pow(data[i][j+2] - initCentroids[k][j], 2);
                    }
                    sqrtdist = Math.sqrt(dist);
                    if (sqrtdist < min) {
                        belongsTo = k + 1;
                        min = sqrtdist;                        
                    }
                    dist = 0;
                }
                data[i][columns] = belongsTo;                                   //change cluster Assignment
        }
        double[][] newCentroids = new double[numOfCentroids][attributes];       //to calculate new centroids
        
        for (int index = 0; index < numOfCentroids; index++)                    //calculate new Centroids
        {
            for (int attr = 0; attr < attributes; attr++)
            {
                double total = 0f;
                int numOfObjects = 0;
                for (int line = 0; line < lines; line++)
                {
                    if ((index+1) == (int)data[line][columns])
                    {
                        numOfObjects++;
                        total = data[line][attr+2] + total;
                    }
                }
                if(numOfObjects != 0) //avoid divide by zero
                    newCentroids[index][attr] = total/numOfObjects;             //assign new mean
            }
        }
        iter++;
        if (check(initCentroids,newCentroids)) hasConverged = true;
       initCentroids = newCentroids;
    }
        long endTime   = System.currentTimeMillis();
        System.out.println("Total Number of Iterations: "+iter);                //total number of iterations taken
        long totalTime = endTime - startTime;                                   //total time taken for convergence

        
//        System.out.println("Final Centroids are:");
//        for(int i=0;i<initCentroids.length;i++)
//        {
//            for(int j=0;j<attributes;j++)
//            System.out.print(initCentroids[i][j]);
//            System.out.println("");
//        }
        List<Integer> groundTruth = new ArrayList<>();
        List<Integer> finalCentroids = new ArrayList<>();
        
        for(int i=0;i<lines;i++)                                                //create arrays for groundtruth and final
        {
            groundTruth.add((int)data[i][1]);
            finalCentroids.add((int)data[i][columns]);
        }
//        for(int i=0;i<lines;i++)                                                //create arrays for groundtruth and final
//        {
//            System.out.println("Ground Truth: "+groundTruth.get(i));
//            System.out.println("Final Centroid: "+finalCentroids.get(i));
//        }

        Index index = new Index(groundTruth,finalCentroids);                    //function to calculate rand index and jaccard coefficient
        double[] stat = index.CalculateIndex();
        printOutPCA(stat,finalCentroids,file);                                  //generate output for PCA
        System.out.println("Total Time Taken: "+totalTime+"ms");
        Map<Integer,List<Integer>> clusterMap = new HashMap<>();
        for(int num = 1;num<=numOfCentroids;num++)
        {
            for(int i=0;i<lines;i++)
            {
                if((int)(data[i][columns]) == num)
                {
                    List<Integer> list = clusterMap.get(num);
                    if(list ==  null)
                    {
                        List<Integer> ans = new ArrayList<>();
                        ans.add(i+1);
                        clusterMap.put(num,ans);
                    }
                    else
                    {
                        list.add(i+1);
                        clusterMap.put(num,list);
                    }
                }
            }
        }
//        for(Integer key : clusterMap.keySet())                                //To check which object belongs to which cluster
//        {
//            System.out.println("Key "+key+" Val "+clusterMap.get(key));
//            System.out.println("");
//        }


//        System.out.println("Data after: ");
//        for(int i = 0; i < lines; i++)
//        {
//            for (int j = 0; j <= columns; j++)
//            {
//                System.out.print(data[i][j]+ " ");
//            }
//            System.out.println("");
//        }

    }
   
    public static boolean check(double[][] initCentroids, double[][] newCentroids)
    {
        return Arrays.deepEquals(initCentroids,newCentroids);
    }
     public static void printOutPCA(double[] stat, List<Integer> outList,String file)
    {

        String outFile = file.substring(0,file.length()-4) + "_KMEANS_Out.txt";
        String outPCA = file.substring(0,file.length()-4) + "_KMEANS_PCA.txt";

        try {
                FileWriter fw1 = new FileWriter(outFile);
                BufferedWriter bw1 = new BufferedWriter(fw1);
                FileWriter fw2 = new FileWriter(outPCA);
                BufferedWriter bw2 = new BufferedWriter(fw2);
                for(int i = 0;i<outList.size();i++){
                    bw1.write(i+1 +"\t"+outList.get(i)+"\n");
                    bw2.write(outList.get(i)+"\n");
                }
                bw1.write("Jaccard Coefficient: "+stat[0] + "\n");
                bw1.write("Rand Index: "+stat[1] + "\n");
                bw1.close(); bw2.close();
                fw1.close();fw2.close();

        } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }


	}
        
}
