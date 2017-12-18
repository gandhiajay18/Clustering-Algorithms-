import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.io.IOUtils;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Counters;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.*;
import java.util.*;



public class hadoopkmeans {
 static String fname = "";
 static int totalLine = 0;
 static int totalIter = 0;
 public static class TokenizerMapper
 extends Mapper < Object, Text, Text, Text > {

  Map < Integer, List < Double >> map = new HashMap < Integer,  List < Double >> ();
  Map < Integer,  List < Double >> centroids = new TreeMap < Integer,  List < Double >> ();
  private final static IntWritable one = new IntWritable(1);
  private Text word = new Text();


  @Override
  protected void setup(Context context) throws IOException,
  InterruptedException {
    //In this function, we set the initial centroids if they don't already exist, or read the new ones which were
    // set by the reducer in the previous iteration


   FileSplit fsFileSplit = (FileSplit) context.getInputSplit();
   //String filename = context.getConfiguration().get(fsFileSplit.getPath().getParent().getName());
   String filename = ((org.apache.hadoop.mapreduce.lib.input.FileSplit) context.getInputSplit()).getPath().getName().toString();
   context.getConfiguration().set("inFile", filename);
   // System.out.println("FILENAME2 IS BIBGPBGPIGSPSIPBGPBSGP "+filename);
   fname = filename;

   //Check if ./centroid/cent_new_filename exists or not. If it exists, then read clusters from that file into centroids Map, AND write those clusters to 
   //cent_old_filename.
   // Else, read input file and generate random clusters from that file and store in cent_old_filename

   File f = new File("./centroid/cent_new.txt");
   if (f.exists()) {

    BufferedReader br = null;
    BufferedWriter bw = null;
    FileWriter fw = null;
    try {
     br = new BufferedReader(new FileReader(f));
     fw = new FileWriter("./centroid/cent_old" + ".txt");

     bw = new BufferedWriter(fw);
     String contentLine = br.readLine();
     while (contentLine != null) {
      bw.write(contentLine + "\n");
      //System.out.println(contentLine);
      String[] st = contentLine.split("\t");
      List < Double > li = new ArrayList < Double > ();
      for (int i = 1; i < st.length; i++) {
       li.add(Double.valueOf(st[i]));
      }
      centroids.put(Integer.parseInt(st[0]), li);
      //   //bw.write(contentLine + "\n");

      contentLine = br.readLine();
     }


     bw.close();
     fw.close();
    } catch (IOException ioe) {
     ioe.printStackTrace();
    }



   } else {
    // System.out.println("fail");


    BufferedReader br = null;
    BufferedWriter bw = null;
    FileWriter fw = null;
    List < Integer > gTruth = new ArrayList < Integer > ();

    try {
     br = new BufferedReader(new FileReader("./input/" + filename));
     fw = new FileWriter("./centroid/cent_old" + ".txt");
     FileWriter fw1 = new FileWriter("./centroid/gTruth" + ".txt");

     bw = new BufferedWriter(fw);
     BufferedWriter bw1 = new BufferedWriter(fw1);
     String contentLine = br.readLine();
     while (contentLine != null) {
      //System.out.println(contentLine);
      String[] st = contentLine.split("\t");
      List < Double > li = new ArrayList < Double > ();
      for (int i = 2; i < st.length; i++) {
       li.add(Double.valueOf(st[i]));
      }
      map.put(Integer.parseInt(st[0]), li);
      gTruth.add(Integer.parseInt(st[1]));
      bw1.write(st[1] + "\n");
      //bw.write(contentLine + "\n");
      contentLine = br.readLine();
     }
     // totalLine = map.size();
     bw1.close();
     fw1.close();

     Random rnd = new Random();
     int num = context.getConfiguration().getInt("clusters", 5);

     for (int i = 1; i <= num; i++) {
      int row = rnd.nextInt(map.size());
      List < Double > cent = map.get(row);
      centroids.put(i, cent);
     }

     for (int k: centroids.keySet()) {
      StringBuilder sb = new StringBuilder();
      sb.append(k + "\t");
      for (Double d: centroids.get(k)) {
       sb.append(d + "\t");
      }
      bw.write(sb.toString() + "\n");
     }
     bw.close();
     fw.close();
    } catch (IOException ioe) {
     ioe.printStackTrace();
    }
   }
  }

  //Function to get distance between two points(rowIDs)
  public double getDist(List < Double > centroid, List < Double > attrib) {

   double sum = 0;

   for (int i = 0; i < centroid.size(); i++) {

    double res = centroid.get(i) - attrib.get(i);
    sum += (res * res);
   }

   sum = Math.sqrt(sum);

   return sum;



  }

  public void map(Object key, Text value, Context context) throws IOException,
  InterruptedException {

    //The map() function receives the input file, line by line.
    //We split the input, and find the distance between the current centroids and the point
    // The point is assigned to the centroid having minimum distance from the point
    // We output the assigned centroid and the point(input line received in map()), which is used as input for reducer

   String[] st = value.toString().split("\t");

   List < Double > att = new ArrayList < Double > ();
   StringBuilder sb = new StringBuilder();
   for (int i = 2; i < st.length; i++) {
    att.add(Double.valueOf(st[i]));
    sb.append(st[i] + "\t");
   }

   double min = Double.MAX_VALUE;
   int ind = -1;

   for (int j = 1; j <= centroids.size(); j++) {
    double sum = 0;

    sum = getDist(centroids.get(j), att);

    if (sum < min) {
     min = sum;
     ind = j;
    }


   }

   Text data = new Text("" + sb.toString());
   Text cent = new Text("" + ind);
   context.write(cent, value);


  }
 }



 public static class IntSumReducer
 extends Reducer < Text, Text, Text, Text > {
  private IntWritable result = new IntWritable();
  Map < Integer,  List < Double >> oldCent = new HashMap < Integer,  List < Double >> ();
  Map < Integer,  List < Double >> newCent = new HashMap < Integer,  List < Double >> ();
  Map < Integer,  List < Integer >> finalMap = new HashMap < Integer,  List < Integer >> ();
  List < Integer > gTruth = new ArrayList < Integer > ();
  int totalCount = 0;

//This class is used to calculate the Jaccard and Rand co-efficient values
  public class Index {
   double same_same = 0;
   double diff_diff = 0;
   double same_diff = 0;
   double diff_same = 0;
   int size;
   int[][] cMatrix;
   int[][] gtMatrix;
   List < Integer > groundTruth;
   List < Integer > finalCentroids;

   public Index(List < Integer > groundTruth, List < Integer > finalCentroids) {
    int lines = groundTruth.size();
    cMatrix = new int[lines][lines];
    gtMatrix = new int[lines][lines];
    this.groundTruth = groundTruth;
    this.finalCentroids = finalCentroids;
    size = lines;

   }

   public double[] CalculateIndex() {
    //System.out.print("Size = " +size);
    for (int i = 0; i < size; i++) {
     for (int j = i; j < size; j++) {
      if (finalCentroids.get(i) == null || finalCentroids.get(j) == null)
       cMatrix[i][j] = cMatrix[j][i] = 0;
      else {
       if (finalCentroids.get(i) == finalCentroids.get(j))
        cMatrix[i][j] = cMatrix[j][i] = 1;
       else
        cMatrix[i][j] = cMatrix[j][i] = 0;
      }
      if (groundTruth.get(i) == groundTruth.get(j))
       gtMatrix[i][j] = gtMatrix[j][i] = 1;
      else
       gtMatrix[i][j] = gtMatrix[j][i] = 0;
     }
    }
    for (int i = 0; i < size; i++) {
     for (int j = 0; j < size; j++) {
      if (cMatrix[i][j] == gtMatrix[i][j] && cMatrix[i][j] == 1) {
       same_same++;
      }
      if (cMatrix[i][j] == gtMatrix[i][j] && cMatrix[i][j] == 0) {
       diff_diff++;
      } else {
       if (cMatrix[i][j] == 1 && gtMatrix[i][j] == 0)
        same_diff++;
       else if (cMatrix[i][j] == 0 && gtMatrix[i][j] == 1)
        diff_same++;
      }
     }
    }
    System.out.println("Jaccard Coefficient: " + same_same / (same_same + same_diff + diff_same));
    System.out.println("Rand Index: " + (same_same + diff_diff) / (same_same + diff_diff + same_diff + diff_same));
    double[] res = new double[2];
    //        return new double[double (same_same/(same_same+same_diff+diff_same)),1.0]
    res[0] = (same_same / (same_same + same_diff + diff_same));
    res[1] = (same_same + diff_diff) / (same_same + diff_diff + same_diff + diff_same);

    return res;
   }


  }







  @Override
  protected void setup(Context context) throws IOException,
  InterruptedException {
    //The reducer setup() reads the groundTruth values and the current initial centroids

   
   String filename = context.getConfiguration().get("inputFile");
   BufferedReader br = null;
   try {
    // br = new BufferedReader(new FileReader("./centroid/cent_old_"+filename));
    br = new BufferedReader(new FileReader("./centroid/cent_old.txt"));
    BufferedReader br1 = new BufferedReader(new FileReader("./centroid/gTruth.txt"));

    String contentLine = br.readLine();
    while (contentLine != null) {
     String[] st = contentLine.split("\t");
     List < Double > li = new ArrayList < Double > ();
     for (int i = 1; i < st.length; i++) {
      li.add(Double.valueOf(st[i]));
     }
     oldCent.put(Integer.parseInt(st[0]), li);
     //bw.write(contentLine + "\n");
     contentLine = br.readLine();
    }

    String contentLine1 = br1.readLine();
    while (contentLine1 != null) {
     gTruth.add(Integer.parseInt(contentLine1));
     contentLine1 = br1.readLine();

    }



   } catch (IOException ioe) {
    ioe.printStackTrace();
   }
  }

  public void reduce(Text key, Iterable < Text > values,
   Context context
  ) throws IOException,
  InterruptedException {
  //The reducer takes all the assigned points to a given cluster 
  //and computes the new centroids by averaging the corresponding points
  //The output of reducer is the centroid (key) and the rowID in the original dataset


   List < Integer > rowID = new ArrayList < Integer > ();
   Map < Integer, Double > atMap = new HashMap < Integer, Double > ();

   int count = 0;
   for (Text t: values) {
    totalCount++;
    count++;
    String input = t.toString();
    String[] in = input.split("\t");
    rowID.add(Integer.parseInt( in [0]));

    for (int i = 2; i < in .length; i++) {

     Double prev = atMap.get(i - 1);

     if (prev == null) {
      atMap.put(i - 1, Double.valueOf( in [i]));
     } else {
      prev += Double.valueOf( in [i]);
      atMap.put(i - 1, prev);
     }


    }

   }

   List < Double > newAtt = new ArrayList < Double > ();
   for (int i = 1; i <= atMap.size(); i++) {

    Double sum = atMap.get(i);
    Double avg = sum / count;
    newAtt.add(avg);

   }

   newCent.put(Integer.parseInt(key.toString()), newAtt);

   StringBuilder sb = new StringBuilder();
   Collections.sort(rowID);
   for (int r: rowID) {
    sb.append(r + "\t");
   }

   finalMap.put(Integer.parseInt(key.toString()), rowID);
   Text clust = new Text("" + sb.toString());
   context.write(new Text("" + Integer.parseInt(key.toString())), clust);


  }

  @Override
  protected void cleanup(Context context) throws IOException,
  InterruptedException {
   //Write new centroids to file


   String filename = context.getConfiguration().get("inputFile");

   
   BufferedWriter bw = null;
   FileWriter fw = null;
   try {
   
    fw = new FileWriter("./centroid/cent_new" + ".txt");
    FileWriter fw1 = new FileWriter("finalCentroids_" + fname);
    BufferedWriter bw1 = new BufferedWriter(fw1);

    bw = new BufferedWriter(fw);

    for (int i = 1; i <= newCent.size(); i++) {
     StringBuilder sb = new StringBuilder();
     sb.append(i + "\t");
     for (Double d: newCent.get(i)) {
      sb.append(d + "\t");
     }
     bw.write(sb.toString() + "\n");
     bw1.write(sb.toString() + "\n");
    }
    bw.close();
    fw.close();
    bw1.close();
    fw1.close();
   } catch (IOException ioe) {
    ioe.printStackTrace();
   }

   boolean convCheck = true;
   //Check for convergence, comparing with old centroids
   for (int n = 1; n <= newCent.size(); n++) {
    double diff = getDist(newCent.get(n), oldCent.get(n));
    if (new Double(0.0).compareTo(diff) >= 0) {
     //Current cluster has converged

    } else {
     convCheck = false;
     context.getCounter(iterCount.iterate).increment(1L);
     //Increment Counter by 1.
    }
   }
   Integer[] finalOut = new Integer[totalCount];

   if (convCheck) {
    int count = 1;
    for (Integer cl: finalMap.keySet()) {
     for (int rowid: finalMap.get(cl)) {

      // finalOut.add(rowID-1, cl);
      finalOut[rowid - 1] = count;

     }
     count++;
     // System.out.println("Cluster Number " + count++ + " contains " + clustMap.get(cl).toString());

    }

    List < Integer > outList = Arrays.asList(finalOut);
    Index ind = new Index(gTruth, outList);
    double[] stat = ind.CalculateIndex();
    printOutPCA(stat, outList, "");


   }



  }





  public double getDist(List < Double > centroids, List < Double > attrib) {

   double sum = 0;
   

   for (int i = 0; i < centroids.size(); i++) {

    double res = centroids.get(i) - attrib.get(i);
    sum += (res * res);
   }

   sum = Math.sqrt(sum);

   return sum;



  }


  public static void printOutPCA(double[] stat, List < Integer > outList, String file) {
//Function to write output to local file, and generate file for input to PCA function

   String outFile = fname.substring(0, fname.length() - 4) + "_" + "hadoop_out.txt";
   String outPCA = fname.substring(0, fname.length() - 4) + "_" + "hadoop_pca.txt";

   try {
    FileWriter fw1 = new FileWriter(outFile);
    BufferedWriter bw1 = new BufferedWriter(fw1);
    FileWriter fw2 = new FileWriter(outPCA);
    BufferedWriter bw2 = new BufferedWriter(fw2);
    bw1.write("Jaccard Coefficient: " + stat[0] + "\n");
    bw1.write("Rand Index: " + stat[1] + "\n");
    bw1.write("Total Iterations: " + totalIter + "\n");
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

 }

 static enum iterCount {
  iterate;
 }

 public static void main(String[] args) throws Exception {

  long startTime = System.currentTimeMillis();

  Configuration conf = new Configuration();
  conf.set("inFile", "null");

  int numClusters = 5;
  if (args.length > 2) {
   numClusters = Integer.parseInt(args[2]);
  }
  conf.setInt("clusters", numClusters);
  //Delete Centroid files
  File centDir = new File("./centroid/");
  if (centDir.exists()) {
   for (File file: centDir.listFiles()) {
    // if (file.isDirectory()) purgeDirectory(file);
    file.delete();
   }
  } else {
   new File("./centroid").mkdir();

  }

  long result = 1;
  int iter = 0;
  while (result > 0) {
   totalIter++;
   FileSystem fs = FileSystem.get(conf);

   if (fs.exists(new Path(args[1]))) {
    /*If exists, delete the output path*/
    fs.delete(new Path(args[1]), true);
   }
   Job job = Job.getInstance(conf, "Hadoop kMeans");

   job.setJarByClass(hadoopkmeans.class);
   job.setMapperClass(TokenizerMapper.class);
   // job.setCombinerClass(IntSumReducer.class);
   job.setReducerClass(IntSumReducer.class);
   job.setNumReduceTasks(1);

   job.setOutputKeyClass(Text.class);
   job.setOutputValueClass(Text.class);
   FileInputFormat.addInputPath(job, new Path(args[0]));
   //FileInputFormat.addInputPath(job, new Path("./input/"));
   FileOutputFormat.setOutputPath(job, new Path(args[1]));
   //FileOutputFormat.setOutputPath(job, new Path("./output"));
   // System.exit(job.waitForCompletion(true) ? 0 : 1);
   job.waitForCompletion(true);


   Counters cnt = job.getCounters();
   result = cnt.findCounter(iterCount.iterate).getValue();
   // result--;
   iter++;
  }
  long endTime = System.currentTimeMillis();
  long totalTime = endTime - startTime;
  System.out.println("Total Time taken : " + totalTime + "ms");
  System.out.println("Total number of iterations: " + totalIter);


 }

}