import java.io.RandomAccessFile;

public class ReadCSV {
    public static void lerCSV() throws Exception {
        RandomAccessFile csv = new RandomAccessFile("BaseAnimes.csv", "rw");
        csv.readLine();

        RandomAccessFile dos = new RandomAccessFile("animeDataBase.db", "rw");
        dos.writeInt(18495);

        // Cria a árvore B com ordem, por exemplo, 10
        ArvoreBMais arvoreB = new ArvoreBMais("ArvoreB.db", 10);

        // Cria o Hash
        HashExtensivo hash = new HashExtensivo(10);

        // Cria a Lista
        ListaInvertida lista = new ListaInvertida("ListaInvertida.db");
       

        String input = csv.readLine();
        while (input != null) {
            System.out.println(input);
            Animes animeTmp = new Animes(input);

            long offset = addToDatabase(dos, animeTmp);
            addToHash(hash, animeTmp, offset);
            addToLista(lista, animeTmp, offset);
            addToArvore(arvoreB, animeTmp, offset);

            input = csv.readLine();
        }

        arvoreB.print();
        hash.print();
        System.out.println();
        System.out.println("* CSV LIDO, DB E ARQUIVOS DE INDICE CRIADOS COM SUCESSO!");
        System.out.println("* NOME DO ARQUIVO DB INICIAL: animeDataBase.db");
        System.out.println();

        dos.close();
        csv.close();
    }

    public static long addToDatabase(RandomAccessFile dos, Animes anime) throws Exception {
        // Obtém a posição atual (offset) antes de escrever
        long offset = dos.getFilePointer();

        // LAPIDE + TAM + OBJETO
        byte[] objeto = anime.toByteArray();

        dos.writeChar(' ');
        dos.writeShort(objeto.length); // Tamanho do registro em bytes
        dos.write(objeto);

        return offset;
    }

    public static void addToHash(HashExtensivo hash, Animes anime, long offset) throws Exception {
        hash.create(anime.getId(), offset);
    }

    public static void addToLista(ListaInvertida lista, Animes anime, long offset) throws Exception {
        lista.create(anime, offset);
    }

    public static void addToArvore(ArvoreBMais arvoreB, Animes anime, long offset) throws Exception {
        arvoreB.create(anime, offset);     
    }
}