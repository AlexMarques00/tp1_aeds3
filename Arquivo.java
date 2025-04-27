import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Arquivo {
    public RandomAccessFile arq;
    public static Scanner sc = new Scanner(System.in);

    public Arquivo(String path) throws Exception {
        arq = new RandomAccessFile(path, "rw");
    }

    public void create(Animes anime, ArvoreBMais arvore, HashExtensivo hash, ListaInvertida lista, int tipo)
            throws Exception {
        // Obtém a posição atual (offset) antes de escrever
        arq.seek(arq.length());
        long offset = arq.getFilePointer();

        // LAPIDE + TAM + OBEJTO
        byte[] objeto = anime.toByteArray();

        arq.writeChar(' ');
        arq.writeShort(objeto.length);
        arq.write(objeto);

        switch (tipo) {
            case 1:
                arvore.create(anime, offset);
                break;
            case 2:
                hash.create(anime.getId(), offset);
                break;
            case 3:
                lista.create(anime, offset);
                break;
            case 4:
                arvore.create(anime, offset);
                hash.create(anime.getId(), offset);
                lista.create(anime, offset);
                break;
            default:
                break;
        }
    }

    public void read(int id, int tipo, ArvoreBMais arvore, HashExtensivo hash) throws IOException, Exception {
        Animes anime = new Animes();
        char lapide;
        boolean resp = false;
        boolean resp2 = false;
        long offset = -1;

        switch (tipo) {
            case 1:
                offset = arvore.read(id);
                break;
            case 2:
                offset = hash.read(id);
                break;
            default:
                break;
        }

        if (offset == -1) {
            System.out.println("ID " + id + " EXCLUÍDO OU INEXISTENTE!");
        } else {

            // Vai para posição
            arq.seek(offset);

            lapide = arq.readChar();
            int tamanhoRegistro = arq.readShort();

            // Lê os bytes do registro
            byte[] ba = new byte[tamanhoRegistro];
            arq.read(ba);
            anime.fromByteArray(ba);

            if (lapide != '*') {
                anime.write();
                System.out.println(" --- (ENDEREÇO: @" + offset + ") --- ");
                resp2 = true;
            } else if (lapide == '*') {
                resp = true;
            }

            if (!resp2 && resp) {
                System.out.println("* OBJETO EXCLUÍDO OU NÃO EXISTE!");
            } else if (id < 18495 && resp) {
                System.out.println("* OBJETO DESLOCADO PARA O FIM DO ARQUIVO!");
            }
        }
    }

    public void readLista(int eps, String season, int tipo, float nota, String studio, ListaInvertida lista) throws IOException, Exception {
        Animes anime = new Animes();
        char lapide;
        boolean resp = false;
        boolean resp2 = false;

        List<Long> offsets = new ArrayList<>();

        switch (tipo) {
            case 3:
                offsets = lista.read(season, -1, -1, null, tipo);
                break;
            case 4:
                offsets = lista.read(null, eps, -1, null, tipo);
                break;
            case 5:
                offsets = lista.read(null, -1, -1, studio, tipo);
                break;
            case 6:
                offsets = lista.read(null, -1, nota, null, tipo);
                break;
            default:
                break;
        }

        for (int i = 0; i < offsets.size(); i++) {
            long offsetAtual = offsets.get(i);

            if (offsetAtual == -1) {
                System.out.println("ANIME EXCLUÍDO OU INEXISTENTE!");
            } else {

                // Vai para posição
                arq.seek(offsetAtual);

                lapide = arq.readChar();
                int tamanhoRegistro = arq.readShort();

                // Lê os bytes do registro
                byte[] ba = new byte[tamanhoRegistro];
                arq.read(ba);
                anime.fromByteArray(ba);

                if (lapide != '*') {
                    anime.write();
                    System.out.println(" --- (ENDEREÇO: @" + offsetAtual + ") --- ");
                    resp2 = true;
                } else if (lapide == '*') {
                    resp = true;
                }

                if (!resp2 && resp) {
                    System.out.println("* OBJETO EXCLUÍDO OU NÃO EXISTE!");
                } else if (anime.getId() < 18495 && resp) {
                    System.out.println("* OBJETO DESLOCADO PARA O FIM DO ARQUIVO!");
                }
            }
        }
    }

    public void readListaPlus(String atributo1, String atributo2, ListaInvertida lista) throws IOException, Exception {
        Animes anime = new Animes();
        char lapide;
        boolean resp = false;
        boolean resp2 = false;

        List<Long> offsets = new ArrayList<>();
        String season;
        int eps;
        String studio;
        float nota;

        if (atributo1.equals("season") && atributo2.equals("episodios") || atributo2.equals("season") && atributo1.equals("episodios")) {
            System.out.println();
            System.out.print("* DIGITE A ESTAÇÃO DO ANO DOS ANIMES QUE DESEJA BUSCAR: ");
            season = sc.nextLine();
            System.out.print("* DIGITE O NÚMERO DE EPISÓDIOS DOS ANIMES QUE DESEJA BUSCAR: ");
            eps = sc.nextInt();
            sc.nextLine();

            offsets = lista.read(season, eps, -1, null, 7);
        } else if (atributo1.equals("nota") && atributo2.equals("studio") || atributo2.equals("nota") && atributo1.equals("studio")) {
            System.out.println();
            System.out.print("* DIGITE A NOTA DOS ANIMES QUE DESEJA BUSCAR: ");
            nota = sc.nextFloat();
            sc.nextLine();
            System.out.print("* DIGITE O ESTÚDIO DOS ANIMES QUE DESEJA BUSCAR: ");
            studio = sc.nextLine();

            offsets = lista.read(null, -1, nota, studio, 8);
        } else if (atributo1.equals("season") && atributo2.equals("studio") || atributo2.equals("season") && atributo1.equals("studio")) {
            System.out.println();
            System.out.print("* DIGITE A ESTAÇÃO DO ANO DOS ANIMES QUE DESEJA BUSCAR: ");
            season = sc.nextLine();
            System.out.print("* DIGITE O ESTÚDIO DOS ANIMES QUE DESEJA BUSCAR: ");
            studio = sc.nextLine();

            offsets = lista.read(season, -1, -1, studio, 9);
        } else if (atributo1.equals("season") && atributo2.equals("nota") || atributo2.equals("season") && atributo1.equals("nota")) {
            System.out.println();
            System.out.print("* DIGITE A ESTAÇÃO DO ANO DOS ANIMES QUE DESEJA BUSCAR: ");
            season = sc.nextLine();
            System.out.print("* DIGITE A NOTA DOS ANIMES QUE DESEJA BUSCAR: ");
            nota = sc.nextFloat();
            sc.nextLine();

            offsets = lista.read(season, -1, nota, null, 10);
        } else if (atributo1.equals("episodios") && atributo2.equals("studio") || atributo2.equals("episodios") && atributo1.equals("studio")) {
            System.out.println();
            System.out.print("* DIGITE O NÚMERO DE EPISÓDIOS DOS ANIMES QUE DESEJA BUSCAR: ");
            eps = sc.nextInt();
            sc.nextLine();
            System.out.print("* DIGITE O ESTÚDIO DOS ANIMES QUE DESEJA BUSCAR: ");
            studio = sc.nextLine();

            offsets = lista.read(null, eps, -1, studio, 11);
        } else if (atributo1.equals("episodios") && atributo2.equals("nota") || atributo2.equals("episodios") && atributo1.equals("nota")) {
            System.out.println();
            System.out.print("* DIGITE O NÚMERO DE EPISÓDIOS DOS ANIMES QUE DESEJA BUSCAR: ");
            eps = sc.nextInt();
            sc.nextLine();
            System.out.print("* DIGITE A NOTA DOS ANIMES QUE DESEJA BUSCAR: ");
            nota = sc.nextFloat();
            sc.nextLine();
            offsets = lista.read(null, eps, nota, null, 12);
        } 

        for (int i = 0; i < offsets.size(); i++) {
            long offsetAtual = offsets.get(i);

            if (offsetAtual == -1) {
                System.out.println("ANIME EXCLUÍDO OU INEXISTENTE!");
            } else {

                // Vai para posição
                arq.seek(offsetAtual);

                lapide = arq.readChar();
                int tamanhoRegistro = arq.readShort();

                // Lê os bytes do registro
                byte[] ba = new byte[tamanhoRegistro];
                arq.read(ba);
                anime.fromByteArray(ba);

                if (lapide != '*') {
                    anime.write();
                    System.out.println(" --- (ENDEREÇO: @" + offsetAtual + ") --- ");
                    resp2 = true;
                } else if (lapide == '*') {
                    resp = true;
                }

                if (!resp2 && resp) {
                    System.out.println("* OBJETO EXCLUÍDO OU NÃO EXISTE!");
                } else if (anime.getId() < 18495 && resp) {
                    System.out.println("* OBJETO DESLOCADO PARA O FIM DO ARQUIVO!");
                }
            }
        }
    }

    public boolean update(Animes novo_anime, ArvoreBMais arvore, HashExtensivo hash, ListaInvertida lista, int tipo) throws Exception {
        Animes anime = new Animes();
        char lapide;
        long offset = -1;

        switch (tipo) {
            case 1:
                offset = arvore.read(novo_anime.getId());
                break;
            case 2:
                offset = hash.read(novo_anime.getId());
                break;
            case 3:
                offset = hash.read(novo_anime.getId());
                break;
            case 4:
                offset = hash.read(novo_anime.getId());
                break;
            default:
                break;
        }

        // Vai para posição
        arq.seek(offset);

        lapide = arq.readChar();
        int tamanhoRegistro = arq.readShort();

        // Lê os bytes do registro
        byte[] ba = new byte[tamanhoRegistro];
        arq.read(ba);
        anime.fromByteArray(ba);

        // Verifica se o registro não está marcado como excluído e se o ID corresponde
        if (lapide != '*') {
            byte[] objeto1 = anime.toByteArray();
            byte[] objeto2 = novo_anime.toByteArray();

            if (objeto2.length <= objeto1.length) {
                // Atualiza objeto no mesmo local
                arq.seek(offset);
                arq.writeChar(' ');
                arq.writeShort(tamanhoRegistro);
                arq.write(objeto2);
                System.out.println("* OBJETO ATUALIZADO NO MESMO LOCAL COM SUCESSO!");

                if (tipo == 3 || tipo == 4) {
                    lista.deleteEspecifico(anime, offset);
                    lista.create(novo_anime, offset);
                }
            } else {
                // Marca o registro antigo como excluído
                arq.seek(offset);
                arq.writeChar('*');

                // Move o novo objeto para o final
                arq.seek(arq.length());
                offset = arq.getFilePointer();

                arq.writeChar(' ');
                arq.writeShort(objeto2.length);
                arq.write(objeto2);

                switch (tipo) {
                    case 1:
                        arvore.delete(novo_anime.getId());
                        arvore.create(novo_anime, offset);
                        break;
                    case 2:
                        hash.update(novo_anime.getId(), offset);
                        break;
                    case 3:
                        lista.deleteEspecifico(anime, offset);
                        lista.create(novo_anime, offset);
                    case 4:
                        arvore.delete(novo_anime.getId());
                        arvore.create(novo_anime, offset);
                        hash.update(novo_anime.getId(), offset);
                        lista.deleteEspecifico(anime, offset);
                        lista.create(novo_anime, offset);
                        break;
                    default:
                        break;
                }

                System.out.println("* OBJETO ATUALIZADO E DESLOCADO PARA O FINAL DO ARQUIVO!");
                System.out.println(" --- (ENDEREÇO: @" + offset + ") --- ");
            }
            return true;
        }

        return false;
    }

    public boolean delete(int id, ArvoreBMais arvore, HashExtensivo hash, ListaInvertida lista, int tipo) throws Exception, IOException{
        Animes anime = new Animes();
        boolean delete = false;
        char lapide;
        long offset = -1;

        switch (tipo) {
            case 1:
            case 4:
                offset = arvore.read(id);
                break;
            case 2:
            case 3:
                offset = hash.read(id);
                break;
            default:
                break;
        }

        // Vai para posição
        if (offset < 0 || offset >= arq.length()) {
            System.out.println("* ERRO: ID NÃO ENCONTRADO OU OFFSET INVÁLIDO *");
            System.out.println(offset);
            return false;
        }
        System.out.println(offset);
        arq.seek(offset);

        lapide = arq.readChar();
        int tamanhoRegistro = arq.readShort();

        byte[] ba = new byte[tamanhoRegistro];
        arq.read(ba);
        anime.fromByteArray(ba);
        anime.write();

        // Verifica se o registro não está marcado como excluído e se o ID corresponde
        if (lapide != '*') {
            arq.seek(offset);
            arq.writeChar('*');
            delete = true;
        }

        boolean excluido1 = false;
        boolean excluido2 = false;
        boolean excluido3 = false;

        switch (tipo) {
            case 1:
                excluido1 = arvore.delete(id);
                break;
            case 2:
                excluido2 = hash.delete(id);
                break;
            case 3:
            case 4:
                excluido3 = lista.deleteEspecifico(anime, offset);
                if (tipo == 4) {
                    excluido1 = arvore.delete(id);
                    excluido2 = hash.delete(id);
                }
                break;
            default:
                break;
        }

        if (excluido1) {
            System.out.println("* ANIME DELETADO DA ÂRVORE B+ COM SUCESSO! *");
        }

        if (excluido2) {
            System.out.println("* ANIME DELETADO DO HASH EXTENSIVO COM SUCESSO! *");
        }

        if (excluido3) {
            System.out.println("* ANIME DELETADO DA LISTA INVERTIDA COM SUCESSO! *");
        }

        if (!excluido1 && !excluido2 && !excluido3) {
            System.out.println("* ERRO AO DELETAR ANIME DO ARQUIVO DE ÍNDICE! *");
        }

        return delete;
    }

    public boolean deleteLista(String atributo, HashExtensivo hash, ListaInvertida lista) throws Exception {
        Animes anime = new Animes();
        boolean delete1 = false;
        boolean delete2 = false;
        char lapide;

        List<Long> offsets = new ArrayList<>();

        switch (atributo) {
            case "season":
                System.out.print("* DIGITE A ESTAÇÃO DO ANO DOS ANIMES QUE DESEJA DELETAR: ");
                String season = sc.nextLine();
                offsets = lista.read(season, -1, -1, null, 3);
                delete2 = lista.delete(season, -1, -1, null, 3);
                break;
            case "episodios":
                System.out.print("* DIGITE O NÚMERO DE EPISÓDIOS DOS ANIMES QUE DESEJA BUSCAR: ");
                int eps = sc.nextInt();
                sc.nextLine();
                offsets = lista.read(null, eps, -1, null, 4);
                delete2 = lista.delete(null, eps, -1, null, 4);
                break;
            case "studio":
                System.out.print("* DIGITE O ESTÚDIO DOS ANIMES QUE DESEJA BUSCAR: ");
                String studio = sc.nextLine();
                offsets = lista.read(null, -1, -1, studio, 5);
                delete2 = lista.delete(null, -1, -1, studio, 5);
                break;
            case "nota":
                System.out.print("* DIGITE A NOTA DOS ANIMES QUE DESEJA BUSCAR (MÁX: 5): ");
                float nota = sc.nextFloat();
                sc.nextLine();
                offsets = lista.read(null, -1, nota, null, 6);
                delete2 = lista.delete(null, -1, nota, null, 6);
                break;
            default:
                break;
        }

    for (long offsetAtual : offsets) {
        // Vai para posição
        arq.seek(offsetAtual);

        lapide = arq.readChar();
        int tamanhoRegistro = arq.readShort();

        // Lê os bytes do registro
        byte[] ba = new byte[tamanhoRegistro];
        arq.read(ba);
        anime.fromByteArray(ba);

        if (lapide != '*') {
            anime.write();
            arq.seek(offsetAtual);
            arq.writeChar('*');  // Apenas marca a lápide
            delete1 = true;
        }
    }

    if (delete2) {
        System.out.println("* ANIMES DELETADOS DA LISTA INVERTIDA COM SUCESSO! *");
    } else {
        System.out.println("* ERRO AO DELETAR ANIME DO ARQUIVO DE ÍNDICE! *");
    }

        return delete1;
    }

    public void close() throws Exception {
        arq.close();
    }
}
