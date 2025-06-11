import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.ArrayList;

public class LZW {
    public static final int BITS_POR_INDICE = 12; // Mínimo de 9 bits por índice (512 itens no dicionário)
    
    // CODIFICAÇÃO POR LZW
    public static byte[] codifica(byte[] msgBytes) throws Exception {
        ArrayList<ArrayList<Byte>> dicionario = new ArrayList<>();
        ArrayList<Byte> vetorBytes;
        int i, j;
        byte b;
        for (j = -128; j < 128; j++) {
            b = (byte) j;
            vetorBytes = new ArrayList<>();
            vetorBytes.add(b);
            dicionario.add(vetorBytes);
        }

        // Adiciona o byte 256 (0x00) como um item especial no dicionário
        ArrayList<Integer> saida = new ArrayList<>();
        i = 0;
        int indice;
        int ultimoIndice;

        // Inicia o índice com -1 para indicar que ainda não foi encontrado
        while (i < msgBytes.length) {
            vetorBytes = new ArrayList<>();
            b = msgBytes[i];
            vetorBytes.add(b);
            indice = dicionario.indexOf(vetorBytes);
            ultimoIndice = indice;

            // Continua adicionando bytes ao vetor até que não seja encontrado no dicionário
            while (indice != -1 && i < msgBytes.length - 1) {
                i++;
                b = msgBytes[i];
                vetorBytes.add(b);
                indice = dicionario.indexOf(vetorBytes);
                if (indice != -1)
                    ultimoIndice = indice;
            }

            // Se o índice for -1, significa que o vetor não está no dicionário
            saida.add(ultimoIndice);

            // Adiciona o vetor atual ao dicionário, se ainda não estiver cheio
            if (dicionario.size() < (Math.pow(2, BITS_POR_INDICE) - 1))
                dicionario.add(vetorBytes);

            // Se o índice foi encontrado, incrementa o índice para continuar a busca
            if (indice != -1 && i == msgBytes.length - 1)
                break;
        }

        // Adiciona o último índice encontrado ao vetor de saída
        VetorDeBits bits = new VetorDeBits(saida.size() * BITS_POR_INDICE);
        int l = saida.size() * BITS_POR_INDICE - 1;
        // Preenche o vetor de bits com os índices encontrados
        for (i = saida.size() - 1; i >= 0; i--) {
            int n = saida.get(i);
            // Converte o índice para binário e preenche os bits
            // Cada índice é representado por BITS_POR_INDICE bits
            for (int m = 0; m < BITS_POR_INDICE; m++) {
                if (n % 2 == 0)
                    bits.clear(l);
                else
                    bits.set(l);
                l--;
                n /= 2;
            }
        }

        return bits.toByteArray();
    }

    // DECODIFICAÇÃO POR LZW
    public static byte[] decodifica(byte[] msgCodificada) throws Exception {
        VetorDeBits bits = new VetorDeBits(msgCodificada);

        int i, j, k;
        // Verifica se o tamanho do vetor de bits é múltiplo de BITS_POR_INDICE
        ArrayList<Integer> indices = new ArrayList<>();
        k = 0;
        for (i = 0; i < bits.length() / BITS_POR_INDICE; i++) {
            int n = 0;
            for (j = 0; j < BITS_POR_INDICE; j++) {
                n = n * 2 + (bits.get(k++) ? 1 : 0);
            }
            indices.add(n);
        }

        // Verifica se o último índice é 256 (0x00), que é o byte especial
        ArrayList<Byte> vetorBytes;
        ArrayList<Byte> msgBytes = new ArrayList<>();
        ArrayList<ArrayList<Byte>> dicionario = new ArrayList<>();
        byte b;
        for (j = -128, i = 0; j < 128; j++, i++) {
            b = (byte) j;
            vetorBytes = new ArrayList<>();
            vetorBytes.add(b);
            dicionario.add(vetorBytes);
        }


        ArrayList<Byte> proximoVetorBytes;
        i = 0;
        // Se o primeiro índice for 256, adiciona o byte especial ao dicionário
        while (i < indices.size()) {
            vetorBytes = (ArrayList<Byte>) (dicionario.get(indices.get(i))).clone();
            for (j = 0; j < vetorBytes.size(); j++)
                msgBytes.add(vetorBytes.get(j));
            if (dicionario.size() < (Math.pow(2, BITS_POR_INDICE) - 1))
                dicionario.add(vetorBytes);
            i++;
            if (i < indices.size()) {
                proximoVetorBytes = (ArrayList<Byte>) dicionario.get(indices.get(i));
                vetorBytes.add(proximoVetorBytes.get(0));
            }
        }

        // Converte a lista de bytes para um vetor de bytes
        byte[] msgVetorBytes = new byte[msgBytes.size()];
        for (i = 0; i < msgBytes.size(); i++)
            msgVetorBytes[i] = msgBytes.get(i);

        return msgVetorBytes;
    }

    // Método para comprimir um arquivo usando LZW
    public static void comprimirArquivo(String caminhoEntrada, String caminhoSaida) throws Exception {
        // 1. Ler arquivo original
        byte[] dados = Files.readAllBytes(new File(caminhoEntrada).toPath());

        // 2. Codificar usando LZW
        byte[] comprimido = codifica(dados);

        // 3. Salvar arquivo comprimido
        try (FileOutputStream fos = new FileOutputStream(caminhoSaida)) {
            fos.write(comprimido);
        }
    }

    // Método para descomprimir um arquivo usando LZW
    public static void descomprimirArquivo(String caminhoComprimido, String caminhoSaida) throws Exception {
        // 1. Ler arquivo comprimido
        File arquivo = new File(caminhoComprimido);
        byte[] comprimido = new byte[(int) arquivo.length()];
        try (FileInputStream fis = new FileInputStream(arquivo)) {
            fis.read(comprimido);
        }

        // 2. Decodificar usando LZW
        byte[] descomprimido = decodifica(comprimido);

        // 3. Salvar arquivo descomprimido
        Files.write(new File(caminhoSaida).toPath(), descomprimido);
    }
}