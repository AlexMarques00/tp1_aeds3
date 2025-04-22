import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.RandomAccessFile;


public class HashExtensivo {
    public RandomAccessFile arq;
    
    public HashExtensivo(String path) throws Exception {
        arq = new RandomAccessFile(path, "rw");
    }

    public static byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        return baos.toByteArray();
    }

    public void fromByteArray(byte[] ba) throws Exception {

    }

    public void create (Animes anime) {
    
    }

    public void read (int id) {
    
    }

    public void update (Animes anime) {
    
    }

    public void delete (int id) {
    
    }
}
