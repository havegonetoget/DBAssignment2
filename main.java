import java.io.IOException;
import java.util.Scanner;

public class main {
    static DataBase database = new DataBase();
    static Scanner scanner = new Scanner(System.in);
    static String filename;

    public static void create_new_DB() {
        System.out.print("Name of new database? >> ");
        filename = scanner.nextLine();
        try {
            database.createDB(filename);    
        } catch(IOException e) {
            System.out.println("Error " + e);
        }
    }

    public static void open_DB() {
        System.out.print("Open database >> ");
        filename = scanner.nextLine();
        database.open(filename);
    }

    public static void display_record() {
        System.out.print("Choose a record (1 - " + database.num_records + ") >> ");
            int chosen_record = scanner.nextInt();
            scanner.nextLine();
        System.out.print(chosen_record + ". ");
        System.out.println(database.readRecord(chosen_record).toString());
    }

    public static void update_record() {
        //Make option for user to choose what to overwrite
        System.out.print("Which record are you changing >> ");
        int record_num = scanner.nextInt();
        scanner.nextLine();
        Record record = database.readRecord(record_num);
        System.out.print("New state >> ");
        String state = scanner.nextLine();
        System.out.print("New City >> ");
        String city = scanner.nextLine();
        System.out.print("New name >> ");
        String name = scanner.nextLine();

        database.overwriteRecord(record_num, record.college_ID, state, city, name);
    }

    public static void display_report() {
       for (int i = 1; i <= 10; i++ ){
        
       }
        
    }

    public static void delete_Record() {   //finished
        if (database.openFlag) {
            System.out.print("Which record would you like to delete >> ");
            int record_num = scanner.nextInt();
            scanner.nextLine(); // to consume the newline character left by nextInt
    
            // Validate if record_num is in range
            if (record_num <= database.NUM_RECORDS && record_num > 0) {
                // Delete the record
                Record recordToDelete = database.readRecord(record_num);
                database.deleteRecord(filename, recordToDelete, record_num);
    
                // Mark the record as empty
                recordToDelete.makeEmpty();
    
                // You can print the status of the record after deletion
                System.out.println("Record deleted: " + recordToDelete.isEmpty());
    
                // Recalculate and update NUM_RECORDS after deletion
                database.NUM_RECORDS--; // Decrease the number of records
                try{
                database.writeConfigFile();
            } catch (IOException e) {
                e.printStackTrace();
            } // Save the updated config file
    
            } else {
                System.out.println("Not in range!\n");
            }
        }
    }

    public static void main(String[] args) {
        while(true) {
            if(database.openFlag) {
                database.print_size();
            }
            System.out.println("1) Create new database");
            System.out.println("2) Open database");
            System.out.println("3) Close database");
            System.out.println("4) Display record");
            System.out.println("5) Update record");
            System.out.println("6) Create report");
            System.out.println("7) Add record");
            System.out.println("8) Delete record");
            System.out.println("9) Quit");
            System.out.print("    >> ");
            int choice = 0;
            
        
            
            
    
            // Loop until user enters a valid integer
            while (true) {
                String input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    System.out.println("Nothing entered, please try again!");
                    continue; 
                }
            try{
                choice = Integer.parseInt(input);

                if (choice > 0 && choice <= 9){
                    break;
                }
                else {
                    System.out.println("Please enter choice from 1-9!");
                }
            } catch (NumberFormatException e){
                System.out.println("That is not an integer, please try again!");
            }
        }


       

            switch(choice) {
                case 1:
                    if(!database.openFlag) {
                        System.out.println("Creating new database...");
                        create_new_DB();
                    } else {
                        System.out.println("Error: DB already open...");
                    }
                    break;
                case 2:
                    if(!database.openFlag) {
                        System.out.println("Opening database...");
                        open_DB();
                    } else {
                        System.out.println("Database already opened, please close other database first...\n");
                    }
                    break;
                case 3:
                    System.out.println("Closing database...");
                    database.close();
                    break;
                case 4:
                    System.out.println("Displaying record...");
                    display_record();
                    break;
                case 5:
                    update_record();
                    break;
                case 6:
                    System.out.println("Creating report...");
                    display_report();
                    break;
                case 7:
                    System.out.println("Adding record(s)...");
                    break;
                case 8:
                    delete_Record();
                    break;
                case 9:
                    System.out.println("Exiting Program...");
                    scanner.close();
                    break;
                default:
                    choice = 9;
                    break;

            }

            if(choice == 9) {
                break;
            }
        }
    }
}
