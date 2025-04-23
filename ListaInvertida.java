import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UTFDataFormatException;
import java.util.ArrayList;
import java.util.HashMap;

public class ListaInvertida {
    private RandomAccessFile arqDicionario;
    private RandomAccessFile arqBlocos;
    private int quantDadosPorBloco;
    private HashMap<String, Long> dicionarioMap = new HashMap<>(); // Cache do dicionário
    private byte[] blocoBuffer; // Buffer reutilizável

    public ListaInvertida(int n) throws Exception {
        this.arqDicionario = new RandomAccessFile("Dicionario.db", "rw");
        this.arqBlocos = new RandomAccessFile("Blocos.db", "rw");
        this.quantDadosPorBloco = n;
        this.blocoBuffer = new byte[new Bloco(n).size()];

        // Inicializa o arquivo Dicionario.db se estiver vazio
        if (arqDicionario.length() == 0) {
            arqDicionario.writeInt(0); // Escreve 0 entidades
        }

        // Carrega o número de entidades (não mais usado, mas lido para posicionar o ponteiro)
        arqDicionario.seek(0);
        int numEntidadesArquivo = arqDicionario.readInt();

        // Limpa o cache e recarrega as entradas válidas
        dicionarioMap.clear();
        long pos;
        while ((pos = arqDicionario.getFilePointer()) < arqDicionario.length()) {
            try {
                String chave = arqDicionario.readUTF();
                long endereco = arqDicionario.readLong();
                dicionarioMap.put(chave, endereco);
            } catch (EOFException | UTFDataFormatException e) {
                // Dados inválidos, para a leitura
                break;
            }
        }

        // Atualiza o número de entidades no arquivo para refletir o mapa
        arqDicionario.seek(0);
        arqDicionario.writeInt(dicionarioMap.size());
    }

    // Incrementa o número de entidades
    public void incrementaEntidades() throws Exception {
        arqDicionario.seek(0);
        int n = arqDicionario.readInt();
        arqDicionario.seek(0);
        arqDicionario.writeInt(n + 1);
    }

    // Decrementa o número de entidades
    public void decrementaEntidades() throws Exception {
        arqDicionario.seek(0);
        int n = arqDicionario.readInt();
        arqDicionario.seek(0);
        arqDicionario.writeInt(n - 1);
    }

    // Retorna o número de entidades
    public int numeroEntidades() throws Exception {
        arqDicionario.seek(0);
        return arqDicionario.readInt();
    }

    public boolean create(Animes anime, long offset) throws IOException, Exception {
        int id = anime.getId();
        String name = anime.getName();

        // Verifica se o nome já existe (garante unicidade)
        if (dicionarioMap.containsKey(name)) {
            return false; // Nome já cadastrado
        }

        // Cria novo bloco para a chave
        Bloco novoBloco = new Bloco(quantDadosPorBloco);
        long endereco = arqBlocos.length();
        arqBlocos.seek(endereco);
        arqBlocos.write(novoBloco.toByteArray());

        // Insere no dicionário
        arqDicionario.seek(arqDicionario.length());
        arqDicionario.writeUTF(name);
        arqDicionario.writeLong(endereco);
        dicionarioMap.put(name, endereco);
        incrementaEntidades();

        // Insere o primeiro registro no bloco
        novoBloco.create(id, offset);
        arqBlocos.seek(endereco);
        arqBlocos.write(novoBloco.toByteArray());

        return true;
    }

    public Long read(String name) throws IOException, Exception {
        if (!dicionarioMap.containsKey(name)) {
            return null;
        }

        long enderecoBloco = dicionarioMap.get(name);
        Bloco blocoAtual = new Bloco(quantDadosPorBloco);

        // Percorre a cadeia de blocos
        while (enderecoBloco != -1) {
            arqBlocos.seek(enderecoBloco);
            arqBlocos.readFully(blocoBuffer);
            blocoAtual.fromByteArray(blocoBuffer);

            // Retorna o primeiro offset encontrado (nome é único)
            if (blocoAtual.quantidade > 0) {
                return blocoAtual.offsets[0]; // Apenas o primeiro registro
            }

            enderecoBloco = blocoAtual.proximo;
        }

        return null; // Caso não encontre (não deveria acontecer)
    }

    public boolean delete(String name) throws IOException, Exception {
        if (!dicionarioMap.containsKey(name)) {
            return false; // Nome não existe
        }

        long enderecoBloco = dicionarioMap.get(name);
        Bloco blocoAtual = new Bloco(quantDadosPorBloco);

        // Carrega o bloco
        arqBlocos.seek(enderecoBloco);
        arqBlocos.readFully(blocoBuffer);
        blocoAtual.fromByteArray(blocoBuffer);

        // Como o nome é único, só há 1 ID no bloco
        if (blocoAtual.quantidade == 0) {
            return false; // Já está vazio (não deveria acontecer)
        }

        int id = blocoAtual.elementos[0];
        boolean removido = blocoAtual.delete(id);

        if (removido) {
            // Atualiza o bloco no arquivo
            arqBlocos.seek(enderecoBloco);
            arqBlocos.write(blocoAtual.toByteArray());

            // Se o bloco ficou vazio, remove do dicionário e libera espaço
            if (blocoAtual.empty()) {
                // Remove a entrada do dicionário
                dicionarioMap.remove(name);
                decrementaEntidades();

                // Atualiza o cabeçalho do arquivo Dicionario.db
                arqDicionario.seek(0);
                arqDicionario.writeInt(dicionarioMap.size());

                // (Opcional) Marcar o bloco como livre para reutilização
                // Isso depende da sua implementação de gerenciamento de espaço livre
            }
            return true;
        }

        return false;
    }

    public void print() throws Exception {
        System.out.println("\nLISTAS INVERTIDAS:");

        // Itera diretamente sobre o cache do dicionário
        for (HashMap.Entry<String, Long> entry : dicionarioMap.entrySet()) {
            String chave = entry.getKey();
            long enderecoBloco = entry.getValue();

            ArrayList<String> itens = new ArrayList<>();
            Bloco blocoAtual = new Bloco(quantDadosPorBloco);

            // Percorre a cadeia de blocos usando buffer reutilizável
            while (enderecoBloco != -1) {
                arqBlocos.seek(enderecoBloco);
                arqBlocos.readFully(blocoBuffer); // Usa buffer pré-alocado
                blocoAtual.fromByteArray(blocoBuffer);

                // Coleta IDs e offsets
                for (int i = 0; i < blocoAtual.quantidade; i++) {
                    itens.add(blocoAtual.elementos[i] + "@" + blocoAtual.offsets[i]);
                }

                enderecoBloco = blocoAtual.proximo;
            }

            // Ordenação otimizada (evita parsing repetido)
            itens.sort((a, b) -> {
                int idA = Integer.parseInt(a.split("@")[0]);
                int idB = Integer.parseInt(b.split("@")[0]);
                return Integer.compare(idA, idB);
            });

            // Exibição formatada
            System.out.print(chave + " - ");
            for (String item : itens) {
                String[] partes = item.split("@");
                System.out.println("ID: " + partes[0] + " - @Endereço: " + partes[1]);
            }
            System.out.println("\n" + "-".repeat(50));
        }
    }

    public void close() throws IOException {
        arqDicionario.close();
        arqBlocos.close();
    }
}