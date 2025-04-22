import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String args[]) throws Exception {
        Scanner sc = new Scanner(System.in);
        boolean keepGoing = true;
        String name;

        while (keepGoing) {

            System.out.println("--------------------------------------------------");
            System.out.println();
            System.out.println("              MENU EDIÇÃO ARQUIVO               ");
            System.out.println("        * DIGITE 1 PARA LER ARQUIVO CSV         ");
            System.out.println("        * DIGITE 2 PARA ABRIR MENU CRUD         ");
            System.out.println("        * DIGITE 3 PARA ABRIR MENU ORDENAÇĀO (INDISPONÍVEL)");
            System.out.println("        * DIGITE 4 PARA APAGAR ARQUIVO    ");
            System.out.println("        * DIGITE 0 PARA SAIR           ");
            System.out.println("--------------------------------------------------");
            System.out.println();

            System.out.print("* ENTRADA: ");
            int menu = Integer.parseInt(sc.nextLine());

            switch (menu) {
                case 1:
                    ReadCSV.lerCSV();
                    break;
                case 2:
                    CrudBD.abrirCRUD();
                    break;
                case 3:
                    //Ordenacao.abrirOrdenacao();
                    break;
                case 4:
                    System.out.print("        * DIGITE O NOME DO ARQUIVO QUE DESEJA APAGAR: ");
                    name = sc.nextLine();
                    File file = new File(name);
                    file.delete();
                    break;
                case 0:
                    keepGoing = false;
                    break;
                default:
                    System.out.println("* NÃO EXISTE ESTA OPÇÃO.");
                    break;
            }
        }

        sc.close();
    }
}
