package datamining;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ajay-Pc
 */
public class Index {
    double same_same = 0;		
    double diff_diff = 0;
    double same_diff = 0;     
    double diff_same = 0;
    int size;
    int[][] cMatrix;
    int[][] gtMatrix;
    List<Integer> groundTruth;
    List<Integer> finalCentroids;
   
    public Index(List<Integer> groundTruth, List<Integer> finalCentroids)
    {
        int lines = groundTruth.size();
        cMatrix = new int[lines][lines];
        gtMatrix = new int[lines][lines];
        this.groundTruth = groundTruth;
        this.finalCentroids = finalCentroids;
        size=lines;
        
    }
    
    public double[] CalculateIndex()
    {
        //System.out.print("Size = " +size);
        for(int i = 0; i < size; i++)                                           //To populate matrix of final centroids and ground truth values
        {
            for(int j = i; j < size; j++)
            {
                if(finalCentroids.get(i)==null || finalCentroids.get(j)==null)  //check for null values if present set to zero
                    cMatrix[i][j] = cMatrix[j][i] = 0;
                else
                {
                    if(finalCentroids.get(i) == finalCentroids.get(j))          //if both belong to same cluster set to 1
                        cMatrix[i][j] = cMatrix[j][i] = 1;
                    else
                        cMatrix[i][j] = cMatrix[j][i] = 0;                      //else zero
                }
                if(groundTruth.get(i) == groundTruth.get(j))                    //if both belong to same cluster set to 1
                    gtMatrix[i][j] = gtMatrix[j][i] = 1;
                else
                    gtMatrix[i][j] = gtMatrix[j][i] = 0;                        //else zero
            }
        }
         for(int i = 0; i < size; i++)
        {
        for(int j = 0; j < size; j++)
            {
                if(cMatrix[i][j] == gtMatrix[i][j] && cMatrix[i][j] == 1) //if present in both ground truth and final centroid
                {
                    same_same++;
                }
                if(cMatrix[i][j] == gtMatrix[i][j] && cMatrix[i][j] == 0)  //if absent in both ground truth and final centroid
                {
                    diff_diff++;
                }
                else
                {
                    if(cMatrix[i][j] == 1 && gtMatrix[i][j] == 0)         //if present in final centroid but absent in ground truth
                        same_diff++;
                    else if(cMatrix[i][j] == 0 && gtMatrix[i][j] == 1)    //if present in ground truth but absent in final centroid
                        diff_same++;
                }
            }
        }
        double[] stat = new double[2];
        stat[0]= same_same/(same_same+same_diff+diff_same);     //Jaccard Coefficient
        stat[1] = (same_same+diff_diff)/(same_same+diff_diff+same_diff+diff_same);      //Rand Index
        //System.out.println("same _ same " + same_same + " diff_same" + diff_same + "same _ diff " + same_diff);
        System.out.println("Jaccard Coefficient: "+stat[0]);
        System.out.println("Rand Coefficient: "+stat[1]);
        return stat;
    }
   
    
}
