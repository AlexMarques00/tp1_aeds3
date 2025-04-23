import java.io.IOException;
import java.io.RandomAccessFile;

public class Arquivo {
    public RandomAccessFile arq;

    public Arquivo(String path) throws Exception {
        arq = new RandomAccessFile(path, "rw");
    }

    public void create(Animes anime, ArvoreBMais arvore, HashExtensivo hash, ListaInvertida lista, int tipo) throws Exception {
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

    public void read(int id, String name, int tipo, ArvoreBMais arvore, ListaInvertida lista, HashExtensivo hash) throws IOException, Exception {
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
            case 3:
                offset = lista.read(name);
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

    public boolean update(Animes novo_anime, ArvoreBMais arvore, HashExtensivo hash, ListaInvertida lista, int tipo)
            throws Exception {
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
                offset = lista.read(novo_anime.getName());
                break;
            case 4:
                offset = arvore.read(novo_anime.getId());
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
            } else {
                // Marca o registro antigo como excluído
                arq.seek(offset);
                arq.writeChar('*');

                // Move o novo objeto para o final
                offset = arq.length();
                arq.seek(arq.length());
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
                        lista.delete(novo_anime.getName());
                        lista.create(novo_anime, offset);
                    case 4:
                        arvore.delete(novo_anime.getId());
                        arvore.create(novo_anime, offset);
                        hash.update(novo_anime.getId(), offset);
                        lista.delete(novo_anime.getName());
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

    public boolean delete(int id, String nome, ArvoreBMais arvore, HashExtensivo hash, ListaInvertida lista, int tipo) throws Exception {
        Animes anime = new Animes();
        boolean delete = false;
        char lapide;
        long offset = -1;

        switch (tipo) {
            case 1:
                offset = arvore.read(id);
                break;
            case 2:
                offset = hash.read(id);
                break;
            case 3:
                offset = lista.read(nome);
                break;
            case 4:
                offset = arvore.read(id);
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
            arq.seek(offset);
            arq.writeChar('*');
            anime.write();
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
                excluido3 = lista.delete(nome);
            case 4:
                excluido1 = arvore.delete(id);
                excluido2 = hash.delete(id);
                excluido3 = lista.delete(nome);
                break;
            default:
                break;
        }

        if (excluido1) {
            System.out.println("*ANIME DELETADO DA ÂRVORE B+ COM SUCESSO!*");
        } 
        
        if (excluido2) {
            System.out.println("*ANIME DELETADO DO HASH EXTENSIVO COM SUCESSO!*");
        } 
        
        if (excluido3) {
            System.out.println("*ANIME DELETADO DA LISTA INVERTIDA COM SUCESSO!*");
        } 
        
        if (!excluido1 && !excluido2 && !excluido3) {
            System.out.println("*ERRO AO DELETAR ANIME DO ARQUIVO DE ÍNDICE!*");
        }

        return delete;
    }

    public void close() throws Exception {
        arq.close();
    }
}
