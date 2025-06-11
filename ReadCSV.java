import java.io.RandomAccessFile;
import java.util.Scanner;

public class ReadCSV {
    public static Scanner sc = new Scanner(System.in);
    public static int ordem = 0;
    public static int elementosCesto = 0;
    public static int elementosBloco = 0;

    public static void lerCSV() throws Exception {
        RandomAccessFile csv = null;
        RandomAccessFile dos = null;
        HashExtensivo hash = null;
        ArvoreBMais arvoreB = null;
        ListaInvertida lista = null;

        try {
            csv = new RandomAccessFile("BaseAnimes.csv", "rw");
            csv.readLine();

            dos = new RandomAccessFile("animeDataBase.db", "rw");
            dos.writeInt(18495);

            System.out.print("* DIGITE A ORDEM DA ÂRVORE B+: ");
            ordem = sc.nextInt();
            sc.nextLine();
            // Cria a árvore B com ordem, por exemplo, 10
            arvoreB = new ArvoreBMais("ArvoreB+.db", ordem);

            System.out.print("* DIGITE A QUANTIDADE DE ELEMENTOS POR CESTO DO HASH EXTENSIVEL: ");
            elementosCesto = sc.nextInt();
            sc.nextLine();
            // Cria o Hash
            hash = new HashExtensivo(elementosCesto);

            System.out.print("* DIGITE A QUANTIDADE DE ELEMENTOS POR BLOCO DA LISTA INVERTIDA: ");
            elementosBloco = sc.nextInt();
            sc.nextLine();
            // Cria a Lista
            lista = new ListaInvertida(elementosBloco);

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

            // arvoreB.print();
            // hash.print();
            // lista.print();

            System.out.println();
            System.out.println("* CSV LIDO, DB E ARQUIVOS DE INDICE CRIADOS COM SUCESSO!");
            System.out.println("* NOME DO ARQUIVO DB INICIAL: animeDataBase.db");
            System.out.println();

        } finally {
            if (csv != null)
                csv.close();
            if (dos != null)
                dos.close();
            if (hash != null)
                hash.close();
            if (arvoreB != null)
                arvoreB.close();
            if (lista != null)
                lista.close();
        }
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

    public static int getOrdem() {
        return ordem;
    }

    public static int getElementosCesto() {
        return elementosCesto;
    }

    public static int getElementosBloco() {
        return elementosBloco;
    }
}