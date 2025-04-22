import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Pagina {
    protected int ordem;
    protected int maxElementos;
    protected int maxFilhos;
    protected int TAMANHO_ELEMENTO;
    protected int TAMANHO_PAGINA;

    protected ArrayList<Integer> elementos; // IDs dos registros
    protected ArrayList<Long> offsets; // Offsets no arquivo DB (apenas folhas)
    protected ArrayList<Integer> filhos; // Ponteiros para filhos
    protected int proxima; // Próxima folha

    public Pagina(int ordem) throws Exception {
        this.ordem = ordem;
        this.maxFilhos = ordem;
        this.maxElementos = ordem - 1;
        this.elementos = new ArrayList<>(this.maxElementos);
        this.offsets = new ArrayList<>(this.maxElementos); // Nova lista para offsets
        this.filhos = new ArrayList<>(this.maxFilhos);
        this.proxima = -1;

        // Cálculo do tamanho fixo:
        // - Cada elemento: 4 bytes (filho) + 4 bytes (ID) + 8 bytes (offset) = 16 bytes
        // - Cabeçalho: 4 bytes (quantidade de elementos)
        // - Último filho: 4 bytes
        // - Próxima folha: 4 bytes
        this.TAMANHO_ELEMENTO = 16;
        this.TAMANHO_PAGINA = 4 + (maxElementos * TAMANHO_ELEMENTO) + 4 + 4;
    }

    protected byte[] toByteArray() throws IOException {
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(ba);

        // Quantidade de elementos
        out.writeInt(this.elementos.size());

        boolean isFolha = this.filhos.get(0) == -1;

        for (int i = 0; i < this.elementos.size(); i++) {
            out.writeInt(this.filhos.get(i));
            out.writeInt(this.elementos.get(i));
            
            // Só escreve offset se for folha
            if (isFolha && i < this.offsets.size()) {
                out.writeLong(this.offsets.get(i));
            } else {
                out.writeLong(-1); // Placeholder para nós internos
            }
        }

        // Último filho
        if (this.filhos.size() > this.elementos.size()) {
            out.writeInt(this.filhos.get(this.elementos.size()));
        } else {
            out.writeInt(-1);
        }

        // Preenche o restante da página com registros vazios
        for (int i = this.elementos.size(); i < this.maxElementos; i++) {
            out.writeInt(-1); // Filho vazio
            out.writeInt(-1); // ID vazio
            out.writeLong(-1); // Offset vazio
        }

        // Próxima folha
        out.writeInt(this.proxima);

        return ba.toByteArray();
    }

    public void fromByteArray(byte[] data) throws IOException {
        ByteArrayInputStream ba = new ByteArrayInputStream(data);
        DataInputStream in = new DataInputStream(ba);
    
        int n = in.readInt();
        this.elementos.clear();
        this.offsets.clear();
        this.filhos.clear();
    
        boolean isFolha = false;
    
        // Lê elementos e filhos
        for (int i = 0; i < n; i++) {
            int filho = in.readInt();
            this.filhos.add(filho);
            
            int elemento = in.readInt();
            this.elementos.add(elemento);
            
            long offset = in.readLong();
            if (filho == -1) { // Folha
                isFolha = true;
                this.offsets.add(offset);
            }
        }
    
        // Lê o ÚLTIMO FILHO
        int ultimoFilho = in.readInt();
        this.filhos.add(ultimoFilho);
    
        // Determina se é folha (todos os filhos são -1)
        isFolha = isFolha && (ultimoFilho == -1);
    
        // Lê os REGISTROS VAZIOS
        int remaining = this.maxElementos - n;
        for (int i = 0; i < remaining; i++) {
            in.readInt(); // filho vazio
            in.readInt(); // id vazio
            in.readLong(); // offset vazio
        }
    
        // Lê PRÓXIMA FOLHA
        this.proxima = in.readInt();
    
        in.close();
        ba.close();
    }
}