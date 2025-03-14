package com.ontariotechu.sofe3980U;


import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;


public class App {


   public static void main(String[] args) {
       // Evaluate metrics for multiple CSV files
       evaluateMetricsForFile("model_1.csv");
       evaluateMetricsForFile("model_2.csv");
       evaluateMetricsForFile("model_3.csv");
   }


   // Method to evaluate metrics for a given CSV file
   public static void evaluateMetricsForFile(String filePath) {
       FileReader filereader;
       List<String[]> allData;


       try {
           filereader = new FileReader(filePath);
           CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
           allData = csvReader.readAll();
       } catch (Exception e) {
           System.out.println("Error reading the CSV file: " + filePath);
           return;
       }


       List<Double> yTrue = new ArrayList<>();
       List<Double> yPred = new ArrayList<>();
      
       // Collect the true and predicted values
       for (String[] row : allData) {
           yTrue.add(Double.parseDouble(row[0]));
           yPred.add(Double.parseDouble(row[1]));
       }


       // Calculate BCE
       double bce = calculateBCE(yTrue, yPred);
       System.out.println("BCE for " + filePath + ": " + bce);


       // Calculate Confusion Matrix and derived metrics
       int[] confusionMatrix = calculateConfusionMatrix(yTrue, yPred);
       int TP = confusionMatrix[0], FP = confusionMatrix[1], TN = confusionMatrix[2], FN = confusionMatrix[3];


       // Accuracy
       double accuracy = (TP + TN) / (double) (TP + FP + TN + FN);
       System.out.println("Accuracy for " + filePath + ": " + accuracy);


       // Precision
       double precision = (TP) / (double) (TP + FP);
       System.out.println("Precision for " + filePath + ": " + precision);


       // Recall
       double recall = (TP) / (double) (TP + FN);
       System.out.println("Recall for " + filePath + ": " + recall);


       // F1-Score
       double f1Score = 2 * (precision * recall) / (precision + recall);
       System.out.println("F1-Score for " + filePath + ": " + f1Score);


       // Calculate AUC-ROC
       double auc = calculateAUCROC(yTrue, yPred);
       System.out.println("AUC-ROC for " + filePath + ": " + auc);
   }


   // Method to calculate Binary Cross-Entropy (BCE)
   public static double calculateBCE(List<Double> yTrue, List<Double> yPred) {
       double bce = 0;
       int n = yTrue.size();
       for (int i = 0; i < n; i++) {
           double trueValue = yTrue.get(i);
           double predValue = yPred.get(i);
           bce -= trueValue * Math.log(predValue) + (1 - trueValue) * Math.log(1 - predValue);
       }
       return bce / n;
   }


   // Method to calculate Confusion Matrix [TP, FP, TN, FN]
   public static int[] calculateConfusionMatrix(List<Double> yTrue, List<Double> yPred) {
       int TP = 0, FP = 0, TN = 0, FN = 0;
       for (int i = 0; i < yTrue.size(); i++) {
           int trueLabel = yTrue.get(i) > 0.5 ? 1 : 0;
           int predLabel = yPred.get(i) > 0.5 ? 1 : 0;
          
           if (trueLabel == 1 && predLabel == 1) TP++;
           if (trueLabel == 0 && predLabel == 1) FP++;
           if (trueLabel == 0 && predLabel == 0) TN++;
           if (trueLabel == 1 && predLabel == 0) FN++;
       }
       return new int[]{TP, FP, TN, FN};
   }


   // Method to calculate AUC-ROC
   public static double calculateAUCROC(List<Double> yTrue, List<Double> yPred) {
       int n = yTrue.size();
       List<int[]> sortedData = new ArrayList<>();
      
       for (int i = 0; i < n; i++) {
           sortedData.add(new int[]{yTrue.get(i).intValue(), yPred.get(i).intValue()});
       }
      
       // Sort by predicted values in descending order
       sortedData.sort((a, b) -> Double.compare(b[1], a[1]));


       // Compute AUC using the trapezoidal rule
       double auc = 0.0;
       double[] tpr = new double[101];
       double[] fpr = new double[101];
       int totalPositives = (int) yTrue.stream().filter(y -> y == 1).count();
       int totalNegatives = n - totalPositives;


       for (int threshold = 0; threshold <= 100; threshold++) {
           double t = threshold / 100.0;
           int TP = 0, FP = 0, FN = totalPositives, TN = totalNegatives;


           for (int[] row : sortedData) {
               int actual = row[0];
               int predicted = row[1] >= t ? 1 : 0;


               if (actual == 1 && predicted == 1) TP++;
               if (actual == 1 && predicted == 0) FN--;
               if (actual == 0 && predicted == 1) FP++;
               if (actual == 0 && predicted == 0) TN--;
           }


           // True Positive Rate (TPR) and False Positive Rate (FPR)
           tpr[threshold] = (double) TP / totalPositives;
           fpr[threshold] = (double) FP / totalNegatives;


           if (threshold > 0) {
               auc += 0.5 * (fpr[threshold - 1] - fpr[threshold]) * (tpr[threshold] + tpr[threshold - 1]);
           }
       }
       return auc;
   }
}
