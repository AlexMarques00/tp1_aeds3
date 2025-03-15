import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class Arquivo {
    public RandomAccessFile arq;

    public Arquivo(String path) throws Exception {
        arq = new RandomAccessFile(path, "rw");
    }

    public void create(Animes anime) throws Exception {
        arq.seek(arq.length());
        // LAPIDE + TAM + OBEJTO
        byte[] objeto = anime.toByteArray();

        arq.writeChar(' ');
        arq.writeShort(objeto.length);
        arq.write(objeto);
    }

    public void read(int id, String name, int tipo) throws IOException {
        Animes anime = new Animes();
        char lapide;
        boolean resp = false;
        boolean resp2 = false;

        // Pula Cabeçalho
        arq.seek(4);

        // Loop para ler todos os registros
        while (arq.getFilePointer() < arq.length()) {
            lapide = arq.readChar();
            int tamanhoRegistro = arq.readShort();

            // Lê os bytes do registro
            byte[] ba = new byte[tamanhoRegistro];
            arq.read(ba);
            anime.fromByteArray(ba);

            if (tipo == 1) {
                // Verifica se o ID corresponde e se não está marcado como excluído
                if (lapide != '*' && anime.getId() == id) {
                    anime.write();
                    resp2 = true;
                    break;
                } else if (lapide == '*' && anime.getId() == id) {
                    resp = true;
                }
            } else if (tipo == 2) {
                // Verifica se o nome corresponde e se não está marcado como excluído
                if (lapide != '*' && anime.getName().trim().equals(name.trim())) {
                    anime.write();
                    resp2 = true;
                    break;
                } else if (lapide == '*' && anime.getName().trim().equals(name.trim())) {
                    resp = true;
                }
            }
        }

        if (!resp2) {
            System.out.println("* OBJETO EXCLUÍDO OU NÃO EXISTE!");
        } else if (resp && id < 18495) {
            System.out.println("* OBJETO DESLOCADO PARA O FIM DO ARQUIVO!");
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
                        conjunto.add(anime); // Adiciona o registro ao conjunto
                    }
                    break;

                case 4:
                    if (lapide != '*' && anime.getSeason().equals(filtro.trim())) {
                        conjunto.add(anime); // Adiciona o registro ao conjunto
                    }
                    break;
                case 5:
                    if (lapide != '*' && anime.getYear().equals(data)) {
                        conjunto.add(anime); // Adiciona o registro ao conjunto
                    }
                    break;
                case 6:
                    if (lapide != '*' && anime.getEpisodes() == valorInt) {
                        conjunto.add(anime); // Adiciona o registro ao conjunto
                    }
                    break;
                case 7:
                    if (lapide != '*' && anime.getRating() == valorFloat) {
                        conjunto.add(anime); // Adiciona o registro ao conjunto
                    }
                    break;
                case 8:
                    if (lapide != '*' && anime.getStudio().equals(filtro.trim())) {
                        conjunto.add(anime); // Adiciona o registro ao conjunto
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

    public boolean update(Animes novo_anime) throws Exception {
        Animes anime = new Animes();
        char lapide;

        // Pula Cabeçalho
        arq.seek(4);

        // Loop para ler todos os registros
        while (arq.getFilePointer() < arq.length()) {
            long posicaoInicialRegistro = arq.getFilePointer();

            lapide = arq.readChar();
            int tamanhoRegistro = arq.readShort();

            // Lê os bytes do registro
            byte[] ba = new byte[tamanhoRegistro];
            arq.read(ba);
            anime.fromByteArray(ba);

            // Verifica se o registro não está marcado como excluído e se o ID corresponde
            if (lapide != '*' && anime.getId() == novo_anime.getId()) {
                byte[] objeto1 = anime.toByteArray();
                byte[] objeto2 = novo_anime.toByteArray();

                if (objeto2.length <= objeto1.length) {
                    // Atualiza objeto no mesmo local
                    arq.seek(posicaoInicialRegistro);
                    arq.writeChar(' ');
                    arq.writeShort(tamanhoRegistro);
                    arq.write(objeto2);
                    System.out.println("* OBJETO ATUALIZADO NO MESMO LOCAL COM SUCESSO!");
                } else {
                    // Marca o registro antigo como excluído
                    arq.seek(posicaoInicialRegistro);
                    arq.writeChar('*');

                    // Move o novo objeto para o final
                    arq.seek(arq.length());
                    arq.writeChar(' ');
                    arq.writeShort(objeto2.length);
                    arq.write(objeto2);

                    System.out.println("* OBJETO ATUALIZADO E DESLOCADO PARA O FINAL DO ARQUIVO!");
                }
                return true;
            }
        }
        return false;
    }

    public boolean delete(int id) throws Exception {
        Animes anime = new Animes();
        char lapide;

        // Pula Cabeçalho
        arq.seek(4);

        // Loop para ler todos os registros
        while (arq.getFilePointer() < arq.length()) {
            long posicaoInicialRegistro = arq.getFilePointer();

            lapide = arq.readChar();
            int tamanhoRegistro = arq.readShort();

            // Lê os bytes do registro
            byte[] ba = new byte[tamanhoRegistro];
            arq.read(ba);
            anime.fromByteArray(ba);

            // Verifica se o registro não está marcado como excluído e se o ID corresponde
            if (lapide != '*' && anime.getId() == id) {
                arq.seek(posicaoInicialRegistro);
                arq.writeChar('*');
                anime.write();
                return true;
            }
        }

        return false;
    }

    public void close() throws Exception {
        arq.close();
    }
}
