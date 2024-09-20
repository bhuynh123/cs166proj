/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;
import java.lang.Character.Subset;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class GameRental {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of GameRental store
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public GameRental(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end GameRental

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            GameRental.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      GameRental esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the GameRental object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new GameRental (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. View Profile");
                System.out.println("2. Update Profile");
                System.out.println("3. View Catalog");
                System.out.println("4. Place Rental Order");
                System.out.println("5. View Full Rental Order History");
                System.out.println("6. View Past 5 Rental Orders");
                System.out.println("7. View Rental Order Information");
                System.out.println("8. View Tracking Information");

                //the following functionalities basically used by employees & managers
                System.out.println("9. Update Tracking Information");

                //the following functionalities basically used by managers
                System.out.println("10. Update Catalog");
                System.out.println("11. Update User");

                System.out.println(".........................");
                System.out.println("20. Log out");
                switch (readChoice()){
                   case 1: viewProfile(esql); break;
                   case 2: updateProfile(esql); break;
                   case 3: viewCatalog(esql); break;
                   case 4: placeOrder(esql); break;
                   case 5: viewAllOrders(esql, authorisedUser); break;
                   case 6: viewRecentOrders(esql, authorisedUser); break;
                   case 7: viewOrderInfo(esql, authorisedUser); break;
                   case 8: viewTrackingInfo(esql, authorisedUser); break;
                   case 9: updateTrackingInfo(esql, authorisedUser); break;
                   case 10: updateCatalog(esql, authorisedUser); break;
                   case 11: updateUser(esql, authorisedUser); break;



                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
	 } //end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user
    **/
    //add option for when no input is in
   public static void CreateUser(GameRental esql){

	    try {

      System.out.print("\tEnter login: ");
      String login = in.readLine();

      System.out.print("\tEnter password: ");
      String password = in.readLine();

      System.out.print("\tEnter phone number: ");
      String phoneNum = in.readLine();


      // Insert the new user into the Users table
      String query = String.format("INSERT INTO Users (login, password, role, favGames, phoneNum, numOverDueGames) " +
                                   "VALUES ('%s', '%s', 'customer', '', '%s', 0);",
                                   login, password, phoneNum);
      esql.executeUpdate(query);
      System.out.println("User successfully added!");

      }

      catch (Exception e) {

         System.out.println("Your input is invalid!");

      };
   }
   //end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(GameRental esql){
      try {

         String userLogin = "";
         String userPassword = "";
         boolean loginSuc = false;

         do {
            System.out.println("Enter login: ");
            userLogin = in.readLine();
            System.out.println("Enter password: ");
            userPassword = in.readLine();
          

            String query = String.format( 
                  "SELECT * " +
                  "FROM Users " +
                  "WHERE login = '%s' AND password = '%s'; ",
                  userLogin, userPassword);
                  int userNum = esql.executeQuery(query);

                  if (userNum > 0) {

                     System.out.println("Logged In!");
                     loginSuc = true;

                  }

                  else {
                     System.out.println("Invalid Login");
                     System.out.println("Would you like to try again? (Yes/No)");
                     
                     String userAnswer = in.readLine();

                     if (userAnswer.equalsIgnoreCase("No")) {
                        System.out.println("Returning");
                        return null;
                     }

                     
                  }
               } while(!loginSuc);

               return userLogin;

      }
      catch (Exception e) {
         System.out.println("Your input is invalid!");
         return null;
      }
      
   }//end

// Rest of the functions definition go in here

   public static void viewProfile(GameRental esql) {

      try {
         
         System.out.println("Enter login: ");
         String userLogin = in.readLine();
         String query = String.format("SELECT * FROM Users WHERE login = '%s'", userLogin);
         List<List<String>> userInfo = esql.executeQueryAndReturnResult(query);
         List<String> proccessedUserInfo = userInfo.get(0);

         System.out.println("Profile: ");
         //login
         System.out.print("Login: ");
         System.out.println(proccessedUserInfo.get(0));
         //role
         System.out.print("Role: ");
         System.out.println(proccessedUserInfo.get(2));
         //fav game
         System.out.print("Favorite Games: ");
         System.out.println(proccessedUserInfo.get(3));
         //phonenum
         System.out.print("Phone Number: ");
         System.out.println(proccessedUserInfo.get(4));
         //overdue game
         System.out.print("Overdue Games: ");
         System.out.println(proccessedUserInfo.get(5));

        //

      }
       catch (Exception e) {
         System.out.println("Not found");
      }

   }
   public static void updateProfile(GameRental esql) {
     
      try{

         String userLogin = "";
         String userPassword = "";
         boolean loginSuc = false;

         do {
            System.out.println("Renter login and password to update profile");
            System.out.println("Enter login: ");
            userLogin = in.readLine();
            System.out.println("Enter password: ");
            userPassword = in.readLine();
          

            String query = String.format( 
                  "SELECT * " +
                  "FROM Users " +
                  "WHERE login = '%s' AND password = '%s'; ",
                  userLogin, userPassword);
                  int userNum = esql.executeQuery(query);

                  if (userNum > 0) {

                     System.out.println("Successful!");
                     loginSuc = true;

                  }

                  else {
                     System.out.println("Invalid Login");
                     System.out.println("Would you like to try again? (Yes/No)");
                     
                     String userAnswer = in.readLine();

                     if (userAnswer.equalsIgnoreCase("No")) {
                        System.out.println("Returning");
                        return;
                     }

                     
                  }
               } while(!loginSuc);

               System.out.println("Options: ");
               System.out.println("1. Update Favorite Games");
               System.out.println("2. Change Password");
               System.out.println("3. Change Phone Number");
               System.out.println("Enter Option: ");
               int choice = Integer.parseInt(in.readLine());

               String query;

               if (choice == 1) {

                  System.out.println("Enter Additional Favorite Game");
                  String game = in.readLine();
                  String updateQuery = String.format("UPDATE Users" +
                     " SET favGames = favGames || ', %s'" +
                     " WHERE login = '%s';", 
                     game, userLogin
                     );

                     esql.executeUpdate(updateQuery);
                     System.out.println("Game Added");
               }
               else if (choice == 2) {
                  System.out.println("Enter New Password");
                  String newPassword = in.readLine();
                  String updateQuery = String.format(
                     "UPDATE Users SET password = '%s'" +
                     " WHERE  login = '%s';",
                     newPassword, userLogin
                  );

                  esql.executeUpdate(updateQuery);

               }
               else if (choice == 3) {
                  System.out.println("Enter New Phone Number");

                  String newPNum = in.readLine();
                  String updateQuery = String.format(
                     "UPDATE Users SET phoneNums = '%s'" +
                     " WHERE login = '%s';", 
                     newPNum, userLogin
                     );

                  esql.executeUpdate(updateQuery);
               }
         }
         catch (Exception e) {
            System.out.println("Invalid");
         }

   }
   public static void viewCatalog(GameRental esql) {

      try {

         //menu
         System.out.println("1. View All ");

         System.out.println("2. Filters by Genre");

         System.out.println("3. Filter by Price");

         System.out.println("4. Sort by Highest to Lowest Price");

         System.out.println("5. Sort by Lowest to Highest Price");

         int choice = readChoice();
         //start query
         String query = "SELECT * FROM Catalog";
         
         //raeads user
         if (choice == 1) {
            //place 
         }

         else if (choice == 2) {

            System.out.println("Enter Genre: ");
            String genre = in.readLine();
            query += String.format(" WHERE genre = '%s'", genre);

         }

         else if (choice == 3) {

            System.out.println("Enter MAX Price");
            double max = Double.parseDouble(in.readLine());
            query += String.format(" WHERE price <= %f", max);

         }

         else if (choice == 4) {

            query += " ORDER BY price DESC";

         }

         else if (choice == 5) {

            query += " ORDER BY price ASC";

         }

         else {
            System.out.println("Invalid Choice");
            return;
         }

         int rows = esql.executeQueryAndPrintResult(query);

         if (rows == 0) {

            System.out.println("No Results");

         }
      }

      catch (Exception e) {
            System.out.println("Your input is invalid!");
      }
   }
   public static void placeOrder(GameRental esql) {
      int rentalOrderIDpos = 5000;
      try {
         System.out.println("Enter login to rent games to: ");
         String login = in.readLine();
 
         List<String> gameIDs = new ArrayList<>();
         List<Integer> unitsOrdered = new ArrayList<>();
         double totalPrice = 0.0;
         boolean continueAction = true;
 
         while (continueAction) {
             System.out.print("Enter gameID: ");
             String gameID = in.readLine();
             System.out.print("Enter unitsOrdered: ");
             int units = Integer.parseInt(in.readLine());
 
             String priceQuery = String.format(
                     "SELECT price " +
                             "FROM Catalog " +
                             "WHERE gameID = '%s'",
                     gameID
             );
 
             List<List<String>> gamePrice = esql.executeQueryAndReturnResult(priceQuery);
             if (gamePrice.isEmpty()) {
                 System.out.println("Invalid gameID");
                 continue;
             }
 
             double price = Double.parseDouble(gamePrice.get(0).get(0));
             totalPrice += price * units;
 
             gameIDs.add(gameID);
             unitsOrdered.add(units);
 
             System.out.print("Do you want to add another game? (yes/no): ");
             String response = in.readLine();
             if (response.equalsIgnoreCase("no")) {
                 continueAction = false;
             }
         }
         rentalOrderIDpos++;
         int newRentalOrderID = rentalOrderIDpos;
         String rentalOrderID = "gamerentalorder" + newRentalOrderID;
         String orderTimestamp = "'2025-06-05 09:00:00'";
         String dueDate = "'2025-07-05'";

     
         
         System.out.println("Generated rentalOrderID: " + rentalOrderID);
         System.out.println("Order Timestamp: " + orderTimestamp);
         System.out.println("Due Date: " + dueDate);
 
         String placeOrderQuery = String.format(
                 "INSERT INTO RentalOrder (rentalorderid, login, noOfGames, totalprice, orderTimestamp, dueDate) " +
                         "VALUES ('%s', '%s', %d, %.2f, %s, %s)",
                 rentalOrderID, login, gameIDs.size(), totalPrice, orderTimestamp, dueDate
         );
 
         esql.executeUpdate(placeOrderQuery);
 
         for (int i = 0; i < gameIDs.size(); i++) {
             String gameID = gameIDs.get(i);
             int units = unitsOrdered.get(i);
             String gamesInOrderQuery = String.format(
                     "INSERT INTO GamesInOrder (gameID, rentalOrderID, unitsOrdered) " +
                             "VALUES ('%s', '%s', %d)",
                     gameID, rentalOrderID, units
             );
             esql.executeUpdate(gamesInOrderQuery);
         }
 
         String trackingID = "trackingid" + rentalOrderIDpos;
         String status = "Order Placed";
         String currentLocation = "N/A";
         String courierName = "N/A";
         String additionalComments = "";
 
         String trackingInfoQuery = String.format(
                 "INSERT INTO TrackingInfo (trackingID, rentalOrderID, status, currentLocation, courierName, lastUpdateDate, additionalComments) " +
                         "VALUES ('%s', '%s', '%s', '%s', '%s', %s, '%s')",
                 trackingID, rentalOrderID, status, currentLocation, courierName, orderTimestamp, additionalComments
         );
         esql.executeUpdate(trackingInfoQuery);
 
         System.out.println("Rental order placed successfully!");
         System.out.printf("Total price: %.2f\n", totalPrice);

      } 
      catch (Exception e) {
         System.out.println("Invalid");
      }
   }
   public static void viewAllOrders(GameRental esql, String username) {
      try {
   		String viewAllOrderQuery = String.format("SELECT rentalOrderID FROM RentalOrder WHERE login = '%s'", username);

		List<List<String>> orderHistory = esql.executeQueryAndReturnResult(viewAllOrderQuery);

		if (orderHistory.size() == 0) {
			System.out.println("Rental history not found");
		} else {
			esql.executeQueryAndPrintResult(viewAllOrderQuery);
		}
	} catch (Exception e) {
		System.out.println("Error viewing rental history");
		System.err.println(e.getMessage());
	}
   }
   public static void viewRecentOrders(GameRental esql, String username) {
      try {
         String viewAllOrderQuery = String.format("SELECT rentalOrderID FROM RentalOrder WHERE login = '%s' ORDER BY orderTimestamp DESC LIMIT 5", username);
   
                   List<List<String>> orderHistory = esql.executeQueryAndReturnResult(viewAllOrderQuery);
   
                   if (orderHistory.size() == 0) {
                           System.out.println("Rental history not found");
                   } else {
                           esql.executeQueryAndPrintResult(viewAllOrderQuery);
                   }
      } catch (Exception e) {
         System.out.println("Error viewing recent rental history");
         System.err.println(e.getMessage());
      }
   }
   public static void viewOrderInfo(GameRental esql, String username) {
      System.out.println("Please enter your rental order ID: ");
	String inputOrderID = "";

	try {
		inputOrderID = in.readLine();
                String viewOrderQuery = String.format("SELECT r.orderTimestamp, r.dueDate, r.totalPrice, t.trackingID, c.gameName FROM GamesInOrder g JOIN Catalog c ON g.gameID = c.gameID JOIN TrackingInfo t ON g.rentalOrderID = t.rentalOrderID JOIN RentalOrder r ON g.rentalOrderID = r.rentalOrderID WHERE r.rentalOrderID = '%s' AND r.login = '%s'", inputOrderID, username);

                List<List<String>> orderHistory = esql.executeQueryAndReturnResult(viewOrderQuery);

		String userRoleQuery = String.format("SELECT role FROM Users WHERE login = '%s'", username);
                List<List<String>> userRole = esql.executeQueryAndReturnResult(userRoleQuery);

                String role = userRole.get(0).get(0).trim();

                if (orderHistory.size() > 0) {
                        esql.executeQueryAndPrintResult(viewOrderQuery);
		} else if (role.equals("manager") || role.equals("employee")) {
		        String viewOrderQuery1 = String.format("SELECT r.orderTimestamp, r.dueDate, r.totalPrice, t.trackingID, c.gameName FROM GamesInOrder g JOIN Catalog c ON g.gameID = c.gameID JOIN TrackingInfo t ON g.rentalOrderID = t.rentalOrderID JOIN RentalOrder r ON g.rentalOrderID = r.rentalOrderID WHERE r.rentalOrderID = '%s'", inputOrderID);
			esql.executeQueryAndPrintResult(viewOrderQuery1);
                } else {
                        System.out.println("Order ID not found");
                }
        } catch (Exception e) {
                System.out.println("Error viewing rental order");
                System.err.println(e.getMessage());
        }

   }
   public static void viewTrackingInfo(GameRental esql, String username) {
      System.out.println("Please enter your tracking ID: ");
	String inputTrackingID = "";

	try {
		inputTrackingID = in.readLine();
		String viewTrackingQuery = String.format("SELECT t.courierName, t.rentalOrderID, t.currentLocation, t.status, t.lastUpdateDate, t.additionalComments FROM TrackingInfo t JOIN RentalOrder r ON t.rentalOrderID = r.rentalOrderID WHERE t.trackingID = '%s' AND r.login = '%s'", inputTrackingID, username);

		List<List<String>> trackingHistory = esql.executeQueryAndReturnResult(viewTrackingQuery);

		String userRoleQuery = String.format("SELECT role FROM Users WHERE login = '%s'", username);
		List<List<String>> userRole = esql.executeQueryAndReturnResult(userRoleQuery);

		String role = userRole.get(0).get(0).trim();

		if (trackingHistory.size() > 0) {
			esql.executeQueryAndPrintResult(viewTrackingQuery);
		} else if (role.equals("manager") || role.equals("employee")){
			String viewTrackingQueryME = String.format("SELECT t.courierName, t.rentalOrderID, t.currentLocation, t.status, t.lastUpdateDate, t.additionalComments FROM TrackingInfo t JOIN RentalOrder r ON t.rentalOrderID = r.rentalOrderID WHERE t.trackingID = '%s'", inputTrackingID);

			esql.executeQueryAndPrintResult(viewTrackingQueryME);
		} else {
			System.out.println("Tracking information not found");
		}
	} catch (Exception e) {
		System.out.println("Error viewing tracking order");
		System.err.println(e.getMessage());
	}
   }
   public static void updateTrackingInfo(GameRental esql, String username) {
      try {
         String query = String.format("SELECT role FROM Users WHERE login = '%s' AND role IN ('manager', 'employee')", username);
   
         List<List<String>> result = esql.executeQueryAndReturnResult(query);
   
         // If they are a manager or employee
         if (!result.isEmpty()) {
            boolean validTrackingID = true;
            String trackingIDInput = "";
            while (validTrackingID) {
               System.out.println("Input tracking ID to update tracking: ");
   
               trackingIDInput = in.readLine();
   
               String query_trackingID = String.format("SELECT * FROM TrackingInfo WHERE trackingID = '%s'", trackingIDInput);
               List<List<String>> trackingID_Results = esql.executeQueryAndReturnResult(query_trackingID);
   
               if (trackingID_Results.size() > 0) {
                  validTrackingID = false;
               } else {
                  System.out.println("Invalid tracking ID. Please try again.");
               }
            }
   
            boolean updateTrackingLoop = true;
            while (updateTrackingLoop) {
               System.out.println("UPDATE TRACKING MENU");
               System.out.println("--------------------");
               System.out.println("1. Update tracking status");
               System.out.println("2. Update tracking current location");
               System.out.println("3. Update tracking courier name");
               System.out.println("4. Update tracking additional comments");
               System.out.println("9. < EXIT");
   
               int updateTrackingLoopChoice = readChoice();
   
               switch(updateTrackingLoopChoice) {
                  case 1:
                     boolean updateTrackingStatusLoop = true;
   
                                                   String newTrackingStatus = "";
                                                   while (updateTrackingStatusLoop) {
                                                           System.out.println("Please enter a new status for tracking. Character limit 50.");
   
                                                           newTrackingStatus = in.readLine();
   
                                                           if (newTrackingStatus.length() > 0 && newTrackingStatus.length() < 51) {
                                                                   updateTrackingStatusLoop = false;
                                                           } else if (newTrackingStatus.length() == 0) {
                                                                   System.out.println("Tracking status cannot be empty. Please enter a new tracking status.");
                                                           } else if (newTrackingStatus.length() > 50) {
                                                                   System.out.println("Tracking status cannot be over 50 characters. Please enter a new tracking status.");
                                                           } else {
                                                                   System.out.println("Invalid tracking status. Please enter a new tracking status.");
                                                           }
                                                   }
   
                                                   String newTrackingQuery = String.format("UPDATE TrackingInfo SET status = '%s', lastUpdateDate = CURRENT_TIMESTAMP WHERE trackingID = '%s'", newTrackingStatus, trackingIDInput);
                                                   esql.executeUpdate(newTrackingQuery);
                                                   System.out.println("New tracking status sucessfully saved for: " + trackingIDInput);
                     break;
                  case 2:
                     boolean updateCurLocLoop = true;
   
                                                   String newCurLoc = "";
                                                   while (updateCurLocLoop) {
                                                           System.out.println("Please enter a new current location for tracking. Character limit 60.");
   
                                                           newCurLoc = in.readLine();
   
                                                           if (newCurLoc.length() > 0 && newCurLoc.length() < 61) {
                                                                   updateCurLocLoop = false;
                                                           } else if (newCurLoc.length() == 0) {
                                                                   System.out.println("Current location cannot be empty. Please enter a new current location.");
                                                           } else if (newCurLoc.length() > 60) {
                                                                   System.out.println("Current location cannot be over 60 characters. Please enter a new current location.");
                                                           } else {
                                                                   System.out.println("Invalid current location. Please enter a new current location.");
                                                           }
                                                   }
   
                                                   String newCurLocQuery = String.format("UPDATE TrackingInfo SET currentLocation = '%s', lastUpdateDate = CURRENT_TIMESTAMP WHERE trackingID = '%s'", newCurLoc, trackingIDInput);
                                                   esql.executeUpdate(newCurLocQuery);
                                                   System.out.println("New current location sucessfully saved for: " + trackingIDInput);
                     break;
                  case 3:
                     boolean updateCourLoop = true;
   
                                                   String newCour = "";
                                                   while (updateCourLoop) {
                                                           System.out.println("Please enter a new courier name for tracking. Character limit 60.");
   
                                                           newCour = in.readLine();
   
                                                           if (newCour.length() > 0 && newCour.length() < 61) {
                                                                   updateCourLoop = false;
                                                           } else if (newCour.length() == 0) {
                                                                   System.out.println("Courier name cannot be empty. Please enter a new courier name.");
                                                           } else if (newCour.length() > 60) {
                                                                   System.out.println("Courier name cannot be over 60 characters. Please enter a new courier name.");
                                                           } else {
                                                                   System.out.println("Invalid courier name. Please enter a new courier name.");
                                                           }
                                                   }
   
                                                   String newCourQuery = String.format("UPDATE TrackingInfo SET courierName = '%s', lastUpdateDate = CURRENT_TIMESTAMP WHERE trackingID = '%s'", newCour, trackingIDInput);
                                                   esql.executeUpdate(newCourQuery);
                                                   System.out.println("New courier name sucessfully saved for: " + trackingIDInput);
                     break;
                  case 4:
                                                   String newAddComm = "";
                                                           System.out.println("Please enter additional comments for tracking.");
   
                                                           newAddComm = in.readLine();
   
                                                   String newAddCommQuery = String.format("UPDATE TrackingInfo SET additionalComments = '%s', lastUpdateDate = CURRENT_TIMESTAMP WHERE trackingID = '%s'", newAddComm, trackingIDInput);
                                                   esql.executeUpdate(newAddCommQuery);
                                                   System.out.println("New additional comments sucessfully saved for: " + trackingIDInput);
                     break;
                  case 9:
                     updateTrackingLoop = false;
                     break;
               }
            }
         } else {
            System.out.println("User is not manager or employee. Unable to update tracking info");
         }
      } catch (Exception e) {
         System.out.println("Error updating tracking info");
         System.err.println(e.getMessage());
      }
   }
   public static void updateCatalog(GameRental esql, String username) {
      try {
         String query = String.format("SELECT role FROM Users WHERE login = '%s' AND role = 'manager'", username);

         List<List<String>> result = esql.executeQueryAndReturnResult(query);

// If they are a manager
         if (!result.isEmpty()) {
  boolean validGameID = true;
  String gameIDInput = "";
  while (validGameID) {
     System.out.println("Input gameID to update catalog: ");

     gameIDInput = in.readLine();

     String query_gameID = String.format("SELECT * FROM Catalog WHERE gameID = '%s'", gameIDInput);
     List<List<String>> gameID_Results = esql.executeQueryAndReturnResult(query_gameID);

     if (gameID_Results.size() > 0) {
        validGameID = false;
     } else {
        System.out.println("Invalid gameID. Please try again.");
     }
  }
  
  boolean updateCatalogLoop = true;
  while (updateCatalogLoop) {
     System.out.println("UPDATE CATALOG MENU");
     System.out.println("-------------------");
     System.out.println("1. Update game name");
     System.out.println("2. Update game genre");
     System.out.println("3. Update game price");
     System.out.println("4. Update game description");
     System.out.println("5. Update game imageURL");
     System.out.println("9. < EXIT");

     int updateCatalogLoopChoice = readChoice();

     switch(updateCatalogLoopChoice) {
        case 1:
           boolean updateGameNameLoop = true;
              
           String newGameName = "";
           while (updateGameNameLoop) {
              System.out.println("Please enter a new name for the game. Character limit 300.");
              
              newGameName = in.readLine();

              if (newGameName.length() > 0 && newGameName.length() < 301) {
                 updateGameNameLoop = false;
              } else if (newGameName.length() == 0) {
                 System.out.println("Game name cannot be empty. Please enter a new game name.");
              } else if (newGameName.length() > 300) {
                 System.out.println("Game name cannot be over 300 characters. Please enter a new game name.");
              } else {
                 System.out.println("Invalid game name. Please enter a new game name.");
              }
           }

              String newGameNameQuery = String.format("UPDATE Catalog SET gameName = '%s' WHERE gameID = '%s'", newGameName, gameIDInput);
              esql.executeUpdate(newGameNameQuery);
              System.out.println("New game name sucessfully saved for: " + gameIDInput);
           break;
        case 2:
           boolean updateGameGenreLoop = true;

           String newGameGenre = "";
           while (updateGameGenreLoop) {
                                            System.out.println("Please enter a new genre for the game. Character limit 30.");
                                                 newGameGenre = in.readLine();

                                                 if (newGameGenre.length() > 0 && newGameGenre.length() < 31) {
                                                         updateGameGenreLoop = false;
                                                 } else if (newGameGenre.length() == 0) { 
                                                         System.out.println("Game genre cannot be empty. Please enter a new game genre.");
                                                 } else if (newGameGenre.length() > 30) {
                                                         System.out.println("Game genre cannot be over 30 characters. Please enter a new game genre.");
                                                 } else {
                                                         System.out.println("Invalid game genre. Please enter a new game genre.");
                                                 }    
           }
              String newGameGenreQuery = String.format("UPDATE Catalog SET genre = '%s' WHERE gameID = '%s'", newGameGenre, gameIDInput);
              esql.executeUpdate(newGameGenreQuery);
              System.out.println("New game genre sucessfully saved for: " + gameIDInput);
           break;
        case 3:
           String newPriceInput = "";
           System.out.println("Please enter a new price for the game.");
           newPriceInput = in.readLine();

           double newPrice = Double.parseDouble(newPriceInput);
           String newPriceQuery = String.format("UPDATE Catalog SET price = %.2f WHERE gameID = '%s'", newPrice, gameIDInput);
           System.out.println("New game price sucessfully saved for: " + gameIDInput);
           break;
        case 4:
           String newDescription;
           System.out.println("Please enter a new description for the game.");

           newDescription = in.readLine();

           String newGameDescriptionQuery = String.format("UPDATE Catalog SET description = '%s' WHERE gameID = '%s'", newDescription, gameIDInput);
           esql.executeUpdate(newGameDescriptionQuery);
           System.out.println("New game description sucessfully saved for: " + gameIDInput);

           break;
        case 5:
           boolean updateImageURLLoop = true;

           String newImageURL = "";
           while (updateImageURLLoop) {
                                       System.out.println("Please enter a new image URL for the game. Character limit 20.");
                                                 newImageURL = in.readLine();

                                                 if (newImageURL.length() > 0 && newImageURL.length() < 21) {
                                                         updateImageURLLoop = false;
                                                 } else if (newImageURL.length() == 0) {
                                                         System.out.println("Game image URL cannot be empty. Please enter a new game image URL.");
                                                 } else if (newImageURL.length() > 20) {
                                                         System.out.println("Game image URL cannot be over 20 characters. Please enter a new game image URL.");
                                                 } else {
                                                         System.out.println("Invalid game image URL. Please enter a new game image URL.");
                                                 }
           }
              String newImageURLQuery = String.format("UPDATE Catalog SET imageURL = '%s'WHERE gameID = '%s'", newImageURL, gameIDInput);
              esql.executeUpdate(newImageURLQuery);
              System.out.println("New game image URL sucessfully saved for: " + gameIDInput);
           break;
        case 9:
           updateCatalogLoop = false;
           break;
        default:
           System.out.println("Invalid menu choice. Please try again.");
     }
  }


} else {
  System.out.println("User is not manager. Unable to update catalog.");
}
} catch (Exception e) {
         System.out.println("Error");
         System.err.println(e.getMessage());
 }
   }
   public static void updateUser(GameRental esql, String username) {
      try {
         String query = String.format("SELECT role FROM Users WHERE login = '%s' AND role = 'manager'", username);
   
         List<List<String>> result = esql.executeQueryAndReturnResult(query);
   
         // If they are a manager
         if (!result.isEmpty()) {
            System.out.println("Enter login of user to update: ");
   
            String input;
            input = in.readLine();
   
            System.out.println("UPDATE USER MENU");
            System.out.println("----------------");
            System.out.println("1. Password");
            System.out.println("2. Role");
            System.out.println("3. Favorite games");
            System.out.println("4. Phone number");
            System.out.println("5. Number of overdue games");
            System.out.println("6. View user");
            System.out.println("9. < EXIT");
   
            int choice = readChoice();
   
            switch(choice) {
               case 1:
                                      try {
                                              String password;
      
                                              while(true) {
                                                      System.out.print("Enter new password (must be 30 characters or less): ");
                                                      password = in.readLine();
      
                                                      if (password.length() > 0 && password.length() < 31) {
                                                              break;
                                                      } else if (password.length() == 0) {
                                                              System.out.println("Password can't be empty. Please enter a different password.");
                                                      } else if (password.length() > 30) {
                                                              System.out.println("Password can't be over 30 characters. Please enter a different password.");
                                                      } else {	
                                                              System.out.println("Invalid. Please try again.");
                                                      }
                                                }
   
                                              String query1 = String.format("UPDATE Users SET password = '%s' WHERE login = '%s'", password, input);
                                              esql.executeUpdate(query1);
                                              System.out.println("Password updated");
                                      } catch (Exception e) {
                                              System.out.println("Error Choice 1");
                                              System.err.println(e.getMessage());
                                         }
                  break;
               case 2:
                  try {
                     System.out.println("Avaliable roles: ");
                     System.out.println("1. Customer");
                     System.out.println("2. Manager");
                     System.out.println("3. Employee");
   
                     int innerChoice = readChoice();
                  
                     switch (innerChoice) {
                        case 1:
                           String query21 = String.format("UPDATE Users SET role = 'customer' WHERE login = '%s'", input);
                           esql.executeUpdate(query21);
                           System.out.println("Role to customer updated");
                           break;
                        case 2:
                                String query22 = String.format("UPDATE Users SET role = 'manager' WHERE login = '%s'", input);
                                                                esql.executeUpdate(query22);
                                                              System.out.println("Role to manager updated");
                           break;
                        case 3:
                                                           String query23 = String.format("UPDATE Users SET role = 'employee' WHERE login = '%s'", input);
                                                              esql.executeUpdate(query23);
                                                              System.out.println("Role to employee updated");
                           break;
                        default:
                           System.out.println("Invalid choice! Please try again.");
                     }
                  }
                  catch (Exception e) {
                     System.out.println("Error Choice 2");
                     System.err.println(e.getMessage());
                  }
                  break;
               case 3:
                            try {
                                               String fav_input;
                                              System.out.println("Enter game(s) to favorite game(s): ");
                                              fav_input = in.readLine();
   
                                              String query3 = String.format("UPDATE Users SET favGames = '%s' WHERE login = '%s'", fav_input, input);
                     esql.executeUpdate(query3);
                                              try {
                                                      String query31 = String.format("SELECT favGames FROM Users WHERE login = '%s'", input);
                                                      System.out.println("UPDATED FAVORITE GAMES");
                                                      System.out.println("----------------------");
                                                      esql.executeQueryAndPrintResult(query31);
                                              } catch (Exception e) {
                                                      System.out.println("Invalid gameID(s)");
                                                      System.err.println(e.getMessage());
                                              }
   
                                      } catch (Exception e) {
                                              System.out.println("Error Choice 3");
                                              System.err.println(e.getMessage());
                                      }
                  break;
               case 4:
                  try {
                                              String phoneNumber;
   
                                              while (true) {
                                                      System.out.print("Enter new phone number (must be 20 characters or less): ");
                                                      phoneNumber = in.readLine();
   
                                                      if (phoneNumber.length() > 0) {
                                                              break;
                                                      } else if (phoneNumber.length() == 0) {
                                                              System.out.println("Phone number can't be empty. Please enter a different phone number.");
                                                      } else if (phoneNumber.length() > 20) {
                                                              System.out.println("Phone number can't be over 20 characters. Please enter a different phone number.");
                                                      } else {
                                                              System.out.println("Invalid. Please try again.");
                                                      }
                                              }
   
                                              String query44 = String.format("UPDATE Users SET phoneNum = '%s' WHERE login = '%s'", phoneNumber, input);
                                              esql.executeUpdate(query44);
                                              System.out.println("Phone number updated");
                                      } catch (Exception e) {
                                              System.out.println("Error Choice 4");
                                              System.err.println(e.getMessage());
                                        }
                                      break;
               case 5:
                  try {
                     String overdue;
                     while (true) {
                        System.out.print("Enter new number of overdue games: ");
                        overdue = in.readLine();
   
                        if (Integer.parseInt(overdue) > 0) {
                           break;
                        }
                        else {
                           System.out.println("Invalid number of overdue games. Please try again.");
                        }
                     }
                     int int_overdue = Integer.parseInt(overdue);
                     String query55 = String.format("UPDATE Users SET numOverDueGames = %d WHERE login = '%s'", int_overdue, input);
                          esql.executeUpdate(query55);
                     System.out.println("Number of overdue games updated");	
                  } catch (Exception e) {
                     System.out.println("Error Choice 5");
                     System.err.println(e.getMessage());
                  }
                  break;
               case 6:
                  String viewUserQuery = String.format("SELECT * FROM Users WHERE login = '%s'", input);
                  esql.executeQueryAndPrintResult(viewUserQuery);
                  break;
               case 9:
                  break;
               default:
                  System.out.println("Invalid choice! Please try again.");
            }
         }
         else {
            System.out.println("User is not a manager");
         }
      } catch (Exception e) {
         System.out.println("Error");
         System.err.println(e.getMessage());
      }
   }


}//end GameRental

