import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String args[]) throws Exception {
        Scanner sc = new Scanner(System.in);
        boolean keepGoing = true;

        while (keepGoing) {

            System.out.println();
            System.out.println("--------------------------------------------------------------");
            System.out.println("              MENU EDIÇÃO ARQUIVO               ");
            System.out.println("        * DIGITE 1 PARA LER ARQUIVO CSV E ARQUIVOS DE ÍNDICE         ");
            System.out.println("        * DIGITE 2 PARA ABRIR MENU CRUD         ");
            System.out.println("        * DIGITE 3 PARA ABRIR MENU DE COMPRESSÃO        ");
            System.out.println("        * DIGITE 4 PARA ABRIR MENU DE CASAMENTO DE PADRÃO        "); 
            System.out.println("        * DIGITE 5 PARA APAGAR TODOS OS ARQUIVOS CRIADOS ");
            System.out.println("        * DIGITE 0 PARA SAIR           ");
            System.out.println("--------------------------------------------------------------");
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
                    Compressao.abrirCompressao();
                    break;
                case 4:
                    CasamentoPadrao.abrirCasamentoPadrao();
                    break;
                case 5:
                    File file1 = new File("animeDataBase.db");
                    file1.delete();
                    File file2 = new File("ArvoreB+.db");
                    file2.delete();
                    File file3 = new File("Diretorio.db");
                    file3.delete();
                    File file4 = new File("Bucket.db");
                    file4.delete();
                    File file5 = new File("Dicionario.db");
                    file5.delete();
                    File file6 = new File("Blocos.db");
                    file6.delete();
                    File file7 = new File("Dicionario_temp.db");
                    file7.delete();
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