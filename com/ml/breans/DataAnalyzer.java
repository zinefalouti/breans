package com.ml.breans;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;

/**
 * Breans - A Data Analyzer added in v1.1.2
 * Author: Zine El Abidine Falouti
 * License: Open Source
 */

public class DataAnalyzer {

    //Convert CSV to Matrix
    public static String[][] readCsvToMatrix(String filePath) throws IOException {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(","); // Split by comma, adjust delimiter if needed
                rows.add(values);
            }
        }

        // Convert List<String[]> to String[][]
        String[][] matrix = new String[rows.size()][];
        for (int i = 0; i < rows.size(); i++) {
            matrix[i] = rows.get(i);
        }
        return matrix;
    }

    //Read a Matrix
    public static void ReadMatrix(String[][] dataMatrix ){

            // Print the matrix for verification
            for (String[] row : dataMatrix) {
                for (String value : row) {
                    System.out.print(value + "\t");
                }
                System.out.println();
            }
    }

    //Data Shape
    public static void DataShape(String[][] dataMatrix){
        int width = dataMatrix[0].length;
        int height = dataMatrix.length;

        System.out.println("The data shape is '[Columns ; Rows]': ["+width+" ; "+height+"]");
    }

    //Data Head
    public static void DataHead(String[][] dataMatrix, int level){

        if (dataMatrix == null || dataMatrix.length == 0) {
            System.out.println("No data to display.");
            return;
        }



        int rowsToShow = Math.min(level, dataMatrix.length);

        for(int i=0 ; i < rowsToShow ; i++){

            for(int j=0; j < dataMatrix[i].length ; j++){
                System.out.print(dataMatrix[i][j] + "\t");
            }

            System.out.println("");
             
        }

    }

    //Display Data Tail
    public static void DataTail(String[][] dataMatrix, int level) {
        if (dataMatrix == null || dataMatrix.length == 0) {
            System.out.println("No data to display.");
            return;
        }else{

            int totalRows = dataMatrix.length;
            int rowsToShow = Math.min(level, totalRows);
            System.out.println("Data Tail Starts Here: ");
            for (int i = totalRows - rowsToShow; i < totalRows; i++) {
                for (int j = 0; j < dataMatrix[i].length; j++) {
                    System.out.print(dataMatrix[i][j] + "\t");
                }
                System.out.println();
            }

        }

       
    }


    //Find blanks or NaN in dataset
    public static void FindBlank(String[][] dataMatrix){

        List<String> errorsPos = new ArrayList<String>();


        for (int i = 0; i < dataMatrix.length; i++) {
            for (int j = 0; j < dataMatrix[i].length; j++) {
                String value = (dataMatrix[i][j] == null) ? "" : dataMatrix[i][j].trim();
                if (value.isEmpty() || value.equals("NaN")) {
                    errorsPos.add(i + "," + j);
                }
            }
        }

        int totalErrors = errorsPos.size();

        System.out.println("A total blanks/errors of: "+totalErrors+" were found in your dataset." );

        if(totalErrors > 0){
            for(String i : errorsPos){
                System.out.println("Error or Blank position: "+i);
            }
        }else{
            System.out.println("Data check complete: everything's clear.");
        }

    }


    //Checking the health, outliers, etc in the dataset
    public static void Describe(String[][] dataMatrix) {
    if (dataMatrix == null || dataMatrix.length == 0) {
        System.out.println("No data to describe.");
        return;
    }

    int columns = dataMatrix[0].length;

    for (int col = 0; col < columns; col++) {
        List<Double> numericValues = new ArrayList<>();

        // Collect numeric values, skip blanks/NaN/non-numeric
        for (int row = 1; row < dataMatrix.length; row++) {
            String cell = dataMatrix[row][col];
            if (cell == null || cell.trim().isEmpty() || cell.equalsIgnoreCase("NaN")) {
                continue;
            }
            try {
                numericValues.add(Double.parseDouble(cell.trim()));
            } catch (NumberFormatException e) {
                // Non-numeric, skip
            }
        }

        if (!numericValues.isEmpty()) {
            // Sort for median and IQR
            numericValues.sort(Double::compareTo);

            int n = numericValues.size();

            double sum = 0.0;
            double min = numericValues.get(0);
            double max = numericValues.get(n - 1);

            for (double val : numericValues) {
                sum += val;
            }

            double mean = sum / n;

            // Median
            double median;
            if (n % 2 == 0) {
                median = (numericValues.get(n / 2 - 1) + numericValues.get(n / 2)) / 2.0;
            } else {
                median = numericValues.get(n / 2);
            }

            // Standard Deviation
            double sumSquaredDiffs = 0.0;
            for (double val : numericValues) {
                sumSquaredDiffs += Math.pow(val - mean, 2);
            }
            double stddev = Math.sqrt(sumSquaredDiffs / n);

            // IQR (Interquartile Range)
            double q1 = numericValues.get(n / 4);
            double q3 = numericValues.get(3 * n / 4);
            double iqr = q3 - q1;

            // Outlier detection (values < Q1 - 1.5*IQR or > Q3 + 1.5*IQR)
            double lowerBound = q1 - 1.5 * iqr;
            double upperBound = q3 + 1.5 * iqr;

            int outliers = 0;
            for (double val : numericValues) {
                if (val < lowerBound || val > upperBound) {
                    outliers++;
                }
            }

            System.out.println("Column " + col + ":");
            System.out.printf("  Count = %d\tMean = %.4f\tMedian = %.4f\tStdDev = %.4f%n", n, mean, median, stddev);
            System.out.printf("  Min = %.4f\tMax = %.4f\tOutliers = %d%n", min, max, outliers);
            System.out.printf("  Q1 = %.4f\tQ3 = %.4f\tIQR = %.4f%n", q1, q3, iqr);
            System.out.println();
        } else {
            System.out.println("Column " + col + " is non-numeric or empty, skipped.");
        }
    }
}

    //Generating an html bar chart between two columns as X and Y and saving it
    public static void DataSetChart(String[][] dataMatrix, int xCol, int yCol, String htmlname) throws IOException {
            if (dataMatrix == null || dataMatrix.length <= 1) {
            System.out.println("No data to plot.");
            return;
        }
        if (xCol >= dataMatrix[0].length || yCol >= dataMatrix[0].length) {
            System.out.println("Invalid column index.");
            return;
        }

        String title = dataMatrix[0][yCol] + " vs " + dataMatrix[0][xCol];

        // Collect labels and values
        StringBuilder labels = new StringBuilder();
        StringBuilder values = new StringBuilder();
        for (int i = 1; i < dataMatrix.length; i++) {
            labels.append("'").append(dataMatrix[i][xCol]).append("'");
            values.append(dataMatrix[i][yCol]);
            if (i < dataMatrix.length - 1) {
                labels.append(", ");
                values.append(", ");
            }
        }

        String html = String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
            </head>
            <body>
                <h3 style="text-align:center;">%s</h3>
                <canvas id="myChart" width="600" height="400"></canvas>
                <script>
                    const ctx = document.getElementById('myChart');
                    new Chart(ctx, {
                        type: 'bar',
                        data: {
                            labels: [%s],
                            datasets: [{
                                label: '%s',
                                data: [%s],
                                backgroundColor: 'rgba(122, 91, 91, 0.62)',
                                borderColor: 'rgba(152, 115, 115, 1)',
                                borderWidth: 1
                            }]
                        },
                        options: { scales: { y: { beginAtZero: true } } }
                    });
                </script>
            </body>
            </html>
            """, title, labels.toString(), dataMatrix[0][yCol], values.toString());

        File file = new File(htmlname+".html");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(html);
        }

        Desktop.getDesktop().browse(file.toURI());
        }
        
    
    //Full report Summary in HTML
    public static String captureOutput(Runnable runnable) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream oldOut = System.out;
        System.setOut(ps);
        try {
            runnable.run();
        } finally {
            System.out.flush();
            System.setOut(oldOut);
        }
        return baos.toString();
    }

    public static void DataSetSummary(String[][] dataMatrix, String reportName, int headRows, int tailRows) throws IOException {
        String shapeOutput = captureOutput(() -> DataShape(dataMatrix));
        String headOutput = captureOutput(() -> DataHead(dataMatrix, headRows));
        String tailOutput = captureOutput(() -> DataTail(dataMatrix, tailRows));
        String blanksOutput = captureOutput(() -> FindBlank(dataMatrix));
        String describeOutput = captureOutput(() -> Describe(dataMatrix));

        String html = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8" />
            <title>Data Analysis Report</title>
            <style>
                body { font-family: monospace; margin: 20px; background: #f5f5f5; }
                h2 { color: #333; }
                pre { background: #fff; border: 1px solid #ccc; padding: 15px; overflow-x: auto; }
                section { margin-bottom: 40px; }
            </style>
        </head>
        <body>
            <h1>Data Analysis Report: %s</h1>

            <section>
                <h2>Data Shape</h2>
                <pre>%s</pre>
            </section>

            <section>
                <h2>Data Head (First %d rows)</h2>
                <pre>%s</pre>
            </section>

            <section>
                <h2>Data Tail (Last %d rows)</h2>
                <pre>%s</pre>
            </section>

            <section>
                <h2>Missing Values / Blanks Check</h2>
                <pre>%s</pre>
            </section>

            <section>
                <h2>Descriptive Statistics</h2>
                <pre>%s</pre>
            </section>
        </body>
        </html>
        """.formatted(reportName, shapeOutput, headRows, headOutput, tailRows, tailOutput, blanksOutput, describeOutput);

        File file = new File(reportName + ".html");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(html);
        }

        System.out.println("Report generated: " + file.getAbsolutePath());
        java.awt.Desktop.getDesktop().browse(file.toURI());
    }


    //Modifying, Encoding and Operations
    public static class DataSet {
        public String[][] dataMatrix;

        public DataSet(String[][] dataMatrix) {
            this.dataMatrix = dataMatrix;
        }

        public String[][] getDataMatrix() {
            return dataMatrix;
        }

        // Store mappings per column: colIndex → (category → index)
        public Map<Integer, Map<String, Integer>> encodingMaps = new HashMap<>();

        // Encode Strings or non num formats
        public void EncodeCol(int colIndex) {
            if (dataMatrix == null || dataMatrix.length < 2 || colIndex >= dataMatrix[0].length) {
                System.out.println("Invalid data or column index for label encoding.");
                return;
            }

            Map<String, Integer> categoryToIndex = new HashMap<>();
            int currentIndex = 0;

            // Skip header at row 0
            for (int row = 1; row < dataMatrix.length; row++) {
                String category = dataMatrix[row][colIndex];
                if (!categoryToIndex.containsKey(category)) {
                    categoryToIndex.put(category, currentIndex++);
                }
            }

            // Replace with indices
            for (int row = 1; row < dataMatrix.length; row++) {
                String category = dataMatrix[row][colIndex];
                dataMatrix[row][colIndex] = String.valueOf(categoryToIndex.get(category));
            }

            System.out.println("Column " + colIndex + " label encoded with " + categoryToIndex.size() + " categories.");
        }

        //Decode Encoded Column
        public void DecodeCol(int colIndex) {
            if (!encodingMaps.containsKey(colIndex)) {
                System.out.println("No encoding map found for column " + colIndex);
                return;
            }

            Map<String, Integer> categoryToIndex = encodingMaps.get(colIndex);

            // Create reverse map: index → category
            Map<String, String> indexToCategory = new HashMap<>();
            for (Map.Entry<String, Integer> entry : categoryToIndex.entrySet()) {
                indexToCategory.put(String.valueOf(entry.getValue()), entry.getKey());
            }

            for (int row = 1; row < dataMatrix.length; row++) {
                String index = dataMatrix[row][colIndex];
                if (indexToCategory.containsKey(index)) {
                    dataMatrix[row][colIndex] = indexToCategory.get(index);
                } else {
                    System.out.println("Warning: Unknown index '" + index + "' at row " + row);
                }
            }

            System.out.println("Column " + colIndex + " decoded back to original categories.");
        }

        // Normalize numeric column values to range [0,1] using Min-Max scaling
        public void normalizeColumn(int colIndex) {
            if (dataMatrix == null || dataMatrix.length < 2 || colIndex >= dataMatrix[0].length) {
                System.out.println("Invalid data or column index for normalization.");
                return;
            }

            double min = Double.MAX_VALUE;
            double max = -Double.MAX_VALUE;

            // Find min and max (skip header)
            for (int row = 1; row < dataMatrix.length; row++) {
                try {
                    double val = Double.parseDouble(dataMatrix[row][colIndex]);
                    if (val < min) min = val;
                    if (val > max) max = val;
                } catch (NumberFormatException e) {
                    // skip non-numeric or missing values
                }
            }

            double range = max - min;
            if (range == 0) {
                System.out.println("Cannot normalize column with zero range.");
                return;
            }

            // Normalize values to [0,1]
            for (int row = 1; row < dataMatrix.length; row++) {
                try {
                    double val = Double.parseDouble(dataMatrix[row][colIndex]);
                    double normVal = (val - min) / range;
                    dataMatrix[row][colIndex] = String.valueOf(normVal);
                } catch (NumberFormatException e) {
                    // skip non-numeric or missing values
                }
            }

            System.out.println("Column " + colIndex + " normalized to [0,1].");
        }

        // Save current dataMatrix to a CSV file
        public void saveToCsv(String filePath) throws IOException {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
                for (String[] row : dataMatrix) {
                    String line = String.join(",", row);
                    bw.write(line);
                    bw.newLine();
                }
            }
            System.out.println("Saved modified data to: " + filePath);
        }
    }

    public static void main(String[]args) throws IOException{

           //Test Locally

    }
    
}
