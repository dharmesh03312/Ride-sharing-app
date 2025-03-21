import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Scanner;

public class ShareRide {
    int id;
    String name;
    String number;
    Connection con;
    Scanner sc = new Scanner(System.in);

    ShareRide(int id, String name, String number, Connection con) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.con = con;
    }

    //--------------------------- ride sharing menu ------------------------------------------
    public void sharing_menu() throws SQLException {
        boolean b = true;
        while (b) {
            System.out.println("\nPress 1 for share a ride(You can share ride in next 6 days).\nPress 2 for find match.\nPress 3 for view sharing history.\nPress 4 for exit.");
            int choice = 0;
            try {
                choice = sc.nextInt();
                sc.nextLine(); // Consume newline character
            } catch (Exception e) {
                System.out.println("Enter only digits.!!!!!");
                // sc.nextLine(); // Clear the invalid input
                continue;
            }
            switch (choice) {
                case 1:
                    driver_data();
                    new BookRide(name, number, id, con).booking("rider");
                    break;
                case 2:
                    checking_matches();
                    break;
                case 3:
                    sharing_history();
                    break;
                case 4:
                    b = false;
                    System.out.println("\n--> Exiting.....");
                    break;
                default:
                    System.out.println("Enter valid input.!!!");
                    break;
            }
        }
    }

    //----------------------------------- sharing history ---------------------------------------------------------
    void sharing_history() throws SQLException {
        String query = "SELECT users.user_id, users.name, trips.source, trips.destination, trips.total_cost " +
                       "FROM co_travelers " +
                       "JOIN trips ON co_travelers.driver_id = trips.driver_id " +
                       "JOIN users ON users.user_id = co_travelers.user_id " +
                       "WHERE co_travelers.driver_id = ? AND trips.date < ?";
               
        PreparedStatement st = con.prepareStatement(query);
        st.setInt(1, id);
        st.setDate(2, Date.valueOf(LocalDate.now())); // Set the current date
               
        ResultSet rs = st.executeQuery();
        boolean found = false;

        while (rs.next()) {
            found = true;
            System.out.println("-> user id = " + rs.getInt("user_id"));
            System.out.println("-> name : " + rs.getString("name"));
            System.out.println("-> pickup location : " + rs.getString("source"));
            System.out.println("-> drop location : " + rs.getString("destination"));
            System.out.println("-> total cost : " + rs.getInt("cost"));
            System.out.println("-----------------------------------");
        }

        if (!found) {
            System.out.println("No sharing history found.");
        }
    }

    //----------------------------------- checking for matches ------------------------------------------------------
    void checking_matches() throws SQLException {
        HashMap<Integer, Date> date = BookRide.select_dates();
        System.out.print("\nSelect date :- ");
        int choice = sc.nextInt();
        sc.nextLine(); // Consume newline character

        if (choice < 1 || choice > 7) {
            System.out.println("\n==> Enter valid input..!!!");
            return;
        }

        String query = "SELECT users.user_id, users.name, trips.source, trips.destination " +
                       "FROM co_travelers " +
                       "JOIN trips ON co_travelers.driver_id = trips.driver_id " +
                       "JOIN users ON users.user_id = co_travelers.user_id " +
                       "WHERE co_travelers.driver_id = ? AND trips.date = ?";

        PreparedStatement st = con.prepareStatement(query);
        st.setInt(1, id);
        st.setDate(2, date.get(choice));

        ResultSet rs = st.executeQuery();
        boolean found = false;

        while (rs.next()) {
            found = true;
            System.out.println("-> user id = " + rs.getInt("user_id"));
            System.out.println("-> name : " + rs.getString("name"));
            System.out.println("-> pickup location : " + rs.getString("source"));
            System.out.println("-> drop location : " + rs.getString("destination"));
            System.out.println("-----------------------------------");
        }

        if (!found) {
            System.out.println("No matches found for the selected date.");
        }
    }

    //----------------------------------- store new driver's data in database --------------------------------------------
    void driver_data() throws SQLException {
        String q = "SELECT * FROM driver_details WHERE driver_id = ?";
        PreparedStatement st = con.prepareStatement(q);
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            return;
        }

        System.out.print("\nEnter your vehicle no. : ");
        // sc.nextLine();
        String Vno = sc.nextLine();
        System.out.print("\nEnter vehicle type (two/four) wheeler: ");
        String Vtype = sc.nextLine();

        if (!Vtype.equals("two") && !Vtype.equals("four")) {
            System.out.println("\nEnter data as 'two' or 'four' only. Don't enter any other values.");
            driver_data();
            return;
        }

        System.out.print("\nEnter Aadhar no. : ");
        String Ano = sc.nextLine();
        System.out.print("\nEnter license no. : ");
        String Lno = sc.nextLine();

        String query = "INSERT INTO driver_details VALUES(?,?,?,?,?,?)";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, id);
        ps.setString(2, Vno);
        ps.setString(3, Vtype);
        ps.setString(4, Ano);
        ps.setString(5, Lno);
        ps.setInt(6, 0);
        int n = ps.executeUpdate();

        if (n > 0) {
            System.out.println("\n==> Data inserted..!!!!");
        }
    }
}
