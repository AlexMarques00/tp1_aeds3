import java.util.ArrayList;

public class Animes{
    // Atributos
    // ID,Name,Type,Episodes,Rating,Realease_year,Genres,Release_season,Studio
    private int id;
    private int episodes;
    private int year;
    private float rating;
    private String name;
    private String type;
    private String season;
    private String studio;
    private ArrayList<String> genres;

    //Costrutor 01
    public Animes (int id, int episodes, int year, float rating, String name, String type, String season, String studio, ArrayList<String> genres) {
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
        this.year = 0;
        this.rating = 0.0f;
        this.name = "Sem nome";
        this.type = "Vazio";
        this.season = "Vazio";
        this.studio = "Vazio";
        this.genres = new ArrayList<>();
    }

    public void write() {
        System.out.println("[#" + id + " -> " + name + ": " + type + " - " + episodes + " - " + rating + " - " + year + " - \"" + genres + "\" - " + season  + " - " + studio + " - ]");
    }

    // Construtor 03
    public Animes(String input) {
        // Verifica se a linha e vazia ou nula
        if (input == null || input.isEmpty()) {
            return;
        }

        // Inicializa a lista de generos
        this.genres = new ArrayList<>();

        // Divide a linha em colunas, considerando virgulas dentro de aspas
        String[] buffer = input.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

        // Atribui os valores as variaveis
        this.id = Integer.parseInt(buffer[0].trim());
        this.name = buffer[1].trim();
        this.type = buffer[2];
        this.episodes = Integer.parseInt(buffer[3].trim());
        this.rating = Float.parseFloat(buffer[4].trim());

        // Verifica se o ano esta presente
        if (!buffer[5].trim().isEmpty()) {
            this.year = Integer.parseInt(buffer[5].trim());
        } else {
            this.year = 0; // Valor padrao
        }

        // Processa os generos (se existirem)
        if (buffer.length > 6 && !buffer[6].trim().isEmpty()) {
            String genres = buffer[6].trim();

            // Remove aspas se existirem (if recomendado por deepseek)
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
        if (buffer.length > 8 && !buffer[8].trim().isEmpty()) {
            this.season = buffer[8].trim();
        } else {
            this.season = "Vazio"; // Valor padrao
        }

        if (buffer.length > 9 && !buffer[9].trim().isEmpty()) {
            this.studio = buffer[9].trim();
        } else {
            this.studio = "Vazio"; // Valor padrao
        }
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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
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