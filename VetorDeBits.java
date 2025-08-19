import java.util.BitSet;

public class VetorDeBits {
    private BitSet vetor;
    private int numBits = -1; // -1 indica usar vetor.length()

    public VetorDeBits() {
        vetor = new BitSet();
    }

    public VetorDeBits(int n) {
        vetor = new BitSet(n);
        numBits = n;
    }

    public VetorDeBits(byte[] v) {
        vetor = BitSet.valueOf(v);
    }

    // Novo construtor para definir o número de bits válidos
    public VetorDeBits(byte[] v, int numBits) {
        vetor = BitSet.valueOf(v);
        this.numBits = numBits;
    }

    public byte[] toByteArray() {
        return vetor.toByteArray();
    }

    public void set(int i) {
        vetor.set(i);
    }

    public void clear(int i) {
        vetor.clear(i);
    }

    public boolean get(int i) {
        return vetor.get(i);
    }

    public int length() {
        return numBits >= 0 ? numBits : vetor.length();
    }

    public int size() {
        return vetor.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int len = length();
        for (int i = 0; i < len; i++)
            sb.append(vetor.get(i) ? '1' : '0');
        return sb.toString();
    }
}