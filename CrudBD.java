import java.util.ArrayList;
import java.util.Scanner;

public class CrudBD {
    public static Scanner sc = new Scanner(System.in);

    public static void abrirCRUD() throws Exception {
        int id;
        int episodes;
        MyDate year;
        float rating;
        String name;
        String type;
        String season;
        String studio;
        ArrayList<String> genres = new ArrayList<>();
        ArrayList<Animes> conjunto = new ArrayList<>();
        boolean keepGoing = true;
        Animes novo_anime;

        Arquivo arq = new Arquivo("animeDataBase.db");

        //HashExtensivo arq1 = new HashExtensivo("HashExtensivo.db");

        ArvoreBMais arq2 = new ArvoreBMais("ArvoreB.db", 10);

        ListaInvertida arq3 = new ListaInvertida("ListaInvertida.db");

        while (keepGoing) {

            System.out.println("--------------------------------------------------");
            System.out.println();
            System.out.println("              MENU CRUD                ");
            System.out.println("        * DIGITE 1 PARA CREATE         ");
            System.out.println("        * DIGITE 2 PARA READ           ");
            System.out.println("        * DIGITE 3 PARA UPDATE         ");
            System.out.println("        * DIGITE 4 PARA DELETE         ");
            System.out.println("        * DIGITE 0 PARA SAIR           ");
            System.out.println("--------------------------------------------------");
            System.out.println();

            System.out.print("* ENTRADA: ");
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

                    System.out.print("* DIGITE A DATA (DD MM AAAA): ");
                    String[] dataFiltro = sc.nextLine().split(" ");
                    year = new MyDate(Integer.parseInt(dataFiltro[0]), Integer.parseInt(dataFiltro[1]),
                            Integer.parseInt(dataFiltro[2]));

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

                    arq.create(novo_anime, arq2);
 
                    arq.arq.seek(0);
                    arq.arq.writeInt(id);
                    System.out.println("* ANIME ADICIONADO COM SUCESSO! (ID: " + id + ")");
                    break;
                case 2:
                    System.out.println("        * DIGITE 1 SE DESEJA FAZER UMA BUSCA POR ID");
                    System.out.println("        * DIGITE 2 SE DESEJA FAZER UMA BUSCA POR NOME");
                    System.out.println("        * DIGITE 3 SE DESEJA FILTRAR POR TIPO");
                    System.out.println("        * DIGITE 4 SE DESEJA FILTRAR POR ESTAÇÃO DO ANO");
                    System.out.println("        * DIGITE 5 SE DESEJA FILTRAR POR DATA DE LANÇAMENTO");
                    System.out.println("        * DIGITE 6 SE DESEJA FILTRAR POR NÚMERO DE EPISÓDIOS");
                    System.out.println("        * DIGITE 7 SE DESEJA FILTRAR POR NOTA");
                    System.out.println("        * DIGITE 8 SE DESEJA FILTRAR POR ESTÚDIO");
                    System.out.println("        * DIGITE 9 SE DESEJA FILTRAR POR GÊNERO");
                    System.out.print("* ENTRADA: ");
                    int opcaoFiltro = Integer.parseInt(sc.nextLine());

                    switch (opcaoFiltro) {
                        case 1:
                            System.out.print("* DIGITE O ID DO ANIME QUE DESEJA BUSCAR: ");
                            id = sc.nextInt();
                            sc.nextLine();
                            arq.read(id, null, opcaoFiltro, arq2, arq3);
                            break;
                        case 2:
                            System.out.print("* DIGITE O NOME DO ANIME QUE DESEJA BUSCAR: ");
                            name = sc.nextLine();
                            arq.read(0, name, opcaoFiltro, arq2, arq3);
                            break;
                        case 3:
                            System.out.print("* DIGITE O TIPO DE MÍDIA (TAMANHO FIXO DE 5 BYTES): ");
                            type = sc.nextLine();
                            conjunto = arq.filtrar(null, type, opcaoFiltro, 0, 0);

                            for (int i = 0; i < conjunto.size(); i++) {
                                name = conjunto.get(i).getName();
                                System.out.println(name);
                            }
                            break;
                        case 4:
                            System.out.print("* DIGITE A ESTAÇÃO DO ANO DE LANÇAMENTO: ");
                            season = sc.nextLine();
                            conjunto = arq.filtrar(null, season, opcaoFiltro, 0, 0);

                            for (int i = 0; i < conjunto.size(); i++) {
                                name = conjunto.get(i).getName();
                                System.out.println(name);
                            }
                            break;
                        case 5:
                            System.out.print("* DIGITE A DATA (DD MM AAAA): ");
                            dataFiltro = sc.nextLine().split(" ");
                            year = new MyDate(Integer.parseInt(dataFiltro[0]), Integer.parseInt(dataFiltro[1]),
                                    Integer.parseInt(dataFiltro[2]));

                            conjunto = arq.filtrar(year, null, opcaoFiltro, 0, 0);

                            for (int i = 0; i < conjunto.size(); i++) {
                                name = conjunto.get(i).getName();
                                System.out.println(name);
                            }
                            break;
                        case 6:
                            System.out.print("* DIGITE O NÚMERO DE EPISÓDIOS: ");
                            episodes = sc.nextInt();
                            conjunto = arq.filtrar(null, null, opcaoFiltro, episodes, 0);

                            for (int i = 0; i < conjunto.size(); i++) {
                                name = conjunto.get(i).getName();
                                System.out.println(name);
                            }
                            break;
                        case 7:
                            System.out.print("* DIGITE A NOTA: ");
                            rating = sc.nextFloat();
                            conjunto = arq.filtrar(null, null, opcaoFiltro, 0, rating);

                            for (int i = 0; i < conjunto.size(); i++) {
                                name = conjunto.get(i).getName();
                                System.out.println(name);
                            }
                            break;
                        case 8:
                            System.out.print("* DIGITE O ESTÚDIO DE CRIAÇĀO: ");
                            studio = sc.nextLine();
                            conjunto = arq.filtrar(null, studio, opcaoFiltro, 0, 0);

                            for (int i = 0; i < conjunto.size(); i++) {
                                name = conjunto.get(i).getName();
                                System.out.println(name);
                            }
                            break;
                        case 9:
                            System.out.print("* DIGITE O GÊNERO: ");
                            genre = sc.nextLine();
                            conjunto = arq.filtrar(null, genre, opcaoFiltro, 0, 0);

                            for (int i = 0; i < conjunto.size(); i++) {
                                name = conjunto.get(i).getName();
                                System.out.println(name);
                            }
                            break;
                        default:
                            System.out.println("* NÃO EXISTE ESTA OPÇÃO.");
                            break;
                    }
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

                    System.out.print("* DIGITE A DATA (DD MM AAAA): ");
                    dataFiltro = sc.nextLine().split(" ");
                    year = new MyDate(Integer.parseInt(dataFiltro[0]), Integer.parseInt(dataFiltro[1]),
                            Integer.parseInt(dataFiltro[2]));

                    System.out.print("* DIGITE OS GÊNEROS (NO MÁXIMO 2) (EXEMPLO: \"ROMANCE, DRAMA\"): ");
                    genre = sc.nextLine();
                    genres.clear();
                    genres.add(genre.trim());

                    System.out.print("* DIGITE A ESTAÇÃO DO ANO DE LANÇAMENTO: ");
                    season = sc.nextLine();

                    System.out.print("* DIGITE O ESTÚDIO DE CRIAÇÃO: ");
                    studio = sc.nextLine();

                    novo_anime = new Animes(id, name, type, episodes, rating, year, genres, season, studio);
                    boolean updated = arq.update(novo_anime, arq2);

                    if (!updated) {
                        System.out.println("* ESSE ELEMENTO NÃO EXISTE MAIS OU NUNCA EXISTIU!");
                    }
                    break;

                case 4:
                    System.out.print("* DIGITE O ID DO ANIME QUE DESEJA DELETAR: ");
                    id = sc.nextInt();
                    sc.nextLine();

                    boolean deleted = arq.delete(id, arq2);
                    if (!deleted) {
                        System.out.println("* ESSE ELEMENTO JÁ FOI EXCLUÍDO!");
                    } else {
                        System.out.println("* O ELEMENTO FOI EXCLUÍDO COM SUCESSO");
                    }
                    break;

                case 0:
                    keepGoing = false;
                    break;

                default:
                    System.out.println("* NÃO EXISTE ESTA OPÇÃO.");
                    break;
            }
        }
        arq.close();
        arq2.close();
    }
}