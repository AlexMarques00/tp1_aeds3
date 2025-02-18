import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class ReadCSV extends Animes{
    public static void main(String args[]) throws Exception{

        RandomAccessFile csv = new RandomAccessFile("BaseAnimesCsv.csv", "rw");
        csv.readLine();
        String input = csv.readLine();
        for(int i = 0; input != null; i++){
            System.out.println(input);
            Animes animeTmp = new Animes(input);
            addToDatabase(animeTmp);
            input = csv.readLine();
        }

        csv.close();
    }
    public static void addToDatabase(Animes anime) throws Exception{
        FileOutputStream arq = new FileOutputStream("animeDataBase.db");
        DataOutputStream dos = new DataOutputStream(arq);
        
        // ID,Name,Type,Episodes,Rating,Realease_year,Genres,Release_season,Studio

        // int id;
        // String name;
        // String type;
        // int episodes;
        // float rating;
        // int year;
        // ArrayList<String> genres;
        // String season;
        // String studio;

    }
}
