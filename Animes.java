import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Animes {
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
                type.trim() + " - (Não Finalizado) - " + 
                "Avaliação: " + rating + " - " + 
                "Lançamento: " + year.toString() + " - " + 
                "Gêneros: \"" + String.join(", ", genres) + "\" - " + 
                "Temporada: " + season + " - " + 
                "Estúdio: " + studio + "]"
            );
        } else {
            System.out.println(
                "[#" + id + " -> " + name + ": " + 
                type.trim() + " - " + episodes + " episódios - " + 
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
            String genresString = buffer[6].trim();

            // Remove aspas se existirem (if recomendado por DeepSeek)
            if (genresString.startsWith("\"") && genresString.endsWith("\"")) {
                genresString = genresString.substring(1, genresString.length() - 1);
            }
            
            // Divide os generos por virgula (caso haja mais de um)
            String[] genresArray = genresString.split(",");
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

        dos.writeInt(this.genres.size());
        for (String genre : this.genres) {
            dos.writeUTF(genre);
        }

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
        this.type = new String(typeBytes);

        this.episodes = dis.readShort();
        this.rating = dis.readFloat();

        int dia = dis.readInt();  
        int mes = dis.readInt();  
        int ano = dis.readInt();  
        this.year = new MyDate(dia, mes, ano);  // Reconstroi o objeto MyDate

        // Lê o número de gêneros
        int numGenres = dis.readInt();
        this.genres = new ArrayList<>();
        for (int i = 0; i < numGenres; i++) {
            this.genres.add(dis.readUTF());
        }

        this.season = dis.readUTF();
        this.studio = dis.readUTF();
    }

    @Override
    public Animes clone() throws CloneNotSupportedException {
        Animes copia = new Animes();

        copia.id = this.id;
        copia.episodes = this.episodes;
        copia.rating = this.rating;
        copia.name = this.name;
        copia.type = this.type;
        copia.season = this.season;
        copia.studio = this.studio;
        copia.year = this.year.clone(); // Clona a data

        // Clona a lista de gêneros
        copia.genres = new ArrayList<>();
        for (String genero : this.genres) {
            copia.genres.add(genero);
        }

        return copia;
    }

    public int size() {
        int size = 0;  
        size += 4; // id
        return size;
    }
       
    public int compareTo(int outro) {
        return Integer.compare(this.id, outro);
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

    // Variáveis estáticas para controlar a criptografia
    private static String chaveVigenere = null;
    private static RSA rsaInstance = null;
    private static boolean criptografiaHabilitada = false;
    private static String tipoCriptografia = null; // "VIGENERE" ou "RSA"

    //Habilita a criptografia Vigenère com a chave especificada
    public static void habilitarCriptografia(String chave) {
        chaveVigenere = chave.toUpperCase();
        rsaInstance = null;
        tipoCriptografia = "VIGENERE";
        criptografiaHabilitada = true;
    }
    
    //Habilita a criptografia RSA com chaves geradas ou fornecidas
    public static void habilitarCriptografiaRSA(RSA rsa) {
        rsaInstance = rsa;
        chaveVigenere = null;
        tipoCriptografia = "RSA";
        criptografiaHabilitada = true;
    }
    
    //Desabilita a criptografia
    public static void desabilitarCriptografia() {
        chaveVigenere = null;
        rsaInstance = null;
        tipoCriptografia = null;
        criptografiaHabilitada = false;
    }
    
    //Verifica se a criptografia está habilitada
    public static boolean isCriptografiaHabilitada() {
        return criptografiaHabilitada && 
               ((tipoCriptografia != null && tipoCriptografia.equals("VIGENERE") && chaveVigenere != null) ||
                (tipoCriptografia != null && tipoCriptografia.equals("RSA") && rsaInstance != null));
    }
    
    public static String getChaveVigenere() {
        return chaveVigenere;
    }
    
    public static RSA getRSAInstance() {
        return rsaInstance;
    }
    
    public static String getTipoCriptografia() {
        return tipoCriptografia;
    }
    
    //Método toByteArray com criptografia automática
    public byte[] toByteArrayCriptografado() throws Exception {
        byte[] dados = toByteArray();
        
        if (isCriptografiaHabilitada()) {
            if (tipoCriptografia.equals("VIGENERE")) {
                Vigenere vigenere = new Vigenere(chaveVigenere);
                return vigenere.criptografar(dados);
            } else if (tipoCriptografia.equals("RSA")) {
                return rsaInstance.criptografar(dados);
            }
        }
        
        return dados;
    }
    
    //Método fromByteArray com descriptografia automática
    public void fromByteArrayDescriptografado(byte[] ba) throws IOException {
        byte[] dados = ba;
        
        // Sempre tenta descriptografar quando chamado deste método
        // pois significa que estamos lendo de um arquivo criptografado
        if (isCriptografiaHabilitada()) {
            if (tipoCriptografia.equals("VIGENERE") && chaveVigenere != null && !chaveVigenere.isEmpty()) {
                Vigenere vigenere = new Vigenere(chaveVigenere);
                dados = vigenere.descriptografar(ba);
            } else if (tipoCriptografia.equals("RSA") && rsaInstance != null) {
                dados = rsaInstance.descriptografar(ba);
            }
        }
        
        fromByteArray(dados);
    }
}