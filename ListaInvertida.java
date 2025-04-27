import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class ListaInvertida {
    private RandomAccessFile arqDicionario;
    private RandomAccessFile arqBlocos;
    private int quantDadosPorBloco;
    private HashMap<String, Long> dicionarioMap = new HashMap<>();
    private byte[] blocoBuffer;

    public ListaInvertida(int n) throws Exception {
        this.quantDadosPorBloco = n;
        this.arqDicionario = new RandomAccessFile("Dicionario.db", "rw");
        this.arqBlocos = new RandomAccessFile("Blocos.db", "rw");
        this.blocoBuffer = new byte[new Bloco(n).size()];

        if (arqDicionario.length() == 0) {
            arqDicionario.writeInt(0);
        }

        carregarDicionario();
    }

    private void carregarDicionario() throws IOException {
        dicionarioMap.clear();
        arqDicionario.seek(0);
        int numEntidades = arqDicionario.readInt();

        while (arqDicionario.getFilePointer() < arqDicionario.length()) {
            String chave = arqDicionario.readUTF();
            long endereco = arqDicionario.readLong();
            dicionarioMap.put(chave, endereco);
        }
    }

    public boolean create(Animes anime, long offset) throws IOException, InterruptedException {
        String seasonKey = "season:" + anime.getSeason();
        String episodesKey = "episodes:" + anime.getEpisodes();
        String studiokey = "studio:" + anime.getStudio();
        String ratingkey = "nota:" + anime.getRating();

        processarChave(seasonKey, offset);
        processarChave(episodesKey, offset);
        processarChave(studiokey, offset);
        processarChave(ratingkey, offset);

        return true;
    }

    private void processarChave(String chave, long offset) throws IOException, InterruptedException {
        Long enderecoBloco = dicionarioMap.get(chave);
        Bloco blocoAtual;
        boolean novoBlocoCriado = false;
    
        if (enderecoBloco == null) {
            // Criação do primeiro bloco para a chave
            blocoAtual = new Bloco(quantDadosPorBloco);
            enderecoBloco = arqBlocos.length();
            arqBlocos.seek(enderecoBloco);
            arqBlocos.write(blocoAtual.toByteArray());
            
            dicionarioMap.put(chave, enderecoBloco);
            atualizarDicionario(chave, enderecoBloco);
        } else {
            blocoAtual = lerBloco(enderecoBloco);
        }
    
        String elemento = chave.split(":")[1];
        
        // Tenta inserir no último bloco da cadeia
        while (blocoAtual.full()) {
            // Bloco cheio: cria novo bloco e avança
            Long novoEndereco = adicionarNovoBloco(blocoAtual, enderecoBloco);
            enderecoBloco = novoEndereco;
            blocoAtual = lerBloco(enderecoBloco);
            novoBlocoCriado = true;
        }
    
        // Insere no bloco atual (que agora tem espaço)
        blocoAtual.create(elemento, offset);
        escreverBloco(blocoAtual, enderecoBloco);
    
        // Atualiza dicionário apenas se for o primeiro bloco
        if (novoBlocoCriado && dicionarioMap.get(chave).equals(enderecoBloco)) {
            dicionarioMap.put(chave, enderecoBloco);
            atualizarDicionario(chave, enderecoBloco);
        }
    }

    public List<Long> read(String season, int episodes, float nota, String studio, int tipo) throws IOException {
        List<Long> resultados = new ArrayList<>();

        if (tipo == 3 || tipo == 7 || tipo == 9 || tipo == 10) {
            String seasonKey = "season:" + season;
            coletarEnderecos(seasonKey, resultados);
        }

        if (tipo == 4 || tipo == 7 || tipo == 11 || tipo == 12) {
            String episodesKey = "episodes:" + episodes;
            coletarEnderecos(episodesKey, resultados);
        }

        if (tipo == 5 || tipo == 8  || tipo == 9 || tipo == 11) {
            String episodesKey = "studio:" + studio;
            coletarEnderecos(episodesKey, resultados);
        }

        if (tipo == 6 || tipo == 8 || tipo == 10 || tipo == 12) {
            String episodesKey = "nota:" + nota;
            coletarEnderecos(episodesKey, resultados);
        }

        if (tipo == 7) {
            List<Long> seasonEnderecos = new ArrayList<>();
            List<Long> episodeEnderecos = new ArrayList<>();
            coletarEnderecos("season:" + season, seasonEnderecos);
            coletarEnderecos("episodes:" + episodes, episodeEnderecos);
            seasonEnderecos.retainAll(episodeEnderecos);
            resultados = seasonEnderecos;
        }

        if (tipo == 8) {
            List<Long> studioEnderecos = new ArrayList<>();
            List<Long> notaEnderecos = new ArrayList<>();
            coletarEnderecos("studio:" + studio, studioEnderecos);
            coletarEnderecos("nota:" + nota, notaEnderecos);
            studioEnderecos.retainAll(notaEnderecos);
            resultados = studioEnderecos;
        }

        if (tipo == 9) {
            List<Long> seasonEnderecos = new ArrayList<>();
            List<Long> studioEnderecos = new ArrayList<>();
            coletarEnderecos("season:" + season, seasonEnderecos);
            coletarEnderecos("studio:" + studio, studioEnderecos);
            seasonEnderecos.retainAll(studioEnderecos);
            resultados = seasonEnderecos;
        }

        if (tipo == 10) {
            List<Long> seasonEnderecos = new ArrayList<>();
            List<Long> notaEnderecos = new ArrayList<>();
            coletarEnderecos("season:" + season, seasonEnderecos);
            coletarEnderecos("nota:" + nota, notaEnderecos);
            seasonEnderecos.retainAll(notaEnderecos);
            resultados = seasonEnderecos;
        }

        if (tipo == 11) {
            List<Long> studioEnderecos = new ArrayList<>();
            List<Long> episodeEnderecos = new ArrayList<>();
            coletarEnderecos("studio:" + studio, studioEnderecos);
            coletarEnderecos("episodes:" + episodes, episodeEnderecos);
            studioEnderecos.retainAll(episodeEnderecos);
            resultados = studioEnderecos;
        }

        if (tipo == 12) {
            List<Long> notaEnderecos = new ArrayList<>();
            List<Long> episodeEnderecos = new ArrayList<>();
            coletarEnderecos("nota:" + nota, notaEnderecos);
            coletarEnderecos("episodes:" + episodes, episodeEnderecos);
            notaEnderecos.retainAll(episodeEnderecos);
            resultados = notaEnderecos;
        }

        return resultados;
    }

    private void coletarEnderecos(String chave, List<Long> resultados) throws IOException {
        Long enderecoBloco = dicionarioMap.get(chave);
        while (enderecoBloco != null && enderecoBloco != -1) {
            Bloco bloco = lerBloco(enderecoBloco);
            for (int i = 0; i < bloco.quantidade; i++) {
                resultados.add(bloco.offsets[i]);
            }
            enderecoBloco = bloco.proximo;
        }
    }

    public boolean deleteEspecifico (Animes anime, long offset) throws IOException {
        boolean deletedSeason = removerOffsetDaLista("season:" + anime.getSeason(), offset);
        boolean deletedEpisodes =removerOffsetDaLista("episodes:" + anime.getEpisodes(), offset);
        boolean deletedStudio = removerOffsetDaLista("studio:" + anime.getStudio(), offset);
        boolean deletedRating = removerOffsetDaLista("nota:" + anime.getRating(), offset);

        return deletedSeason || deletedEpisodes || deletedStudio || deletedRating;
    }

    private boolean removerOffsetDaLista(String chave, long targetOffset) throws IOException {
        Long enderecoBloco = dicionarioMap.get(chave);
        boolean removed = false;
    
        while (enderecoBloco != null && enderecoBloco != -1) {
            Bloco bloco = lerBloco(enderecoBloco);
            for (int i = 0; i < bloco.quantidade; i++) {
                if (bloco.offsets[i] == targetOffset) {
                    bloco.delete2(bloco.elementos[i], targetOffset);
                    escreverBloco(bloco, enderecoBloco);
                    removed = true;
                    break;
                }
            }
            enderecoBloco = bloco.proximo;
        }
    
        return removed;
    }

    public boolean delete(String season, int episodes, float nota, String studio, int tipo) throws IOException, InterruptedException {
        boolean deleted = false;

        // Delete por Season
        if (tipo == 3) {
            String seasonKey = "season:" + season;
            deleted |= processarDelete(seasonKey, season);
        }

        // Delete por Episodes
        if (tipo == 4) {
            String episodesKey = "episodes:" + episodes;
            deleted |= processarDelete(episodesKey, String.valueOf(episodes));
        }

        // Delete por Season
        if (tipo == 5) {
            String seasonKey = "season:" + season;
            deleted |= processarDelete(seasonKey, season);
        }

        // Delete por Nota
        if (tipo == 6) {
            String notaKey = "nota:" + nota;
            deleted |= processarDelete(notaKey, String.valueOf(nota));
        }

        return deleted;
    }

    private boolean processarDelete(String chave, String valor) throws IOException, InterruptedException {
        Long enderecoAtual = dicionarioMap.get(chave);
        if (enderecoAtual == null)
            return false;

        boolean deleted = false;
        Long enderecoAnterior = null;
        Bloco blocoAnterior = null;

        while (enderecoAtual != -1) {
            Bloco blocoAtual = lerBloco(enderecoAtual);
            boolean modificado = false;

            // Remove todas as ocorrências do valor no bloco
            for (int i = 0; i < blocoAtual.quantidade; i++) {
                if (blocoAtual.elementos[i].equals(valor)) {
                    blocoAtual.delete(valor);
                    modificado = true;
                    deleted = true;
                    i--; // Recheck same position after deletion
                }
            }

            if (modificado) {
                // Atualiza o bloco no arquivo
                escreverBloco(blocoAtual, enderecoAtual);

                // Remove bloco vazio da cadeia
                if (blocoAtual.empty()) {
                    Long proximo = blocoAtual.proximo;

                    // Atualiza o ponteiro do bloco anterior
                    if (enderecoAnterior != null && blocoAnterior != null) {
                        blocoAnterior.proximo = proximo;
                        escreverBloco(blocoAnterior, enderecoAnterior);
                    }
                    // Atualiza dicionário se for o primeiro bloco
                    else if (proximo != -1) {
                        dicionarioMap.put(chave, proximo);
                        atualizarDicionario(chave, proximo);
                    } else {
                        dicionarioMap.remove(chave);
                        atualizarDicionario(chave, null);
                    }

                    enderecoAtual = proximo;
                    continue;
                }
            }

            enderecoAnterior = enderecoAtual;
            blocoAnterior = blocoAtual;
            enderecoAtual = blocoAtual.proximo;
        }

        return deleted;
    }

    // Método auxiliar para atualizar o arquivo de dicionário
    private void atualizarDicionario(String chave, Long endereco) throws IOException, InterruptedException {
        arqDicionario.seek(0);
        int numEntidades = arqDicionario.readInt();

        // Cria arquivo temporário
        File tempFile = new File("Dicionario_temp.db");
        try (RandomAccessFile tempArq = new RandomAccessFile(tempFile, "rw")) {
            tempArq.writeInt(endereco != null ? numEntidades : numEntidades - 1);

            arqDicionario.seek(4);
            while (arqDicionario.getFilePointer() < arqDicionario.length()) {
                String currentKey = arqDicionario.readUTF();
                long currentAddr = arqDicionario.readLong();

                if (!currentKey.equals(chave)) {
                    tempArq.writeUTF(currentKey);
                    tempArq.writeLong(currentAddr);
                }
            }

            if (endereco != null) {
                tempArq.writeUTF(chave);
                tempArq.writeLong(endereco);
            }
        }

        // Fecha o arquivo antes da operação de substituição
        arqDicionario.close();

        // Substitui o arquivo original
        Files.move(Paths.get("Dicionario_temp.db"), Paths.get("Dicionario.db"), StandardCopyOption.REPLACE_EXISTING);
        Thread.sleep(1);

        // Reabre o arquivo atualizado
        arqDicionario = new RandomAccessFile("Dicionario.db", "rw");

        // Recarrega o dicionário na memória
        carregarDicionario();
    }

    private void escreverBloco(Bloco bloco, long endereco) throws IOException {
        arqBlocos.seek(endereco);
        arqBlocos.write(bloco.toByteArray());
    }

    private long adicionarNovoBloco(Bloco blocoAtual, long enderecoAtual) throws IOException {
        // Cria novo bloco e ajusta ponteiros
        Bloco novoBloco = new Bloco(quantDadosPorBloco);
        long novoEndereco = arqBlocos.length();

        // Atualiza o próximo do bloco atual
        blocoAtual.proximo = novoEndereco;
        escreverBloco(blocoAtual, enderecoAtual);

        // Escreve o novo bloco vazio
        arqBlocos.seek(novoEndereco);
        arqBlocos.write(novoBloco.toByteArray());

        return novoEndereco;
    }

    public void print() throws IOException {
        System.out.println("\nÍNDICE INVERTIDO:");
        System.out.println("==================================================");

        for (Map.Entry<String, Long> entry : dicionarioMap.entrySet()) {
            String chave = entry.getKey();
            Long enderecoBloco = entry.getValue();

            // Determina o tipo de chave
            String tipo = "";
            if (chave.startsWith("season:")) {
                tipo = "TEMPORADA";
            } else if (chave.startsWith("episodes:")) {
                tipo = "EPISÓDIOS";
            } else if (chave.startsWith("studio:")) {
                tipo = "ESTÚDIO";
            } else if (chave.startsWith("nota:")) {
                tipo = "NOTA";
            }           
            String valor = chave.split(":")[1];

            System.out.printf("%s %s:\n", tipo, valor);

            int blocoCount = 1;
            while (enderecoBloco != -1) {
                Bloco bloco = lerBloco(enderecoBloco);
                System.out.printf("  Bloco %d (%d registros):\n", blocoCount++, bloco.quantidade);

                for (int i = 0; i < bloco.quantidade; i++) {
                    System.out.printf("    - %s: Offset %d\n", bloco.elementos[i], bloco.offsets[i]);
                }

                enderecoBloco = bloco.proximo;
            }
            System.out.println("--------------------------------------------------");
        }
    }

    // Método auxiliar para ler um bloco do arquivo
    private Bloco lerBloco(long endereco) throws IOException {
        Bloco bloco = new Bloco(quantDadosPorBloco);
        arqBlocos.seek(endereco);
        arqBlocos.readFully(blocoBuffer);
        bloco.fromByteArray(blocoBuffer);
        return bloco;
    }

    public void close() throws IOException {
        arqDicionario.close();
        arqBlocos.close();
    }
}