import java.io.*;
import java.util.Arrays;

public class Bloco {
    private static final int MAX_STRING_LENGTH = 100; // Tamanho máximo para a String
    short quantidade;
    short quantMax;
    String[] elementos; // Alterado para String
    long[] offsets;
    long proximo;
    short bytesPorBloco;

    public Bloco(int qtdmax) {
        this.quantidade = 0;
        this.quantMax = (short) qtdmax;
        this.elementos = new String[quantMax];
        this.offsets = new long[quantMax];
        Arrays.fill(offsets, -1L);
        this.proximo = -1;
        this.bytesPorBloco = (short) (2 + (quantMax * (MAX_STRING_LENGTH + 8)) + 8);
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeShort(quantidade);
        
        for (int i = 0; i < quantMax; i++) {
            String elemento = (elementos[i] != null) ? elementos[i] : "";
            byte[] elementoBytes = elemento.getBytes("UTF-8");
            byte[] fixedElemento = new byte[MAX_STRING_LENGTH];
            System.arraycopy(elementoBytes, 0, fixedElemento, 0, Math.min(elementoBytes.length, MAX_STRING_LENGTH));
            dos.write(fixedElemento);
            dos.writeLong(offsets[i]);
        }
        
        dos.writeLong(proximo);
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        quantidade = dis.readShort();
        
        for (int i = 0; i < quantMax; i++) {
            byte[] elementoBytes = new byte[MAX_STRING_LENGTH];
            dis.readFully(elementoBytes);
            elementos[i] = new String(elementoBytes, "UTF-8").trim();
            offsets[i] = dis.readLong();
        }
        
        proximo = dis.readLong();
    }

    public boolean create(String elemento, long offset) {
        if (full()) return false;
    
        // Encontra a posição correta (ordem crescente)
        int pos = 0;
        while (pos < quantidade && elemento.compareTo(elementos[pos]) > 0) {
            pos++;
        }
    
        // Desloca elementos para a direita
        if (quantidade - pos >= 0) {
            System.arraycopy(elementos, pos, elementos, pos + 1, quantidade - pos);
            System.arraycopy(offsets, pos, offsets, pos + 1, quantidade - pos);
        }
    
        // Insere na posição correta
        elementos[pos] = elemento;
        offsets[pos] = offset;
        quantidade++;
        
        return true;
    }
    
    public Long read(String elemento) {
        for (int i = 0; i < quantidade; i++) {
            if (elementos[i].equals(elemento)) return offsets[i];
        }
        return null;
    }

    public boolean delete(String elemento) {
        if (empty()) return false;

        int i = 0;
        while (i < quantidade && !elemento.equals(elementos[i])) i++;

        if (i < quantidade) {
            System.arraycopy(elementos, i + 1, elementos, i, quantidade - i - 1);
            System.arraycopy(offsets, i + 1, offsets, i, quantidade - i - 1);
            quantidade--;
            elementos[quantidade] = null;
            offsets[quantidade] = -1;
            return true;
        }
        return false;
    }

    public boolean delete2(String elemento, long targetOffset) {
        if (empty()) return false;
    
        for (int i = 0; i < quantidade; i++) {
            if (elementos[i].equals(elemento) && offsets[i] == targetOffset) {
                // Desloca elementos para a esquerda
                System.arraycopy(elementos, i + 1, elementos, i, quantidade - i - 1);
                System.arraycopy(offsets, i + 1, offsets, i, quantidade - i - 1);
                quantidade--;
                elementos[quantidade] = null;
                offsets[quantidade] = -1;
                return true;
            }
        }
        return false;
    }

    public String last() {
        return elementos[quantidade - 1];
    }

    public String[] list() {
        String[] lista = new String[quantidade];

        for (int i = 0; i < quantidade; i++) {
            lista[i] = elementos[i];
        }

        return lista;
    }

    public boolean empty() {
        return quantidade == 0;
    }

    public boolean full() {
        return quantidade == quantMax;
    }

    public String toString() {
        String s = "\nQuantidade: " + quantidade + "\n| ";
        int i = 0;

        while (i < quantidade) {
            s += elementos[i] + " | ";
            i++;
        }
        while (i < quantMax) {
            s += "- | ";
            i++;
        }
        return s;
    }

    public long next() {
        return proximo;
    }

    public void setNext(long p) {
        proximo = p;
    }

    public int size() {
        return bytesPorBloco;
    }

    public String[] listIds() {
        String[] lista = new String[quantidade];

        for (int i = 0; i < quantidade; i++) {
            lista[i] = elementos[i];
        }
        
        return lista;
    }
    
}