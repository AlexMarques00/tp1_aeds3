import java.io.DataOutputStream;
import java.io.RandomAccessFile;

public class ReadCSV {
    public static void lerCSV() throws Exception {
        RandomAccessFile csv = new RandomAccessFile("BaseAnimes.csv", "rw");
        csv.readLine();

        RandomAccessFile dos = new RandomAccessFile("animeDataBase.db", "rw");
        dos.writeInt(18495);

        //FileOutputStream arq1 = new FileOutputStream("HashExtensivo.db");
        //DataOutputStream dos1 = new DataOutputStream(arq1);

        //FileOutputStream arq3 = new FileOutputStream("ListaInvertida.db");
        //DataOutputStream dos3 = new DataOutputStream(arq3);

        // Cria a árvore B com ordem, por exemplo, 10
        ArvoreBMais arvoreB = new ArvoreBMais("ArvoreB.db", 10);
       

        String input = csv.readLine();
        while (input != null) {
            System.out.println(input);
            Animes animeTmp = new Animes(input);

            long offset = addToDatabase(dos, animeTmp);
            //addToHash(dos1, animeTmp);
            //addToLista(dos3, animeTmp);
            addToArvore(arvoreB, animeTmp, offset);

            input = csv.readLine();
        }

        arvoreB.print();
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

    public static void addToHash(DataOutputStream dos, Animes anime) throws Exception {
    }

    public static void addToLista(DataOutputStream dos, Animes anime) throws Exception {
    }

    public static void addToArvore(ArvoreBMais arvoreB, Animes anime, long offset) throws Exception {
        arvoreB.create(anime, offset);     
    }
}