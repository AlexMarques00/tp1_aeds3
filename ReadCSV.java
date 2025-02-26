import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

public class ReadCSV extends Animes{
    public static void main(String args[]) throws Exception{
        RandomAccessFile csv = new RandomAccessFile("BaseAnimes.csv", "rw");
        csv.readLine();

        FileOutputStream arq = new FileOutputStream("animeDataBase.db");
        DataOutputStream dos = new DataOutputStream(arq);
        dos.writeInt(18495);

        String input = csv.readLine();
        while(input != null){
            System.out.println(input);
            Animes animeTmp = new Animes(input);
            addToDatabase(dos, animeTmp);
            input = csv.readLine();
        }

        dos.close();
        csv.close();
    }
    public static void addToDatabase(DataOutputStream dos, Animes anime) throws Exception{
        // tam + lapide + objeto
        byte[] objeto = anime.toByteArray();
        
        dos.writeChar(' ');
        dos.writeShort(objeto.length); //Tamano do registro em bytes
        dos.write(objeto);
    }
}
