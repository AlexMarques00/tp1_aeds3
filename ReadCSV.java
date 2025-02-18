import java.io.RandomAccessFile;

public class ReadCSV extends Animes{
    public static void main(String args[]) throws Exception{
        RandomAccessFile arq = new RandomAccessFile("animeDataBase.db", "rw");
        // int ultima id utilizada no inicio
        arq.writeInt(18495);
        arq.close();
        RandomAccessFile csv = new RandomAccessFile("BaseAnimesCsv.csv", "rw");
        csv.readLine();
        String input = csv.readLine();
        while(input != null){
            System.out.println(input);
            Animes animeTmp = new Animes(input);
            addToDatabase(animeTmp);
            input = csv.readLine();
        }

        csv.close();
    }
    public static void addToDatabase(Animes anime) throws Exception{
        RandomAccessFile arq = new RandomAccessFile("animeDataBase.db", "rw");
        arq.writeInt(anime.getId());





        // para cada objeto:
        //     short tamanho do objeto
        //     byte de lápide ('*' para morto e ' ' para vivo)
        //     int id
        //     short tamanho do nome
        //     string nome
        //     string type (tam = 5 bytes)
        //     short episodes
        //     float rating
        //     short year
        //     short numero de genres 
        //     {
        //         short tamanho do genre
        //         string genre
        //     }
        //     string season (tam = 5 bytes) (if season == "" -> write "     ")
        //     short tamanho do studio
        //     string studio
        arq.close();
    }
}
