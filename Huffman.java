import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Huffman {

    public static HashMap<Byte, String> codifica(byte[] sequencia) {
        // Classe interna para representar um nó da árvore de Huffman
        HashMap<Byte, Integer> mapaDeFrequencias = new HashMap<>();
        for (byte c : sequencia) {
            // Conta a frequência de cada byte na sequência
            mapaDeFrequencias.put(c, mapaDeFrequencias.getOrDefault(c, 0) + 1);
        }

        // Cria uma fila de prioridade para construir a árvore de Huffman
        PriorityQueue<NoHuffman> pq = new PriorityQueue<>();
        for (Byte b : mapaDeFrequencias.keySet()) {
            pq.add(new NoHuffman(b, mapaDeFrequencias.get(b)));
        }

        // Enquanto houver mais de um nó na fila, combina os dois de menor frequência
        // para formar um novo nó pai, até restar apenas um nó (a raiz da árvore)
        while (pq.size() > 1) {
            NoHuffman esquerdo = pq.poll();
            NoHuffman direito = pq.poll();

            // Cria um novo nó pai com a soma das frequências dos dois nós
            // e adiciona os nós esquerdo e direito a ele
            NoHuffman pai = new NoHuffman((byte)0, esquerdo.frequencia + direito.frequencia);
            pai.esquerdo = esquerdo;
            pai.direito = direito;

            pq.add(pai);
        }

        // A raiz da árvore de Huffman é o único nó restante na fila
        NoHuffman raiz = pq.poll();
        HashMap<Byte, String> codigos = new HashMap<>();
        constroiCodigos(raiz, "", codigos);

        return codigos;
    }

    // Classe interna para representar um nó da árvore de Huffman
    private static void constroiCodigos(NoHuffman no, String codigo, HashMap<Byte, String> codigos) {
        if (no == null) return;

        // Folha: nó com símbolo válido
        if (no.esquerdo == null && no.direito == null) {
            codigos.put(no.b, codigo.length() > 0 ? codigo : "0"); // caso só um símbolo
        }

        // Recursivamente constrói os códigos para os nós esquerdo e direito
        constroiCodigos(no.esquerdo, codigo + "0", codigos);
        constroiCodigos(no.direito, codigo + "1", codigos);
    }

    // Decodifica usando VetorDeBits e tabela de códigos
    public static byte[] decodifica(VetorDeBits bits, HashMap<Byte, String> codigos) {
        ByteArrayOutputStream sequenciaDecodificada = new ByteArrayOutputStream();
        NoHuffman raiz = reconstruirArvore(codigos);
        NoHuffman atual = raiz;
        int len = bits.length();
        // Percorre o VetorDeBits e decodifica
        // Cada bit indica se deve ir para a esquerda (0) ou direita (1)
        for (int i = 0; i < len; i++) {
            if (bits.get(i)) {
                atual = atual.direito;
            } else {
                atual = atual.esquerdo;
            }
            // Se chegou em uma folha
            if (atual.esquerdo == null && atual.direito == null) {
                sequenciaDecodificada.write(atual.b);
                atual = raiz;
            }
        }
        return sequenciaDecodificada.toByteArray();
    }

    private static NoHuffman reconstruirArvore(HashMap<Byte, String> codigos) {
        NoHuffman raiz = new NoHuffman((byte)0, 0);
        // Percorre a tabela de códigos e constrói a árvore
        // Cada código é uma sequência de '0's e '1's que define o caminho na árvore
        for (HashMap.Entry<Byte, String> entry : codigos.entrySet()) {
            NoHuffman atual = raiz;
            String codigo = entry.getValue();
            // Percorre o código e cria os nós na árvore
            // '0' significa ir para a esquerda, '1' significa ir para a direita
            for (int i = 0; i < codigo.length(); i++) {
                char c = codigo.charAt(i);
                if (c == '0') {
                    if (atual.esquerdo == null)
                        atual.esquerdo = new NoHuffman((byte)0, 0);
                    atual = atual.esquerdo;
                } else {
                    if (atual.direito == null)
                        atual.direito = new NoHuffman((byte)0, 0);
                    atual = atual.direito;
                }
            }
            atual.b = entry.getKey();
        }
        return raiz;
    }

    public static void comprimirArquivo(String caminhoEntrada, String caminhoSaida) throws IOException {
        // 1. Ler arquivo original
        byte[] dados = Files.readAllBytes(new File(caminhoEntrada).toPath());

        // 2. Gerar tabela de códigos
        HashMap<Byte, String> codigos = codifica(dados);

        // 3. Gerar VetorDeBits comprimido
        VetorDeBits bits = new VetorDeBits();
        int i = 0;
        for (byte b : dados) {
            String codigo = codigos.get(b);
            for (char c : codigo.toCharArray()) {
                if (c == '0') bits.clear(i++);
                else bits.set(i++);
            }
        }
        byte[] comprimido = bits.toByteArray();

        // 4. Salvar tabela de códigos + dados comprimidos
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(caminhoSaida))) {
            oos.writeObject(codigos); // salva tabela
            oos.writeInt(i); // salva quantidade de bits válidos
            oos.writeInt(comprimido.length); // salva tamanho do array de bytes comprimido
            oos.write(comprimido); // salva dados comprimidos
        }
    }

    public static void descomprimirArquivo(String caminhoComprimido, String caminhoSaida) throws IOException, ClassNotFoundException {
        // 1. Ler tabela de códigos + dados comprimidos
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(caminhoComprimido))) {
            HashMap<Byte, String> codigos = (HashMap<Byte, String>) ois.readObject();
            int numBits = ois.readInt();
            int tamanhoBytes = ois.readInt();
            byte[] comprimido = new byte[tamanhoBytes];
            ois.readFully(comprimido);

            // 2. Reconstruir VetorDeBits sem classe anônima
            VetorDeBits bits = new VetorDeBits(comprimido, numBits);

            // 3. Descomprimir
            byte[] descomprimido = decodifica(bits, codigos);

            // 4. Salvar arquivo descomprimido
            Files.write(new File(caminhoSaida).toPath(), descomprimido);
        }
    }
}