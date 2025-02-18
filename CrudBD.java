import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.util.Scanner;

public class CrudBD {
    public static Scanner sc = new Scanner(System.in);
    public static void main(String args[]){
        for (int i = 0; i < 50; i++) {
            System.out.print("-");
        }

        System.out.println();
        System.out.println("              MENU CRUD           ");
        System.out.println("        * DIGITE 1 PARA CREATE         ");
        System.out.println("        * DIGITE 2 PARA READ           ");
        System.out.println("        * DIGITE 3 PARA UPDATE         ");
        System.out.println("        * DIGITE 1 PARA DELETE         ");

        for (int i = 0; i < 50; i++) {
            System.out.print("-");
        }

        System.out.println();
        System.out.print("        * ENTRADA:");
        int crud = sc.nextInt();

        switch (crud) {
            case 1: 
                System.out.println("Create");
                
                break;
            case 2: 
                System.out.println("Read");

                break;
            case 3: 
                System.out.println("Updtade");

                break;
            case 4: 
                System.out.println("Delete");

                break;

            default:
                throw new AssertionError();
        }
  
        sc.close();
    }
}
