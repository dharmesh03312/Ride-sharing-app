import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        // BST bst = new BST();
        Scanner sc = new Scanner(System.in);
        // bst.findCitiesBetween("ahmedabad", "rajkot");
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/coride database", "root", "");
        System.out.println("\n=================================================================");
        System.out.println("|                     WELCOME TO CORIDE                         |");
        System.out.println("=================================================================");
        RagistrationAndLogInPage r = new RagistrationAndLogInPage(con);
        r.login_options();
        if(r.user_name!=null){//----------- check for user log in or not 
            boolean b = true;
            while(b){
                System.out.println("\nPress 1 for book a ride menu.\nPress 2 for share your ride menu.\nPress 3 for exit the application.");
                int choice = 0;
                try {
                choice = sc.nextInt();    
                } catch (Exception e) {
                    System.out.println("Enter only degets.!!!!!");
                    continue;
                }
                switch (choice) {
                    case 1:
                        BookRide b1 = new BookRide(r.user_name,r.user_number, r.id, con);
                        b1.book_rideManu();
                        break;
                    case 2:
                    ShareRide s1 = new ShareRide(r.id,r.user_name,r.user_number, con);
                    s1.sharing_menu();
                        break;
                    case 3:
                    b = false;
                        break;
                
                    default:
                    System.out.println("\n-> Enter valid input..!!!!!");
                        break;
                }
            }

        }else{
            System.out.println("\nExiting..........");  
        }
        sc.close();
    }
}
