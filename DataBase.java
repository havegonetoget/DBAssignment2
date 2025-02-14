import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import javax.swing.JSpinner.NumberEditor;

public class DataBase {
    public long NUM_RECORDS;
    public int RECORD_SIZE;   //removed value placed into config
    public String FILE_NAME;

    private RandomAccessFile Dinout;
    public int num_records;
    private String college_ID;
    private String state;
    private String city;
    private String name;
    public boolean openFlag;

    public DataBase() {
        this.Dinout = null;
        this.num_records = 0;
        this.college_ID = "000000";
        this.state = "State";
        this.city = "City";
        this.name = "Name";
        this.openFlag = false;
        this.NUM_RECORDS = 0;
    }

    public void print_size() {
        try {
            System.out.println("File length: " + this.Dinout.length());
            System.out.println("Number of Records: " + NUM_RECORDS);   //changed from (this.Dinout.length() / RECORD_SIZE)
        } catch (IOException e) {
            System.out.println("Couldn't get length of file");
        }
    }

    public void writeConfigFile() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME + ".config"))) {
            writer.println("# Database Configuration File for " + FILE_NAME);
            writer.println("# This file contains metadata and settings for the database system.");
            writer.println();
            writer.println("NUM_RECORDS=" + NUM_RECORDS);
            writer.println("RECORD_SIZE=" + RECORD_SIZE);
            writer.println("FILE_NAME=" + FILE_NAME);
            writer.println("DATABASE_VERSION=1.0");
            writer.println("DATABASE_INITIALIZED=" + openFlag);
        }
    }

    public void readConfigFile() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME + ".config"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("NUM_RECORDS=")) {
                    NUM_RECORDS = Long.parseLong(line.split("=")[1]);
                    this.num_records = (int)Long.parseLong(line.split("=")[1]);
                } else if (line.startsWith("RECORD_SIZE=")) {
                    RECORD_SIZE = Integer.parseInt(line.split("=")[1]);
                } else if (line.startsWith("FILE_NAME=")) {
                    FILE_NAME = line.split("=")[1];
                } else if (line.startsWith("DATABASE_VERSION=")) {
                    // Process version info (optional)
                } else if (line.startsWith("DATABASE_INITIALIZED=")) {
                    openFlag = Boolean.parseBoolean(line.split("=")[1]);
                }
            }
        }
    }
    
    public void loadConfig(String configFilePath) {  //added to load the congif file
        Properties config = new Properties();
        
        try (FileInputStream configFile = new FileInputStream(configFilePath)) {
            config.load(configFile);
            
            // Load RECORD_SIZE from the config file
            String recordSizeStr = config.getProperty("RECORD_SIZE");
            if (recordSizeStr != null) {
                this.RECORD_SIZE = Integer.parseInt(recordSizeStr);  // Convert to integer
            }
            
            // Optionally, load NUM_RECORDS and FILE_NAME from the config
            String numRecordsStr = config.getProperty("NUM_RECORDS");
            if (numRecordsStr != null) {
                this.NUM_RECORDS = Long.parseLong(numRecordsStr);  // Convert to long
                this.num_records = (int)Long.parseLong(numRecordsStr);
            }
            
            String fileNameStr = config.getProperty("FILE_NAME");
            if (fileNameStr != null) {
                this.FILE_NAME = fileNameStr;
            }
        } catch (IOException e) {
            System.err.println("Error loading config file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format in config file.");
        }
    }

    public void open(String filename) {
       
        // Open file in read/write mode
        try {

            FILE_NAME = filename;
            readConfigFile();
          this.Dinout = new RandomAccessFile(filename+".data", "rw");
          this.openFlag = true;
          System.out.println("Opening [" + filename + ".data] ...\n");
        } catch (IOException e) {
          System.out.println("Could not open file\n");
          e.printStackTrace();
        } 
    }

    public void close() {
        try {
          Dinout.close();
          this.openFlag = false;
          System.out.println("Database successfully closed...");
        } catch (IOException e) {
          System.out.println("There was an error while attempting to close the database file.\n");
          e.printStackTrace();
        }
    }

    public void createDB(String filename) throws IOException {
        RandomAccessFile Din = new RandomAccessFile(filename+".csv", "r");
        RandomAccessFile dataFile = new RandomAccessFile(filename+".data","rw"); 
        String line;
        loadConfig(filename + ".config");
        while ((line = Din.readLine()) != null) {
            String[] attribute = line.split(",");
            writeRecord(dataFile, attribute[0], attribute[1], attribute[2], attribute[3]);
        }
        try {
            NUM_RECORDS = dataFile.length() / RECORD_SIZE;
            writeConfigFile();
        } catch (IOException e) {
            System.out.println(e);
        }
        dataFile.close();
    }

    public void writeRecord(RandomAccessFile file, String college_ID, String state, String city, String name) {
            //format input values to be put in record
            this.college_ID = String.format("%-8s", college_ID.length() > 8 ? college_ID.substring(0, 8) : college_ID);
            this.state = String.format("%-26s", state.length() > 26 ? state.substring(0, 26) : state);
            this.city = String.format("%-25s", city.length() > 25 ? city.substring(0, 25) : city);
            this.name = String.format("%-77s", name.length() > 77 ? name.substring(0, 77) : name);
        try {
            file.writeBytes(this.college_ID + this.state + this.city + this.name + System.lineSeparator());
            //System.out.println("Writing record length: " + (this.college_ID + this.state + this.city + this.name + System.lineSeparator()).length());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            NUM_RECORDS = file.length() / RECORD_SIZE; 
            writeConfigFile();  
        } catch (IOException e){
            System.err.println("Error calc num records. ");
        }
    }

    public Record readRecord(int record_num) {
      Record university = new Record();

        try (RandomAccessFile file = new RandomAccessFile(new File(FILE_NAME+".data"), "rw")) {
          long bytePosition = (record_num - 1) * RECORD_SIZE; // Calculate exact position

          if (bytePosition >= 0 && bytePosition + RECORD_SIZE <= file.length()) {
              String fields[] = new String[4];

              file.seek(bytePosition); // Jump directly to the requested line
              
              byte[] buffer = new byte[RECORD_SIZE];
              file.readFully(buffer); // Read exactly 138 bytes
              
              String rawData = new String(buffer);

              //Extract fields based on fixed-width positions
              String collegeID = rawData.substring(0, 8);
              String state = rawData.substring(8, 34);
              String city = rawData.substring(34, 59);
              String universityName = rawData.substring(59, 136);

              //Create and print the selected university record
              fields[0] = collegeID;
              fields[1] = state;
              fields[2] = city;
              fields[3] = universityName;

              // Update the fields and print the record
              university.updateFields(fields);
          } else {
              System.out.println("Line number " + record_num + " is out of range.");
          }
      } catch (FileNotFoundException e) {
          System.err.println("File not found: " + FILE_NAME+".data");
      } catch (IOException e) {
          e.printStackTrace();
      }
      return university;
    }

    // Understand options of which the user wants to change and then change it.
    public void overwriteRecord(int record_num, String college_ID, String state, String city, String name) {
        if ((record_num >= 0) && (record_num < this.num_records)) {
          try {
            record_num = record_num-1;
            Dinout.seek(0); // return to the top of the file
            Dinout.skipBytes(record_num * RECORD_SIZE);
        //overwrite the specified record
            writeRecord(Dinout, college_ID, state, city, name);
            System.out.println("Record Successfully Overwritten");
          } catch (IOException e) {
            System.out.println("There was an error while attempting to overwrite a record from the database file.\n");
            e.printStackTrace();
          }
        }
      }


    public void deleteRecord(String filename, Record record, int record_num) {   
       
        overwriteRecord(record_num, record.college_ID, "", "", ""); 
        //record.makeEmpty(); //this could possibly cause errors becasue overwrite does the same as this hypotheticall
                            //maybe this consider for data not sure
    }
    
    public void deleteRecord_backup(String filename, int record_num) {
        Path filepath = Paths.get(filename+".data");
        File inputFile = new File(filename + ".data");
        File tempFile = new File("temp.data");

        try(BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

                String current_line;
                int current_record = 1;

                while ((current_line = reader.readLine()) != null) {
                    if (current_record != record_num) {  // Skip the line to be deleted
                        writer.write(current_line);
                        writer.newLine();
                    }
                    current_record++;
                }
        } catch (IOException e) {
            System.out.println("Error while deleting record.");
            e.printStackTrace();
            return;
        }
        
        //Clearing Content Section
        if (Dinout != null) {
            try {
                Dinout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            boolean deleted = Files.deleteIfExists(filepath);
            if (deleted) {
                //System.out.println("File deleted successfully");
            } else {
                System.out.println("File does not exist");
            }
        } catch (Exception e) {
            System.err.println("Error deleting file: " + e.getMessage());
        }

        if(tempFile.renameTo(inputFile)) {
            //System.out.println("File renamed Successfully");
        } else {
            System.out.println("Error: could not rename file.");
        }

        try {
          this.Dinout = new RandomAccessFile(filename+".data", "rw");
        } catch (FileNotFoundException e) {
            e.getMessage();
        }

      
    }
}
