package com.ontariotechu.sofe3980U;




import java.io.FileReader;
import java.util.List;
import com.opencsv.*;


/**
* Evaluate Single Variable Continuous Regression
*
*/
public class App
{
   public static void main( String[] args )
   {
       // Process each file
       evaluateModel("model_1.csv");
       evaluateModel("model_2.csv");
       evaluateModel("model_3.csv");
   }
   //method to calculate MSE, MAE, and MARE to avoid repeating code
   public static void evaluateModel (String filePath)
   {
       FileReader filereader;
       List<String[]> allData;
       // Variables for Error Calculations
       double mse=0, mae = 0, mare =0;
       int count = 0;


       try
       {
           filereader = new FileReader(filePath);
           CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
           allData = csvReader.readAll();
       }
       catch(Exception e)
       {
           System.out.println( "Error reading the CSV file" );
           return;
       }
      
       //int count=0;
       for (String[] row : allData)
       {
           float y_true=Float.parseFloat(row[0]);
           float y_predicted=Float.parseFloat(row[1]);


           //System.out.print(y_true + "  \t  "+y_predicted);
           //System.out.println();


           // Error calculations
           double error = y_true - y_predicted;
           mse += error *error;
           mae+=Math.abs(error);
          
           // Avoid division by zero
           if (y_true != 0)
           {
               mare += Math.abs(error / y_true);
           }
              
               count++;
       }
       // Calculate final mse,mae and ,mare
       if (count>0)
       {
           mse /= count;
           mae /= count;
           mare /=count;
       }
       // Results
       System.out.println("Metrics for" + filePath + ":");
       System.out.println("MSE: " + mse);
       System.out.println("MAE: " + mae);
       System.out.println("MARE: " + mare);
       System.out.println ();
   }
}
