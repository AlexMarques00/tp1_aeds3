import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class BoyerMoore {

    // Gera a tabela de deslocamento do caractere ruim
    private static int[] gerarTabelaRuim(byte[] padrao) {
        int[] tabela = new int[256]; // Para todos os valores possíveis de byte
        for (int i = 0; i < tabela.length; i++) tabela[i] = -1;
        for (int i = 0; i < padrao.length; i++) {
            tabela[padrao[i] & 0xFF] = i;
        }
        return tabela;
    }

    // Busca padrão no arquivo (byte[])
    public static ArrayList<Long> buscarPadrao(byte[] padrao, String caminhoArquivo) throws Exception {
        ArrayList<Long> posicoes = new ArrayList<>();
        try (RandomAccessFile raf = new RandomAccessFile(caminhoArquivo, "r")) {
            long fileLength = raf.length();
            int m = padrao.length;
            if (m == 0) return posicoes;
            int[] tabelaRuim = gerarTabelaRuim(padrao);

            // Busca usando o algoritmo de Boyer-Moore
            byte[] buffer = new byte[m];
            long i = 0;
            while (i <= fileLength - m) {
                raf.seek(i);
                raf.readFully(buffer);
                int j = m - 1;
                while (j >= 0 && buffer[j] == padrao[j]) j--;
                if (j < 0) {
                    posicoes.add(i);
                    i++;
                } else {
                    int desloc = j - tabelaRuim[buffer[j] & 0xFF];
                    i += Math.max(1, desloc);
                }
            }
        }
        return posicoes;
    }

    // Busca padrão no arquivo (String)
    public static ArrayList<Long> buscarPadrao(String padrao, String caminhoArquivo) throws Exception {
        return buscarPadrao(padrao.getBytes(StandardCharsets.UTF_8), caminhoArquivo);
    }
}