package com.ontariotechu.sofe3980U;


import java.io.FileReader;
import java.util.List;
import com.opencsv.*;


/**
* Evaluate Multiclass Classification with Cross Entropy and Confusion Matrix
*/
public class App {
   public static void main(String[] args) {
       String filePath = "model.csv";
       FileReader filereader;
       List<String[]> allData;
       try {
           filereader = new FileReader(filePath);
           CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
           allData = csvReader.readAll();
       } catch (Exception e) {
           System.out.println("Error reading the CSV file");
           return;
       }


       int count = 0;
       float[] y_predicted = new float[5];
       double totalCrossEntropy = 0.0;
       int[][] confusionMatrix = new int[5][5]; // 5x5 matrix for 5 classes


       for (String[] row : allData) {
           int y_true = Integer.parseInt(row[0]) - 1; // 0-indexed class (adjusting for 1-indexed data)
           for (int i = 0; i < 5; i++) {
               y_predicted[i] = Float.parseFloat(row[i + 1]); // Probabilities for all classes
           }


           // Calculate Cross Entropy for this row
           totalCrossEntropy += calculateCrossEntropy(y_true, y_predicted);


           // Update Confusion Matrix (adjusted to match expected order)
           int y_pred = getPredictedClass(y_predicted);
           confusionMatrix[y_pred][y_true]++; // Reverse row and column for correct order


           count++;
       }


       // Calculate the average Cross Entropy
       double averageCE = totalCrossEntropy / count;
       System.out.println("CE = " + String.format("%.7f", averageCE)); // Adjust precision


       // Display Confusion Matrix
       System.out.println("Confusion matrix");
       System.out.println("\t\t y=1 \t y=2 \t y=3 \t y=4 \t y=5");
       for (int i = 0; i < 5; i++) {
           System.out.print("y^=" + (i + 1) + "\t");
           for (int j = 0; j < 5; j++) {
               System.out.print(confusionMatrix[i][j] + "\t");
           }
           System.out.println();
       }
   }


   // Method to calculate Cross Entropy for a given instance
   private static double calculateCrossEntropy(int y_true, float[] y_pred) {
       // Calculate Cross Entropy for the true class
       return -Math.log(y_pred[y_true]);
   }


   // Method to get the predicted class (highest probability)
   private static int getPredictedClass(float[] y_pred) {
       int predictedClass = 0;
       for (int i = 1; i < 5; i++) {
           if (y_pred[i] > y_pred[predictedClass]) {
               predictedClass = i;
           }
       }
       return predictedClass;
   }
}
