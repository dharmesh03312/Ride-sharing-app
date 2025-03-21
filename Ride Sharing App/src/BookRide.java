import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BookRide {
    String name;
    String number;
    int id = 0;
    Scanner sc = new Scanner(System.in);
    Connection con;
    String user_source;
    String user_destination;
    Date user_date;

    BookRide(String user_name, String user_number, int id, Connection con) {
        this.name = user_name;
        this.number = user_number;
        this.id = id;
        this.con = con;
    }

    //------------------------------------- book ride menu  ----------------------------------------------------
    public void book_rideManu() throws SQLException {
        boolean b = true;
        while (b) {
            System.out.println("\nPress 1 for book a ride.\nPress 2 for check available rides.\nPress 3 for view booking history.\nPress 4 for exit.");
            try {
                int choice = sc.nextInt();
                sc.nextLine(); // consume the newline character
                switch (choice) {
                    case 1:
                        booking("passenger");
                        break;
                    case 2:
                        check_rides();
                        break;
                    case 3:
                        history();
                        break;
                    case 4:
                        b = false;
                        System.out.println("\n--> Exiting.....");
                        break;
                    default:
                        System.out.println("Enter valid input.!!!");
                        break;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                sc.nextLine(); // consume the invalid input
            }
        }
    }

    //------------------------------- booking history  -----------------------------------------
    public void history() throws SQLException {
        String query = "SELECT * FROM co_travelers JOIN trips ON co_travelers.trip_code = trips.trip_code WHERE user_id = ? and date < ?";
        try (PreparedStatement st = con.prepareStatement(query)) {
            st.setInt(1, id);
            st.setDate(2,Date.valueOf(LocalDate.now()));
            try (ResultSet rs = st.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("\n--> No trips found.");
                } else {
                    System.out.println("\nBooking History:");
                    do {
                        // Print relevant trip details
                        System.out.println("-----------------------------------------");
                        System.out.println("Trip Code: " + rs.getString("trip_code"));
                        System.out.println("Source: " + rs.getString("source"));
                        System.out.println("Destination: " + rs.getString("destination"));
                        System.out.println("Date: " + rs.getDate("date"));
                        System.out.println("Cost: " + rs.getDouble("total_cost"));
                        System.out.println("-----------------------------------------");
                    } while (rs.next());
                }
            }
        }
    }

    //------------------------------- checking available rides -----------------------------------
    public void    check_rides() throws SQLException {
        HashMap<Integer, Date> dates = select_dates();
        System.out.print("\nSelect date: ");
        int choice = 0;
        while (true) {
            try {
                choice = sc.nextInt();
                if (choice < 1 || choice > 7) {
                    throw new Exception("Enter digit according to the date.!!!!!");
                }
                break;
            } catch (Exception e) {
                System.out.println("Enter only digits.!!!!!");
                sc.nextLine(); // consume the invalid input
            }
        }
        Date date = dates.get(choice);
        String query = "SELECT * FROM ride_requests WHERE user_id = ? AND status = 'pending' AND request_type = 'passenger' AND request_date = ?";
        try (PreparedStatement st = con.prepareStatement(query)) {
            st.setInt(1, id);
            st.setDate(2, date);
            try (ResultSet rs = st.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("\nNo request found.!!!");
                    return;
                }
                user_date=rs.getDate("request_date");
                user_source = rs.getString("source");
                user_destination=rs.getString("destination");
                ArrayList<Integer> riders = find_rides(rs.getString("source"), rs.getString("destination"), date);
                ArrayList<Double> costs =   new ArrayList<>();
                if (riders.isEmpty()) {
                    System.out.println("\n==> Till now, no rider found.!!!");
                    return;
                } else {
                    System.out.println(riders);
                    System.out.println("\nAvailable riders :- ");
                    for (int i = 0; i <= riders.size()-1; i++) {
                        System.out.println("--------------------");
                        System.out.println("|===> Rider no. " + (i + 1) + " |");
                        System.out.println("--------------------------------------------------------------");

                        String riderQuery = "SELECT * FROM ride_requests JOIN users ON ride_requests.user_id = users.user_id JOIN driver_details ON ride_requests.user_id = driver_details.driver_id    WHERE ride_requests.user_id = ? and ride_requests.request_type = 'rider'";
                     PreparedStatement riderSt = con.prepareStatement(riderQuery); 
                            riderSt.setInt(1, riders.get(i));
                            ResultSet riderRs = riderSt.executeQuery();
                                if (riderRs.next()) {
                                    System.out.println("\n--> Name :- " + riderRs.getString("name"));
                                    System.out.println("--> Mobile Number :-"+riderRs.getString("mobile_number"));
                                    System.out.println("--> Rider's source :- " + riderRs.getString("source"));
                                    System.out.println("--> Rider's destination :- " + riderRs.getString("destination"));
                                    System.out.println("--> Vehicle type :- " + riderRs.getString("vehicle_type"));
                                    System.out.println("--> Total cost :- " + riderRs.getDouble("cost"));
                                    System.out.println("--> Total sharing counts :- " + riderRs.getInt("trip_counts"));
                                    System.out.println("-----------------------------------------------------------------");
                                    costs.add(riderRs.getDouble("cost"));
                                }
                            
                        
                    }
                    System.out.print("\nSelect rider (press digit according to rider number, or press any other key to go back): ");
                    int choice1 = 0;
                    try {
                        choice1 = sc.nextInt();
                        if (choice1 < 1 || choice1 > riders.size()) {
                            return;
                        } else {
                            int Rid = riders.get(choice1 - 1);
                            double cost = costs.get(choice1 - 1);
                            confirm_booking(Rid, cost);
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        sc.nextLine(); // consume the invalid input
                    }
                }
            }
        }
    }

    //------------------------------- confirming booking ----------------------------------------
    public void confirm_booking(int Rid, double cost) throws SQLException {
        String query = "{call complateRide(?,?,?,?,?,?)}";
        try (CallableStatement cs = con.prepareCall(query)) {
            cs.setInt(1, id);
            cs.setInt(2, Rid);
            cs.setString(3, user_source);
            cs.setString(4, user_destination);
            cs.setDate(5, user_date);
            cs.setDouble(6, cost);
            // cs.setString(7, "complete");
            cs.execute();

                System.out.println("\n===> Trip booked...!!!");
            
        }
    }

    //------------------------------- find rides -------------------------------------------------
    public ArrayList<Integer> find_rides(String source, String destination, Date date) throws SQLException {
        String query = "SELECT * FROM ride_requests WHERE request_type = 'rider' and status = 'pending' AND request_date = ?";
        ArrayList<Integer> riders = new ArrayList<>();
        try (PreparedStatement st = con.prepareStatement(query)) {
            st.setDate(1, date);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    String Dsource = rs.getString("source");
                    String Ddestination = rs.getString("destination");
                    SLL s1 = new map().findCitiesBetween(Dsource, Ddestination);
                    int si = 0;
                    int di = 0;
                    for (int i = 0; i < s1.getSize(); i++) {
                        if (s1.getNodeAt(i).equals(source)) {
                            si = i;
                        }         
                        if (s1.getNodeAt(i).equals(destination)) {
                            di = i;
                        }
                    }
                    if (di > si) {
                        riders.add(rs.getInt("user_id"));
                    }
                }
            }
        }
        return riders;
    }

    //------------------------------- book ride  --------------------------------------------------
    public void booking(String type) throws SQLException {
        map tree = new map();
        System.out.println("============================:- Cities in map -:=======================================");
        tree.inorder(tree.root);
        System.out.println("\n========================================================================================");
        // sc.nextLine(); // consume the newline character after the previous input
        System.out.print("\nEnter source location: ");
        user_source = sc.nextLine();
        System.out.print("Enter your destination location: ");
        user_destination = sc.nextLine();
        boolean a = check_location(user_source, user_destination);
        if (!a) {
            System.out.println("\n==> Sorry, Route is not in the database..!!!");
            return;
        }
        Double cost = 0.0;
        if (type.equals("rider")) {
            System.out.print("Enter the cost of the ride: ");
            cost = sc.nextDouble();
        }
        HashMap<Integer, Date> dates = select_dates();
        System.out.print("\nSelect date: ");
        int choice = 0;
        while (true) {
            try {
                choice = sc.nextInt();
                if (choice < 1 || choice > 7) {
                    throw new Exception("Enter digit according to the date.!!!!!");
                }
                break;
            } catch (Exception e) {
                System.out.println("Enter only digits.!!!!!");
                sc.nextLine(); // consume the invalid input
            }
        }
        user_date = dates.get(choice);
        String request_type = type.equals("passenger") ? "passenger" : "rider";
        String status = "pending";
        String query = "INSERT INTO ride_requests (user_id, source, destination, request_type, request_date, cost, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement st = con.prepareStatement(query)) {
            st.setInt(1, id);
            st.setString(2, user_source);
            st.setString(3, user_destination);
            st.setString(4, request_type);
            st.setDate(5, user_date);
            st.setDouble(6, cost);
            st.setString(7, status);
            st.executeUpdate();
            System.out.println("\n===> Ride requested successfully..!!!");
        }
    }

    //------------------------------- select dates for booking -----------------------------------
    public static HashMap<Integer, Date> select_dates() {
        HashMap<Integer, Date> dates = new HashMap<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        System.out.println("\nAvailable dates for booking rides:");
        for (int i = 1; i <= 7; i++) {
            LocalDate localDate = LocalDate.now().plusDays(i);
            String formattedDate = dtf.format(localDate);
            System.out.println(i + ". " + formattedDate);
            dates.put(i, Date.valueOf(formattedDate));
        }
        return dates;
    }

    //------------------------------- check if locations are valid ------------------------------
    public boolean check_location(String source, String destination) {
        map tree = new map();
        boolean s = tree.containsNode(source);
        boolean d = tree.containsNode(destination);
        return s && d;
    }
}
