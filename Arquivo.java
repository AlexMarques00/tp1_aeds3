import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class Arquivo {
    public RandomAccessFile arq;

    public Arquivo(String path) throws Exception {
        arq = new RandomAccessFile(path, "rw");
    }

    public void create(Animes anime, ArvoreBMais arvore, HashExtensivo hash, ListaInvertida lista) throws Exception {
        // Obtém a posição atual (offset) antes de escrever
        arq.seek(arq.length());
        long offset = arq.getFilePointer();

        // LAPIDE + TAM + OBEJTO
        byte[] objeto = anime.toByteArray();

        arq.writeChar(' ');
        arq.writeShort(objeto.length);
        arq.write(objeto);

        arvore.create(anime, offset);
        hash.create(anime.getId(), offset);
        lista.create(anime, offset);
    }

    public void read(int id, String name, int tipo, ArvoreBMais arvore, ListaInvertida lista, HashExtensivo hash)
            throws IOException, Exception {
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
                // offset = lista.read(name);
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

    public ArrayList<Animes> filtrar(MyDate data, String filtro, int tipo, int valorInt, float valorFloat)
            throws IOException {
        ArrayList<Animes> conjunto = new ArrayList<>();
        char lapide;

        // Pula Cabeçalho
        arq.seek(4);

        // Loop para ler todos os registros
        while (arq.getFilePointer() < arq.length()) {
            lapide = arq.readChar();
            int tamanhoRegistro = arq.readShort();

            // Lê os bytes do registro
            byte[] ba = new byte[tamanhoRegistro];
            arq.read(ba);

            Animes anime = new Animes();
            anime.fromByteArray(ba);

            switch (tipo) {
                // Verifica se o registro não está marcado como excluído e se o tipo corresponde
                case 3:
                    if (lapide != '*' && anime.getType().equals(filtro.trim())) {
                        conjunto.add(anime);
                    }
                    break;

                case 4:
                    if (lapide != '*' && anime.getSeason().equals(filtro.trim())) {
                        conjunto.add(anime);
                    }
                    break;
                case 5:
                    if (lapide != '*' && anime.getYear().equals(data)) {
                        conjunto.add(anime);
                    }
                    break;
                case 6:
                    if (lapide != '*' && anime.getEpisodes() == valorInt) {
                        conjunto.add(anime);
                    }
                    break;
                case 7:
                    if (lapide != '*' && anime.getRating() == valorFloat) {
                        conjunto.add(anime);
                    }
                    break;
                case 8:
                    if (lapide != '*' && anime.getStudio().equals(filtro.trim())) {
                        conjunto.add(anime);
                    }
                    break;
                case 9:
                    if (lapide != '*') {
                        ArrayList<String> genres = anime.getGenres(); // Obtém a lista de gêneros
                        if (!genres.isEmpty() && (genres.get(0).equalsIgnoreCase(filtro.trim()) ||
                                (genres.size() > 1 && genres.get(1).equalsIgnoreCase(filtro.trim())))) {
                            conjunto.add(anime); // Adiciona o registro ao conjunto
                        }
                    }
                    break;
            }
        }
        return conjunto; // Retorna a lista de registros encontrados
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
                // offset = lista.read(novo_anime.getName());
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

                arvore.delete(novo_anime.getId());
                arvore.create(novo_anime, offset);
                hash.update(novo_anime.getId(), offset);
                //lista.update(novo_anime.getName());

                System.out.println("* OBJETO ATUALIZADO E DESLOCADO PARA O FINAL DO ARQUIVO!");
                System.out.println(" --- (ENDEREÇO: @" + offset + ") --- ");
            }
            return true;
        }

        return false;
    }

    public boolean delete(int id, String nome, ArvoreBMais arvore, HashExtensivo hash, ListaInvertida lista, int tipo)
            throws Exception {
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
                // offset = lista.read(nome);
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

        boolean excluido1 = arvore.delete(id);
        boolean excluido2 = hash.delete(id);
        // boolean excluido3 = lista.delete(id);
        if (excluido1 && excluido2) {
            System.out.println("*ANIME DELETADO DO ARQUIVO DE ÍNDICE COM SUCESSO!*");
        } else {
            System.out.println("*ERRO AO DELETAR ANIME DO ARQUIVO DE ÍNDICE!*");
        }

        return delete;
    }

    public void close() throws Exception {
        arq.close();
    }
}
