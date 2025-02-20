import java.io.RandomAccessFile;
import java.io.DataOutputStream;
import java.io.FileOutputStream;

public class ReadCSV extends Animes{
    public static void main(String args[]) throws Exception{
        RandomAccessFile csv = new RandomAccessFile("BaseAnimesCsv.csv", "rw");
        csv.readLine();

        FileOutputStream arq1 = new FileOutputStream("animeDataBase.db");
        DataOutputStream dos = new DataOutputStream(arq1);
        dos.writeInt(18495);

        String input = csv.readLine();
        while(input != null){
            // System.out.println(input);
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
        
        dos.writeShort(objeto.length); //Tamanho do registro em bytes
        dos.write(' ');
        dos.write(objeto);
    }
}
