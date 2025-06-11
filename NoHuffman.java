public class NoHuffman implements Comparable<NoHuffman> {
    byte b;
    int frequencia;
    NoHuffman esquerdo, direito;

    public NoHuffman(byte b, int f) {
        this.b = b;
        this.frequencia = f;
        esquerdo = direito = null;
    }

    public int compareTo(NoHuffman o) {
        return this.frequencia - o.frequencia;
    }
}
