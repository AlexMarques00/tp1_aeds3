import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Diretorio {
    byte profundidadeGlobal;
    long[] enderecos;

    public Diretorio() {
        profundidadeGlobal = 0;
        enderecos = new long[1];
        enderecos[0] = -1;
    }

    public boolean atualizaEndereco(int indice, long e) {
        if (indice > Math.pow(2, profundidadeGlobal))
            return false;

        enderecos[indice] = e;
        return true;
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeByte(profundidadeGlobal);
        int quantidade = (int) Math.pow(2, profundidadeGlobal);

        int i = 0;
        while (i < quantidade) {
            dos.writeLong(enderecos[i]);
            i++;
        }

        return baos.toByteArray();
    }

    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        profundidadeGlobal = dis.readByte();
        int quantidade = (int) Math.pow(2, profundidadeGlobal);
        enderecos = new long[quantidade];

        int i = 0;
        while (i < quantidade) {
            enderecos[i] = dis.readLong();
            i++;
        }
    }

    protected boolean duplica() {
        if (profundidadeGlobal == 127)
            return false;

        profundidadeGlobal++;

        int q1 = (int) Math.pow(2, profundidadeGlobal - 1);
        int q2 = (int) Math.pow(2, profundidadeGlobal);
        long[] novosEnderecos = new long[q2];

        int i = 0;
        while (i < q1) { // copia o vetor anterior para a primeiro metade do novo vetor
            novosEnderecos[i] = enderecos[i];
            i++;
        }

        while (i < q2) { // copia o vetor anterior para a segunda metade do novo vetor
            novosEnderecos[i] = enderecos[i - q1];
            i++;
        }

        enderecos = novosEnderecos;
        return true;
    }

    // Para efeito de determinar o cesto em que o elemento deve ser inserido,
    // só serão considerados valores absolutos da chave.
    protected int hash(int chave) {
        int hash = chave;
        hash = (hash ^ (hash >>> 16)) & 0x7fffffff; // Garante positivo
        return hash % (int) Math.pow(2, profundidadeGlobal);
    }

    // Método auxiliar para atualizar endereço ao duplicar o diretório
    protected int hash2(int chave, int pl) { // cálculo do hash para uma dada profundidade local
        return Math.abs(chave) % (int) Math.pow(2, pl);
    }

    public String toString() {
        String s = "\nProfundidade global: " + profundidadeGlobal;

        int i = 0;
        int quantidade = (int) Math.pow(2, profundidadeGlobal);
        while (i < quantidade) {
            s += "\n" + i + ": " + enderecos[i];
            i++;
        }

        return s;
    }

    protected long endereço(int p) {
        if (p < 0 || p >= enderecos.length) {
            return -1;
        }

        return enderecos[p];
    }

    public byte getProfundidadeGlobal() {
        return profundidadeGlobal;
    }

    public int getTamanho() {
        return enderecos.length;
    }
}