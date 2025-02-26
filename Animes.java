import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Animes{
    // Atributos
    // ID,Name,Type,Episodes,Rating,Realease_year,Genres,Release_season,Studio
    private int id;
    private int episodes;
    private MyDate year;
    private float rating;
    private String name;
    private String type;
    private String season;
    private String studio;
    private ArrayList<String> genres;

    //Costrutor 01
    public Animes (int id, String name, String type, int episodes, float rating, MyDate year, ArrayList<String> genres, String season, String studio) {
        this.id = id;
        this.episodes = episodes;
        this.year = year;
        this.rating = rating;
        this.name = name;
        this.type = type;
        this.season = season;
        this.studio = studio;
        this.genres = genres;
    }

    //Construtor 02
    public Animes() {
        this.id = 0;
        this.episodes = 0;
        this.year = new MyDate();
        this.rating = 0.0f;
        this.name = "Sem nome";
        this.type = "Vazio";
        this.season = "Vazio";
        this.studio = "Vazio";
        this.genres = new ArrayList<>();
    }

    public void write() {
        if(this.episodes == 0) {
            System.out.println(
                "[#" + id + " -> " + name + ": " + 
                type + " - (Não Finalizado) - " + 
                "Avaliação: " + rating + " - " + 
                "Lançamento: " + year.toString() + " - " + 
                "Gêneros: \"" + String.join(", ", genres) + "\" - " + 
                "Temporada: " + season + " - " + 
                "Estúdio: " + studio + "]"
            );
        } else {
            System.out.println(
                "[#" + id + " -> " + name + ": " + 
                type + " - " + episodes + " episódios - " + 
                "Avaliação: " + rating + " - " + 
                "Lançamento: " + year.toString() + " - " + 
                "Gêneros: \"" + String.join(", ", genres) + "\" - " + 
                "Temporada: " + season + " - " + 
                "Estúdio: " + studio + "]"
            );
        }
    }

    // Construtor 03
    public Animes(String input) {
        // Verifica se a linha e vazia ou nula
        if (input == null || input.isEmpty()) {
            return;
        }

        // Inicializa a lista de generos
        this.genres = new ArrayList<>();

        // Divide a linha em colunas, considerando virgulas dentro de aspas (split sugerido por DeepSeek)
        String[] buffer = input.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

        // Atribui os valores as variaveis
        this.id = Integer.parseInt(buffer[0].trim());
        this.name = buffer[1].trim();
        this.type = buffer[2];
        this.episodes = Integer.parseInt(buffer[3].trim());
        this.rating = Float.parseFloat(buffer[4].trim());

        // Lê a data de lançamento
        String[] data = buffer[5].trim().split("/");
        if (data.length == 3) {
            int dia = Integer.parseInt(data[0]);
            int mes = Integer.parseInt(data[1]);
            int ano = Integer.parseInt(data[2]);
            this.year = new MyDate(dia, mes, ano);
        }

        // Processa os generos (se existirem)
        if (buffer.length > 6 && !buffer[6].trim().isEmpty()) {
            String genres = buffer[6].trim();

            // Remove aspas se existirem (if recomendado por DeepSeek)
            if (genres.startsWith("\"") && genres.endsWith("\"")) {
                genres = genres.substring(1, genres.length() - 1);
            }
            
            // Divide os generos por virgula (caso haja mais de um)
            String[] genresArray = genres.split(",");
            for (String genre : genresArray) {
                if (!genre.trim().isEmpty()) { // Adiciona apenas generos nao vazios
                    this.genres.add(genre.trim());
                }
            }
        } else {
            // Se nao houver generos, a lista de generos permanece vazia
        }

        // Atribui a temporada e o estudio, se existirem
        if (buffer.length > 7 && !buffer[7].trim().isEmpty()) {
            this.season = buffer[7].trim();
        } else {
            this.season = "Vazio"; // Valor padrao
        }

        if (buffer.length > 8 && !buffer[8].trim().isEmpty()) {
            this.studio = buffer[8].trim();
        } else {
            this.studio = "Vazio"; // Valor padrao
        }
    }

    public byte[] toByteArray() throws Exception{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(this.id);
        dos.writeUTF(this.name);
        dos.writeBytes(this.type);
        dos.writeShort(this.episodes);
        dos.writeFloat(this.rating);

        // Escreve os campos do objeto MyDate
        dos.writeInt(this.year.getDia());  // Dia
        dos.writeInt(this.year.getMes());  // Mês
        dos.writeInt(this.year.getAno()); // Ano

        dos.writeUTF(genres.toString()); 
        dos.writeUTF(this.season);
        dos.writeUTF(this.studio);

        return baos.toByteArray();
    }

    public void fromByteArray(byte ba[]) throws IOException{

        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        this.id = dis.readInt();
        this.name = dis.readUTF();

        byte[] typeBytes = new byte[5]; 
        dis.readFully(typeBytes); 
        this.type = new String(typeBytes).trim();

        this.episodes = dis.readShort();
        this.rating = dis.readFloat();

        int dia = dis.readInt();  
        int mes = dis.readInt();  
        int ano = dis.readInt();  
        this.year = new MyDate(dia, mes, ano);  // Reconstroi o objeto MyDate

        this.genres = new ArrayList<>(Arrays.asList(dis.readUTF().split(", ")));

        this.season = dis.readUTF();
        this.studio = dis.readUTF();

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEpisodes() {
        return episodes;
    }

    public void setEpisodes(int episodes) {
        this.episodes = episodes;
    }

    public MyDate getYear() {
        return year;
    }

    public void setYear(MyDate year) {
        this.year = year;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getStudio() {
        return studio;
    }

    public void setStudio(String studio) {
        this.studio = studio;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }

    
}