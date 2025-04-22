import java.io.RandomAccessFile;

public class ListaInvertida {
    public RandomAccessFile arq;

    public ListaInvertida(String path) throws Exception {
        arq = new RandomAccessFile(path, "rw");
    }

    public void create(Animes anime, long offset) {

    }

    public void read(int id) {

    }

    public void delete(int id, long offset) {

    }

    public void close() throws Exception {
        arq.close();
    }
}
