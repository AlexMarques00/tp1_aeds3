import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class KMP {
    private RandomAccessFile arqPtr;
    private byte[] chave;
    private int[] vetFalhas;
    private int estado;

    // Construtor
    private KMP(RandomAccessFile arqPtr, byte[] chave) {
        this.arqPtr = arqPtr;
        this.chave = new byte[chave.length];
        for (int i = 0; i < this.chave.length; i++) {
            this.chave[i] = chave[i];
        }
        vetFalhas = new int[chave.length];
        criaVetFalhas();
        estado = 0;
    }

    // Cria o vetor de falhas
    private void criaVetFalhas() {
        vetFalhas[0] = 0;
        int j = 0;
        for (int i = 1; i < chave.length; i++) {
            while (j > 0 && chave[i] != chave[j]) {
                j = vetFalhas[j - 1];
            }
            if (chave[i] == chave[j]) {
                j++;
            }
            vetFalhas[i] = j;
        }
    }

    // Algoritmo KMP para um byte
    private boolean algoritmo(byte carater) {
        while (estado > 0 && carater != chave[estado]) {
            estado = vetFalhas[estado - 1];
        }
        if (carater == chave[estado]) {
            estado++;
            if (estado == chave.length) {
                estado = vetFalhas[estado - 1];
                return true;
            }
        }
        return false;
    }

    // Busca todas as ocorrências do padrão no arquivo
    private ArrayList<Long> buscar() throws Exception {
        arqPtr.seek(0);
        ArrayList<Long> posicoes = new ArrayList<>();
        while (arqPtr.getFilePointer() < arqPtr.length()) {
            byte carater = arqPtr.readByte();
            long posAtual = arqPtr.getFilePointer();
            if (algoritmo(carater)) {
                long posChave = posAtual - chave.length;
                posicoes.add(posChave);
            }
        }
        return posicoes;
    }

    // Método estático para buscar padrão string no arquivo
    public static ArrayList<Long> buscarPadrao(String padrao, String caminhoArquivo) throws Exception {
        try (RandomAccessFile arq = new RandomAccessFile(caminhoArquivo, "r")) {
            KMP kmp = new KMP(arq, padrao.getBytes(StandardCharsets.UTF_8));
            return kmp.buscar();
        }
    }

    // Método estático para buscar padrão byte[] no arquivo
    public static ArrayList<Long> buscarPadrao(byte[] padrao, String caminhoArquivo) throws Exception {
        try (RandomAccessFile arq = new RandomAccessFile(caminhoArquivo, "r")) {
            KMP kmp = new KMP(arq, padrao);
            return kmp.buscar();
        }
    }
}