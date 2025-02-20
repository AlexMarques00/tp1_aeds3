import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;

public class CrudBD {
    public static Scanner sc = new Scanner(System.in);
    public static void main(String args[]) throws Exception{
        int id;
        int episodes;
        int year;
        float rating;
        String name;
        String type;
        String season;
        String studio;
        ArrayList<String> genres = new ArrayList<>();
        
        Animes novo_anime;
        
        Arquivo arq = new Arquivo("animeDataBase.db");
        
        for (int i = 0; i < 50; i++) {
            System.out.print("-");
        }

        System.out.println();
        System.out.println("              MENU CRUD                ");
        System.out.println("        * DIGITE 1 PARA CREATE         ");
        System.out.println("        * DIGITE 2 PARA READ           ");
        System.out.println("        * DIGITE 3 PARA UPDATE         ");
        System.out.println("        * DIGITE 1 PARA DELETE         ");

        for (int i = 0; i < 50; i++) {
            System.out.print("-");
        }

        System.out.println();
        
        System.out.print("        * ENTRADA: ");
        int crud = sc.nextInt();
        sc.nextLine();

        switch (crud) {
            case 1: 
                System.out.print("* DIGITE O NOME: ");
                name = sc.nextLine();

                System.out.print("* DIGITE O TIPO DE MÍDIA (TAMANHO FIXO DE 5 BYTES): ");
                type = sc.nextLine();

                System.out.print("* DIGITE O NUMERO DE EPISÓDIOS: ");
                episodes = sc.nextInt();
                sc.nextLine();

                System.out.print("* DIGITE A NOTA DESSE ANIME: ");
                rating = sc.nextFloat();
                sc.nextLine();

                System.out.print("* DIGITE O ANO DE LANÇAMENTO: ");
                year = sc.nextInt();
                sc.nextLine();

                System.out.println("* DIGITE OS GÊNEROS ENTRE \"\" (NO MÁXIMO 2): ");
                System.out.println("(EXEMPLO: \"ROMANCE, DRAMA\")");

                String genre = sc.nextLine();
                genres.add(genre.trim());

                System.out.print("* DIGITE A ESTAÇÃO DO ANO DE LANÇAMENTO: ");
                season = sc.nextLine();

                System.out.print("DIGITE O ESTÚDIO DE CRIAC: ");
                studio = sc.nextLine();
                arq.arq.seek(0);
                id = arq.arq.readInt() + 1;
                arq.arq.seek(0);
                arq.arq.writeInt(id);

                novo_anime = new Animes(id, name, type, episodes, rating, year, genres, season, studio);
                arq.create(novo_anime);
                break;
                
            case 2: 
                System.out.print("* DIGITE O ID DO ANIME QUE DESEJA LER: ");
                id = sc.nextInt();
                arq.read(id);
                break;

            case 3: 
                System.out.print("* DIGITE O ID DO ANIME QUE DESEJA ATUALIZAR: ");
                id = sc.nextInt();
                arq.update(id);
                break;

            case 4: 
                System.out.print("* DIGITE O ID DO ANIME QUE DESEJA DELETAR: ");
                id = sc.nextInt();
                arq.delete(id);
                break;

            default:
                System.out.println("NÃO EXISTE ESTA OPÇÃO.");
                break;
        }
  
        sc.close();
        arq.close();
    }
    
}
