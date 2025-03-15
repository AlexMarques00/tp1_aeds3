import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

public class ReadCSV {
    public static void lerCSV() throws Exception {
        RandomAccessFile csv = new RandomAccessFile("BaseAnimes.csv", "rw");
        csv.readLine();

        FileOutputStream arq = new FileOutputStream("animeDataBase.db");
        DataOutputStream dos = new DataOutputStream(arq);
        dos.writeInt(18495);

        String input = csv.readLine();
        while (input != null) {
            System.out.println(input);
            Animes animeTmp = new Animes(input);
            addToDatabase(dos, animeTmp);
            input = csv.readLine();
        }

        System.out.println();
        System.out.println("* CSV LIDO E DB CRIADO COM SUCESSO!");
        System.out.println("* NOME DO ARQUIVO DB INICIAL: animeDataBase.db");
        System.out.println();
        
        dos.close();
        csv.close();
    }

    public static void addToDatabase(DataOutputStream dos, Animes anime) throws Exception {
        // LAPIDE + TAM + OBJETO
        byte[] objeto = anime.toByteArray();

        dos.writeChar(' ');
        dos.writeShort(objeto.length); // Tamanho do registro em bytes
        dos.write(objeto);
    }
}