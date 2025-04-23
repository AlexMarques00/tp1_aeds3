import java.io.*;
import java.util.Arrays;

public class Bloco {
    short quantidade;
    short quantMax;
    int[] elementos;
    long[] offsets;
    long proximo;
    short bytesPorBloco;

    public Bloco(int qtdmax) {
        this.quantidade = 0;
        this.quantMax = (short) qtdmax;
        this.elementos = new int[quantMax];
        this.offsets = new long[quantMax]; // Array primitivo
        Arrays.fill(offsets, -1L); // Inicializa com -1
        this.proximo = -1;
        this.bytesPorBloco = (short) (2 + (quantMax * (4 + 8)) + 8);
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeShort(quantidade);
        
        for (int i = 0; i < quantMax; i++) {
            dos.writeInt(elementos[i]);
            dos.writeLong(offsets[i]); // Escreve todo o array
        }
        
        dos.writeLong(proximo);
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        quantidade = dis.readShort();
        
        for (int i = 0; i < quantMax; i++) {
            elementos[i] = dis.readInt();
            offsets[i] = dis.readLong(); // Lê todo o array
        }
        
        proximo = dis.readLong();
    }

    public boolean create(int id, long offset) {
        if (full()) return false;

        int pos = 0;
        while (pos < quantidade && id > elementos[pos]) pos++;

        // Desloca elementos e offsets juntos
        if (pos < quantidade) {
            System.arraycopy(elementos, pos, elementos, pos + 1, quantidade - pos);
            System.arraycopy(offsets, pos, offsets, pos + 1, quantidade - pos);
        }

        elementos[pos] = id;
        offsets[pos] = offset;
        quantidade++;
        return true;
    }

    public Long read(int id) {
        for (int i = 0; i < quantidade; i++) {
            if (elementos[i] == id) return offsets[i];
        }
        return null;
    }

    public boolean delete(int id) {
        if (empty())
            return false;

        int i = 0;
        while (i < quantidade && id > elementos[i])
            i++;

        if (i < quantidade && id == elementos[i]) {
            while (i < quantidade - 1) {
                elementos[i] = elementos[i + 1];
                offsets[i] = offsets[i + 1];
                i++;
            }
            quantidade--;
            return true;
        } else
            return false;
    }

    public int last() {
        return elementos[quantidade - 1];
    }

    public int[] list() {
        int[] lista = new int[quantidade];

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

    public int[] listIds() {
        int[] lista = new int[quantidade];

        for (int i = 0; i < quantidade; i++) {
            lista[i] = elementos[i];
        }
        
        return lista;
    }
}