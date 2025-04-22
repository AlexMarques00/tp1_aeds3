import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;

public class Bucket {
    int bytesPorElemento;
    int bytesPorCesto;

    int quantidadeMaxima; // quantidade máxima de elementos que o cesto pode conter
    byte profundidadeLocal; // profundidade local do cesto
    short quantidade; // quantidade de elementos presentes no cesto
    ArrayList<Integer> elementos; // sequência de elementos armazenados
    protected ArrayList<Long> offsets; // Offsets no arquivo DB

    public Bucket(int qtdmax) throws Exception {
        this(qtdmax, 0);
    }

    public Bucket(int qtdMax, int pl) throws Exception {
        if (qtdMax > 32767)
            throw new Exception("*QUANTIDADE MÁXIMA DE 32.767 ELEMENTOS*");

        if (pl > 127)
            throw new Exception("*PROFUNDIDADE LOCAL MÁXIMA DE 127 BITS*");

        this.profundidadeLocal = (byte) pl;
        this.quantidade = 0;
        this.quantidadeMaxima = (short) qtdMax;
        this.elementos = new ArrayList<>(this.quantidadeMaxima);
        this.offsets = new ArrayList<>(this.quantidadeMaxima);
        this.bytesPorElemento = 12; // 4 bytes (int) + 8 bytes (long)
        this.bytesPorCesto = (quantidadeMaxima * bytesPorElemento) + 3; // 1 byte + 2 bytes
    }

    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeByte(profundidadeLocal);
        dos.writeShort(quantidade);

        // Escreve elementos
        for (int i = 0; i < quantidadeMaxima; i++) {
            if (i < quantidade) {
                dos.writeInt(elementos.get(i));
                dos.writeLong(offsets.get(i));
            } else {
                // Preenche com valores nulos
                dos.writeInt(-1);
                dos.writeLong(-1L);
            }
        }

        return baos.toByteArray();
    }

    public void fromByteArray(byte[] ba) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        // Limpa listas
        elementos.clear();
        offsets.clear();

        profundidadeLocal = dis.readByte();
        quantidade = dis.readShort();

        // Lê elementos
        for (int i = 0; i < quantidadeMaxima; i++) {
            int elemento = dis.readInt();
            long offset = dis.readLong();

            if (i < quantidade) { // Ignora padding
                elementos.add(elemento);
                offsets.add(offset);
            }
        }
    }

    // Inserir elementos no cesto
    public boolean create(int id, long offset) {
        if (full())
            return false;
        int i = quantidade - 1; // posição do último elemento no cesto
        while (i >= 0 && id < elementos.get(i))
            i--;
        elementos.add(i + 1, id);
        offsets.add(i + 1, offset);
        quantidade++;
        return true;
    }

    // Buscar retornando o offset correspondente
    public long read(int id) {
        if (empty())
            return -1;

        int esq = 0;
        int dir = quantidade - 1;

        while (esq <= dir) {
            int meio = (esq + dir) / 2;
            int atual = elementos.get(meio);

            if (atual == id) {
                return offsets.get(meio); // Retorna o offset
            } else if (atual < id) {
                esq = meio + 1;
            } else {
                dir = meio - 1;
            }
        }
        return -1; // Não encontrado
    }

    // atualizar um elemento do cesto
    public boolean update(int id, long offset) {
        if (empty())
            return false;

        int esq = 0;
        int dir = quantidade - 1;

        while (esq <= dir) {
            int meio = (esq + dir) / 2;
            int atual = elementos.get(meio);

            if (atual == id) {
                offsets.set(meio, offset); // Atualiza o offset
                return true;
            } else if (atual < id) {
                esq = meio + 1;
            } else {
                dir = meio - 1;
            }
        }
        return false;
    }

    // pagar um elemento do cesto
    public boolean delete(int id) {
        if (empty())
            return false;

        int esq = 0;
        int dir = quantidade - 1;

        while (esq <= dir) {
            int meio = (esq + dir) / 2;
            int atual = elementos.get(meio);

            if (atual == id) {
                elementos.remove(meio);
                offsets.remove(meio); // Remove o offset correspondente
                quantidade--;
                return true;
            } else if (atual < id) {
                esq = meio + 1;
            } else {
                dir = meio - 1;
            }
        }
        return false;
    }

    public boolean empty() {
        return quantidade == 0;
    }

    public boolean full() {
        return quantidade == quantidadeMaxima;
    }

    public int size() {
        return bytesPorCesto;
    }

    public String toString() {
        String s = "Profundidade Local: " + profundidadeLocal + "\nQuantidade: " + quantidade + "\n| ";
        int i = 0;
        while (i < quantidade) {
            s += elementos.get(i).toString() + " | ";
            i++;
        }
        while (i < quantidadeMaxima) {
            s += "- | ";
            i++;
        }
        return s;
    }
}
