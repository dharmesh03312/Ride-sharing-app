import java.sql.*;
import java.util.Scanner;

public class RagistrationAndLogInPage {
    Scanner sc = new Scanner(System.in);
    Connection con;
    String user_name = null;
    String user_number = null;
    int id = 0;

    RagistrationAndLogInPage(Connection con) {
        this.con = con;
    }

    // Login menu
    public void login_options() throws SQLException {
        boolean b = true;
        while (b) {
            System.out.println("\n===============================================================");
            System.out.println("================= Registration (or) LogIn =====================");
            System.out.println("===============================================================");
            System.out.println("Press 1 for Registration.");
            System.out.println("Press 2 for Log In.");
            System.out.println("Press 3 for back.");
            System.err.print("Enter your choice: ");

            try {
                int choice = sc.nextInt();
                switch (choice) {
                    case 1:
                    sc.nextLine();
                        Ragistration();
                        break;
                    case 2:
                        login();
                        if (user_name != null) {
                            b = false;
                        }
                        break;
                    case 3:
                        b = false;
                        break;
                    default:
                        System.out.println("\n===> Enter valid input. !!!!");
                        break;
                }
            } catch (Exception e) {
                // sc.nextLine(); // Clear the buffer
                System.out.println("Enter only digits !!!!");
            }
        }
    }

    // Log in menu
    public void login() throws SQLException {
        boolean v = true;
        while (v) {
            System.out.println("\n===========================> Log In <==================================");
            System.out.println("Press 1 for log in using Gmail.");
            System.out.println("Press 2 for log in using phone number.");
            System.out.println("Press 3 for back.");

            try {
                int choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        boolean a = gmail_login();
                        if (!a) {
                            continue;
                        }
                        v = false;
                        break;
                    case 2:
                        boolean b = phone_login();
                        if (!b) {
                            continue;
                        }
                        v = false;
                        break;
                    case 3:
                        v = false;
                        break;
                    default:
                        System.out.println("Enter valid input.!!!");
                        break;
                }
            } catch (Exception e) {
                sc.nextLine(); // Clear the buffer
                System.out.println("Enter digits only !!!"+e.getMessage());
            }
        }
    }

    // Phone number login
    public boolean phone_login() throws SQLException {
        sc.nextLine();
        boolean a = true;
        System.out.print("Enter your phone number (or type BACK to go back): ");
        String phone = sc.nextLine();
        if (phone.equalsIgnoreCase("BACK")) {
            return false;
        }
        String query = "SELECT * FROM users WHERE mobile_number = ?";
        PreparedStatement st = con.prepareStatement(query);
        st.setString(1, phone);
        ResultSet rs = st.executeQuery();
        if (!rs.next()) {
            System.out.println("User not found !!!");
            a = false;
            return a;
        }
        int min = 10000;
        int max = 99999;
        int OTP = (int) (Math.random() * (max - min + 1)) + min;
        System.out.println("Your OTP is: " + OTP);
        for (int i = 1; i <= 5; i++) {
            System.out.print("\nEnter your OTP: ");
            String otp = sc.nextLine();
            if (otp.equals(OTP + "")) {
                System.out.println("--------------: LOG IN SUCCESSFUL :------------------");
                user_name = rs.getString("name");
                user_number = rs.getString("mobile_number");
                id = rs.getInt("user_id");
                break;
            } else {
                a = false;
                System.out.println("OTP is incorrect, you have only " + (5 - i) + " attempts left.!!!!");
            }
        }
        return a;
    }

    // Gmail login
    public boolean gmail_login() throws SQLException {
        sc.nextLine();
        boolean a = true;
        System.out.print("Enter your Gmail (or type BACK to go back): ");
        String gmail = sc.nextLine();
        if (gmail.equalsIgnoreCase("BACK")) {
            return false;
        }
        if (!check_gmail(gmail)) {
            System.out.println("Entered Gmail is not correct !!!");
            a = false;
            return a;
        }
        System.out.print("Enter password (or type BACK to go back): ");
        String pass = sc.nextLine();
        if (pass.equalsIgnoreCase("BACK")) {
            return false;
        }
        String query = "SELECT * FROM users WHERE gmail = ? AND password = ?";
        PreparedStatement st = con.prepareStatement(query);
        st.setString(1, gmail);
        st.setString(2, pass);
        ResultSet rs = st.executeQuery();
        if (!rs.next()) {
            System.out.println("\n===> Gmail or password is incorrect !!");
            a = false;
            return a;
        }
        if (a == true) {
            System.out.println("--------------: LOG IN SUCCESSFUL :------------------");
            user_name = rs.getString("name");
            user_number = rs.getString("mobile_number");
            id = rs.getInt("user_id");
        }
        return a;
    }

    //---------------------------------------------------- Registration---------------------------------------------------
    public void  Ragistration() throws SQLException {
        boolean b = true;
        while (b) {
            System.out.println("\n===========================> Registration  <==================================");
            System.out.print("Enter your Name: ");
            String name = sc.nextLine();
            System.out.print("\nEnter your Gmail: ");
            String gmail = sc.nextLine();
            boolean a1 = check_gmail(gmail);
            if (!a1) {
                System.out.println("\nDo you want to go back (Y/N)?");
                String choice = sc.nextLine();
                if (choice.equalsIgnoreCase("Y")) {
                    return;
                }
                continue;
            }
            System.out.print("\nEnter Mobile Number: ");
            String number = sc.nextLine();
            boolean a2 = check_number(number);
            if (!a2) {
                System.out.println("\nDo you want to go back (Y/N)?");
                String choice = sc.nextLine();
                if (choice.equalsIgnoreCase("Y")) {
                    return;
                }
                continue;
            }
            System.out.print("\nEnter Password (must be 8 or less characters): ");
            String pass = sc.nextLine();
            insert_data(name, number, gmail, pass);
            b = false;
        }
    }

    // Insert data into database 
    public void insert_data(String name, String number, String gmail, String pass) throws SQLException {
        
        System.out.println("-------------------------------------------------");
        String q = "{call newuser(?,?,?,?)}";
        CallableStatement cs = con.prepareCall(q);
        cs.setString(1, name);
        cs.setString(2, number);
        cs.setString(3, gmail);
        cs.setString(4, pass);
        int b = 0;
        try {
            
             b = cs.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // TODO: handle exception
        }
        if (b > 0) {
            System.out.println("!!========================>  Registration Successful  <=============================!!");
        }
    }

    // Check number
    public boolean check_number(String num) {
        boolean b = true;
        try {
            Long num1 = Long.parseLong(num);
            if (num.length() != 10) {
                throw new ExceptionsClasses("\n===> Mobile Number must be 10 digits");
            }
        } catch (ExceptionsClasses a) {
            System.out.println(a.getMessage());
            b = false;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("\n===> Wrong input !!!");
            b = false;
        }
        return b;
    }

    // Check Gmail
    public boolean check_gmail(String gmail) {
        boolean b = true;
        try {
            String[] s = gmail.split("@");
            if (s.length < 2) {
                throw new ExceptionsClasses("\n====> Enter valid Gmail !!!");
            }
            if (!s[1].equals("gmail.com")) {
                throw new ExceptionsClasses("\n====> Enter valid Gmail !!!");
            }
        } catch (ExceptionsClasses e) {
            System.out.println(e.getMessage());
            b = false;
        } catch (Exception e) {
            System.out.println("\n====> Enter valid Gmail !!!");
            b = false;
        }
        return b;
    }
}
