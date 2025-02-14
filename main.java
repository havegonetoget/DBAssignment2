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
        int skipped_lines=0;
        for(int i = 1; i <= 10; i++) {
            //if(!database.readRecord(i).isEmpty()) {
                System.out.print((i-skipped_lines)+". ");
                System.out.print(database.readRecord(i).toString()+"\n");
                System.out.println();
            // } else {
            //     skipped_lines++;
            // }
        }
    }

    public static void delete_Record() {   //finished
        if(database.openFlag) {
            
            System.out.print("Which record would you like to delete >> ");
                 int record_num = scanner.nextInt();
                 scanner.nextLine();

            database.deleteRecord(filename, database.readRecord(record_num), record_num); //takes desired record and uses record obj to change

            
            database.readRecord(record_num).empty = true;

            System.out.println(database.readRecord(record_num).isEmpty()); 
            
            
        //     Boolean decision_made = false;


        //     while(!decision_made) {
        //     System.out.print("Which record would you like to delete >> ");
        //     int record_num = scanner.nextInt();
        //     scanner.nextLine();
            
        //     System.out.println(database.readRecord(record_num).toString());
        //     System.out.print("Is this the record you'd like to delete? (Y/N)\n(Press Enter to quit) >> ");
        //     String choice = scanner.nextLine();

        //         if(choice.equalsIgnoreCase("Y")) {
        //             //Call Delete Record in DataBase
        //             decision_made = true;
        //             database.readRecord(record_num).makeEmpty();
        //             System.out.println(database.readRecord(record_num).isEmpty());

                    
        //         } else if (choice.equalsIgnoreCase("N")) {
        //             System.out.println("Please try again...");
        //         } else if (choice.isEmpty()) {
        //             break;
        //         }
        //     }
        // } else {
        //     System.out.println("Database not open, please open one and try again");
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
            int choice;
            
        
        do {
            
            choice = scanner.nextInt();
            scanner.nextLine();

            if(choice > 9 || choice < 1 )
                System.out.print("Not a valid option please try again! >> ");
        
        } while (choice > 9 || choice < 1);


       

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
