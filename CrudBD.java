import java.util.ArrayList;
import java.util.Scanner;

public class CrudBD {
    public static Scanner sc = new Scanner(System.in);
    public static void main(String args[]) throws Exception{
        int id;
        int episodes;
        MyDate year;
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
        System.out.println("        * DIGITE 4 PARA DELETE         ");
        System.out.println("        * DIGITE 0 PARA SAIR           ");
        
        for (int i = 0; i < 50; i++) {
            System.out.print("-");
        }
        System.out.println();

        while (true) {    
            System.out.print("        * ENTRADA: ");
            int crud = sc.nextInt();
            sc.nextLine();
            
            if (crud == 0) {
                break;
            }

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

                    System.out.print("* DIGITE O DIA DE LANÇAMENTO: ");
                    int dia = sc.nextInt();
                    sc.nextLine(); 

                    System.out.print("* DIGITE O MÊS DE LANÇAMENTO: ");
                    int mes = sc.nextInt();
                    sc.nextLine();

                    System.out.print("* DIGITE O ANO DE LANÇAMENTO: ");
                    int ano = sc.nextInt();
                    sc.nextLine(); 

                    year = new MyDate(dia, mes, ano);

                    System.out.print("* DIGITE OS GÊNEROS (NO MÁXIMO 2) (EXEMPLO: ROMANCE, DRAMA): ");
                    String genre = sc.nextLine();
                    genres.clear();
                    genres.add(genre.trim());

                    System.out.print("* DIGITE A ESTAÇÃO DO ANO DE LANÇAMENTO: ");
                    season = sc.nextLine();

                    System.out.print("* DIGITE O ESTÚDIO DE CRIAÇÃO: ");
                    studio = sc.nextLine();

                    arq.arq.seek(0);
                    id = arq.arq.readInt() + 1;

                    novo_anime = new Animes(id, name, type, episodes, rating, year, genres, season, studio);
                    arq.create(novo_anime);

                    arq.arq.seek(0);
                    arq.arq.writeInt(id);
                    System.out.println("* ANIME ADICIONADO COM SUCESSO! (ID: " + id + ")");
                    break;
                    
                case 2: 
                    System.out.print("* DIGITE O ID DO ANIME QUE DESEJA LER: ");
                    id = sc.nextInt();
                    sc.nextLine();
                    arq.read(id);
                    break;

                case 3: 
                    System.out.print("* DIGITE O ID DO ANIME QUE DESEJA ATUALIZAR: ");
                    id = sc.nextInt();
                    sc.nextLine();

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

                    System.out.print("* DIGITE O DIA DE LANÇAMENTO: ");
                    dia = sc.nextInt();
                    sc.nextLine();

                    System.out.print("* DIGITE O MÊS DE LANÇAMENTO: ");
                    mes = sc.nextInt();
                    sc.nextLine(); 

                    System.out.print("* DIGITE O ANO DE LANÇAMENTO: ");
                    ano = sc.nextInt();
                    sc.nextLine();

                    year = new MyDate(dia, mes, ano);

                    System.out.print("* DIGITE OS GÊNEROS (NO MÁXIMO 2) (EXEMPLO: \"ROMANCE, DRAMA\"): ");
                    genre = sc.nextLine();
                    genres.clear();
                    genres.add(genre.trim());

                    System.out.print("* DIGITE A ESTAÇÃO DO ANO DE LANÇAMENTO: ");
                    season = sc.nextLine();

                    System.out.print("* DIGITE O ESTÚDIO DE CRIAÇÃO: ");
                    studio = sc.nextLine();

                    novo_anime = new Animes(id, name, type, episodes, rating, year, genres, season, studio);
                    boolean updated = arq.update(novo_anime);

                    if (!updated) { 
                        System.out.println("* ESSE ELEMENTO NÃO EXISTE MAIS OU NUNCA EXISTIU!"); 
                    }
                    break;

                case 4: 
                    System.out.print("* DIGITE O ID DO ANIME QUE DESEJA DELETAR: ");
                    id = sc.nextInt();
                    sc.nextLine();

                    boolean deleted = arq.delete(id);
                    if (!deleted) { 
                        System.out.println("* ESSE ELEMENTO JÁ FOI EXCLUÍDO!"); 
                    } else { 
                        System.out.println("* O ELEMENTO FOI EXCLUÍDO COM SUCESSO"); 
                    }  
                    break;

                default:
                    System.out.println("* NÃO EXISTE ESTA OPÇÃO.");
                    break;
            }
        }
        
        sc.close();
        arq.close();
    }
}