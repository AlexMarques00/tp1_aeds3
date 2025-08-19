import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class CrudBD {
    public static Scanner sc = new Scanner(System.in);

    public static void abrirCRUD() throws Exception {
        // Limpa arquivos temporários órfãos antes de iniciar
        Arquivo.limparArquivosTemporarios();
        
        int id;
        int episodes;
        MyDate year;
        float rating;
        String name;
        String type;
        String season;
        String studio;
        ArrayList<String> genres = new ArrayList<>();
        boolean keepGoing = true;
        Animes novo_anime;

        // Escolhe o arquivo baseado no status da criptografia
        String nomeArquivo;
        
        if (Animes.isCriptografiaHabilitada()) {
            nomeArquivo = escolherArquivoCriptografado();
        } else {
            nomeArquivo = "animeDataBase.db";
        }
        
        Arquivo arq = new Arquivo(nomeArquivo);

        HashExtensivo hash = new HashExtensivo(ReadCSV.getElementosCesto());

        ArvoreBMais arvore = new ArvoreBMais("ArvoreB+.db", ReadCSV.getOrdem());

        ListaInvertida lista = new ListaInvertida(ReadCSV.getElementosBloco());

        while (keepGoing) {

            System.out.println();
            System.out.println("--------------------------------------------------");
            System.out.println("              MENU CRUD                ");
            System.out.println("        * DIGITE 1 PARA CREATE         ");
            System.out.println("        * DIGITE 2 PARA READ           ");
            System.out.println("        * DIGITE 3 PARA UPDATE         ");
            System.out.println("        * DIGITE 4 PARA DELETE         ");
            System.out.println("        * DIGITE 5 PARA VER ARQUIVOS DE INDICE         ");
            System.out.println("        * DIGITE 0 PARA SAIR           ");
            System.out.println("--------------------------------------------------");
            
            // Mostra status da criptografia e arquivo em uso
            if (Animes.isCriptografiaHabilitada()) {
                String tipo = Animes.getTipoCriptografia();
                System.out.println("  * CRIPTOGRAFIA: HABILITADA (" + tipo + ") | ARQUIVO: " + nomeArquivo);
            } else {
                System.out.println("  * CRIPTOGRAFIA: DESABILITADA | ARQUIVO: " + nomeArquivo);
            }
            System.out.println("  * (Configure criptografia no Menu Criptografia)");
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

                    System.out.println();
                    System.out.println("        * DIGITE 1 SE DESEJA CRIAR O ANIME NA ARVORE B+");
                    System.out.println("        * DIGITE 2 SE DESEJA CRIAR O ANIME NO HASH EXTENSIVO");
                    System.out.println("        * DIGITE 3 SE DESEJA CRIAR O ANIME NA LISTA INVERTIDA");
                    System.out.println("        * DIGITE 4 SE DESEJA CRIAR O ANIME NOS TRÊS");
                    System.out.print("* ENTRADA: ");
                    int opcaoFiltro = Integer.parseInt(sc.nextLine());

                    switch (opcaoFiltro) {
                        case 1:
                            arq.create(novo_anime, arvore, hash, lista, opcaoFiltro);
                            break;
                        case 2:
                            arq.create(novo_anime, arvore, hash, lista, opcaoFiltro);
                            break;
                        case 3:
                            arq.create(novo_anime, arvore, hash, lista, opcaoFiltro);
                            break;
                        case 4:
                            arq.create(novo_anime, arvore, hash, lista, opcaoFiltro);
                            break;
                        default:
                            System.out.println("* NÃO EXISTE ESTA OPÇÃO.");
                            break;
                    }

                    arq.arq.seek(0);
                    arq.arq.writeInt(id);
                    System.out.println("* ANIME ADICIONADO COM SUCESSO! (ID: " + id + ")");
                    break;
                case 2:
                    System.out.println("        * DIGITE 1 SE DESEJA FAZER UMA BUSCA POR ID (ARVORE B+)");
                    System.out.println("        * DIGITE 2 SE DESEJA FAZER UMA BUSCA POR ID (HASH EXTENSÍVEL)");
                    System.out.println("        * DIGITE 3 SE DESEJA FAZER UMA BUSCA POR ESTAÇÃO DO ANO (LISTA INVERTIDA)");
                    System.out.println("        * DIGITE 4 SE DESEJA FAZER UMA BUSCA POR EPISÓDIOS (LISTA INVERTIDA)");
                    System.out.println("        * DIGITE 5 SE DESEJA FAZER UMA BUSCA POR STUDIO (LISTA INVERTIDA)");
                    System.out.println("        * DIGITE 6 SE DESEJA FAZER UMA BUSCA POR NOTA (LISTA INVERTIDA)");
                    System.out.println("        * DIGITE 7 SE DESEJA BUSCAR POR MAIS DE UM ATRIBUTO (LISTA INVERTIDA)");
                    System.out.print("* ENTRADA: ");
                    opcaoFiltro = Integer.parseInt(sc.nextLine());

                    switch (opcaoFiltro) {
                        case 1:
                            System.out.print("* DIGITE O ID DO ANIME QUE DESEJA BUSCAR: ");
                            id = sc.nextInt();
                            sc.nextLine();
                            arq.read(id, opcaoFiltro, arvore, null);
                            break;
                        case 2:
                            System.out.print("* DIGITE O ID DO ANIME QUE DESEJA BUSCAR: ");
                            id = sc.nextInt();
                            sc.nextLine();
                            arq.read(id, opcaoFiltro, null, hash);
                            break;
                        case 3:
                            System.out.print("* DIGITE A ESTAÇÃO DO ANO DOS ANIMES QUE DESEJA BUSCAR: ");
                            season = sc.nextLine();
                            arq.readLista(-1, season, opcaoFiltro, -1, null, lista);
                            break;
                        case 4:
                            System.out.print("* DIGITE O NÚMERO DE EPISÓDIOS DOS ANIMES QUE DESEJA BUSCAR: ");
                            episodes = sc.nextInt();
                            sc.nextLine();
                            arq.readLista(episodes, null, opcaoFiltro, -1, null, lista);
                            break;
                        case 5:
                            System.out.print("* DIGITE O ESTÚDIO DOS ANIMES QUE DESEJA BUSCAR: ");
                            studio = sc.nextLine();
                            arq.readLista(-1, null, opcaoFiltro, -1, studio, lista);
                            break;
                        case 6:
                            System.out.print("* DIGITE A NOTA DOS ANIMES QUE DESEJA BUSCAR (MÁX: 5): ");
                            rating = sc.nextFloat();
                            sc.nextLine();
                            arq.readLista(-1, null, opcaoFiltro, rating, null, lista);
                            break;
                        case 7:
                            System.out.println("        * ===== ATRIBUTOS DISPONÍVEIS: ===== *");
                            System.out.println("         season -- studio -- episodios -- nota");
                            System.out.println();
                            System.out.print("        * DIGITE O PRIMEIRO ATRIBUTO QUE DESEJA BUSCAR (LISTA INVERTIDA): ");
                            String atributo1 = sc.nextLine();
                            System.out.print("        * DIGITE O SEGUNDO ATRIBUTO QUE DESEJA BUSCAR (LISTA INVERTIDA) : ");
                            String atributo2 = sc.nextLine();
                            arq.readListaPlus(atributo1, atributo2, lista);
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

                    System.out.print("* DIGITE O NOME NOVO: ");
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

                    System.out.println();
                    System.out.println("        * DIGITE 1 SE DESEJA FAZER O UPDATE NA ARVORE B+");
                    System.out.println("        * DIGITE 2 SE DESEJA FAZER O UPTADE NO HASH EXTENSIVO");
                    System.out.println("        * DIGITE 3 SE DESEJA FAZER O UPDATE NA LISTA INVERTIDA");
                    System.out.println("        * DIGITE 4 SE DESEJA FAZER O UPDATE NOS TRÊS");
                    System.out.print("* ENTRADA: ");
                    opcaoFiltro = Integer.parseInt(sc.nextLine());
                    boolean updated = false;

                    switch (opcaoFiltro) {
                        case 1:
                            updated = arq.update(novo_anime, arvore, hash, lista, opcaoFiltro);
                            break;
                        case 2:
                            updated = arq.update(novo_anime, arvore, hash, lista, opcaoFiltro);
                            break;
                        case 3:
                            updated = arq.update(novo_anime, arvore, hash, lista, opcaoFiltro);
                            break;
                        case 4:
                            updated = arq.update(novo_anime, arvore, hash, lista, opcaoFiltro);
                            break;
                        default:
                            System.out.println("* NÃO EXISTE ESTA OPÇÃO.");
                            break;
                    }

                    if (!updated) {
                        System.out.println("* ESSE ELEMENTO NÃO EXISTE MAIS OU NUNCA EXISTIU!");
                    }
                    break;
            
                case 4:
                    System.out.println();
                    System.out.println("        * DIGITE 1 SE DESEJA DELETAR DA ARVORE B+");
                    System.out.println("        * DIGITE 2 SE DESEJA DELETAR DO HASH EXTENSIVO");
                    System.out.println("        * DIGITE 3 SE DESEJA DELETAR DA LISTA INVERTIDA");
                    System.out.println("        * DIGITE 4 SE DESEJA DELETAR DOS TRÊS");
                    System.out.println("        * DIGITE 5 SE DESEJA DELETAR ANIMES POR ATRIBUTO");
                    System.out.print("* ENTRADA: ");
                    opcaoFiltro = Integer.parseInt(sc.nextLine());
                    boolean deleted = false;

                    switch (opcaoFiltro) {
                        case 1:
                            System.out.print("* DIGITE O ID DO ANIME QUE DESEJA DELETAR: ");
                            id = sc.nextInt();
                            sc.nextLine();

                            deleted = arq.delete(id, arvore, hash, lista, opcaoFiltro);
                            break;
                        case 2:
                            System.out.print("* DIGITE O ID DO ANIME QUE DESEJA DELETAR: ");
                            id = sc.nextInt();
                            sc.nextLine();
                            
                            deleted = arq.delete(id, arvore, hash, lista, opcaoFiltro);
                            break;
                        case 3:
                            System.out.print("* DIGITE O ID DO ANIME QUE DESEJA DELETAR: ");
                            id = sc.nextInt();
                            sc.nextLine();

                            deleted = arq.delete(id , arvore, hash, lista, opcaoFiltro);
                            break;
                        case 4:
                            System.out.print("* DIGITE O ID DO ANIME QUE DESEJA DELETAR: ");
                            id = sc.nextInt();
                            sc.nextLine();

                            deleted = arq.delete(id , arvore, hash, lista, opcaoFiltro);
                            break;
                        case 5:
                            System.out.println("        * ===== ATRIBUTOS DISPONÍVEIS: ===== *");
                            System.out.println("         season -- studio -- episodios -- nota");
                            System.out.println();
                            System.out.print("        * DIGITE O ATRIBUTO DOS ANIMES QUE DESEJA DELETAR (LISTA INVERTIDA): ");
                            String atributo1 = sc.nextLine();

                            deleted = arq.deleteLista(atributo1, hash, lista);
                            break;
                        default:
                            System.out.println("* NÃO EXISTE ESTA OPÇÃO.");
                            break;
                    }

                    if (!deleted) {
                        System.out.println("* ESSE ELEMENTO JÁ FOI EXCLUÍDO!");
                    } else {
                        System.out.println("* O ELEMENTO FOI EXCLUÍDO COM SUCESSO");
                    }
                    break;
                case 5:
                    System.out.println("        * DIGITE 1 PARA VER ARVORE B+         ");
                    System.out.println("        * DIGITE 2 PARA VER HASH EXTENSIVEL         ");
                    System.out.println("        * DIGITE 3 PARA VER LISTA INVERTIDA         ");
                    System.out.print("* ENTRADA: ");
                    opcaoFiltro = Integer.parseInt(sc.nextLine());

                    switch (opcaoFiltro) {
                        case 1:
                            arvore.print();;
                            break;
                        case 2:
                            hash.print();
                            break;
                        case 3:
                            lista.print();
                            break;
                        default:
                            break;
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

        // Fecha o arquivo (se for criptografado, criptografa de volta)
        arq.close();
        arvore.close();
        lista.close();
        hash.close();
    }

    //Método para escolher qual arquivo criptografado usar quando a criptografia estiver habilitada 
    private static String escolherArquivoCriptografado() {
        System.out.println();
        String tipoCriptografia = Animes.getTipoCriptografia();
        System.out.println("* CRIPTOGRAFIA " + tipoCriptografia + " HABILITADA - USANDO ARQUIVO ORIGINAL CRIPTOGRAFADO");
        System.out.println("* NOTA: CRUD não suporta arquivos comprimidos criptografados");
        
        // Escolhe o arquivo baseado no tipo de criptografia
        String arquivoCriptografado;
        if ("VIGENERE".equals(tipoCriptografia)) {
            arquivoCriptografado = "animeDataBase.cripto.db";
        } else if ("RSA".equals(tipoCriptografia)) {
            arquivoCriptografado = "animeDataBase.cripto.rsa.db";
        } else {
            // Fallback para Vigenère se tipo não reconhecido
            arquivoCriptografado = "animeDataBase.cripto.db";
        }
        
        File arquivo = new File(arquivoCriptografado);
        if (!arquivo.exists()) {
            System.out.println("* ERRO: ARQUIVO CRIPTOGRAFADO NÃO ENCONTRADO!");
            System.out.println("* Crie o arquivo criptografado primeiro no Menu Criptografia");
            System.out.println("* Usando arquivo não criptografado como fallback...");
            return "animeDataBase.db"; // Fallback para arquivo não criptografado
        }
        
        System.out.println("* ARQUIVO SELECIONADO: " + arquivoCriptografado);
        return arquivoCriptografado;
    }

}