import java.io.File;
import java.util.Scanner;

public class Compressao {

    public static void abrirCompressao () throws Exception {
        Scanner sc = new Scanner(System.in);
        boolean keepGoing = true;
        long tempoCompressaoHuff = 0;
        long tempoDescompressaoHuff = 0;
        long tempoCompressaoLZW = 0;
        long tempoDescompressaoLZW = 0;
        int opcaoFiltro;

        String arquivoOriginal = "animeDataBase.db";
        String arquivoComprimidoHuff = "animeDataBase.huff.db";
        String arquivoDescomprimidoHuff = "animeDataBase.db";
        String arquivoComprimidoLZW = "animeDataBase.lzw.db";
        String arquivoDescomprimidoLZW = "animeDataBase.db";

        while (keepGoing) {

            System.out.println();
            System.out.println("--------------------------------------------------------------");
            System.out.println("                 MENU COMPRESSÃO                ");
            System.out.println("        * DIGITE 1 PARA COMPRIMIR/DESCOMPRIMIR UTILIZANDO HUFFMAN         ");
            System.out.println("        * DIGITE 2 PARA COMPRIMIR/DESCOMPRIMIR UTILIZANDO LZW         ");
            System.out.println("        * DIGITE 3 PARA APAGAR TODOS OS ARQUIVOS CRIADOS ");
            System.out.println("        * DIGITE 0 PARA SAIR           ");
            System.out.println("--------------------------------------------------------------");
            System.out.println();

            System.out.print("* ENTRADA: ");
            int menu = Integer.parseInt(sc.nextLine());

            switch (menu) {
                case 1:
                    System.out.println();
                    System.out.println("--------------------------------------------------------------");
                    System.out.println("               MENU HUFFMAN             ");
                    System.out.println("        * DIGITE 1 PARA COMPRIMIR         ");
                    System.out.println("        * DIGITE 2 PARA DESCOMPRIMIR         ");
                    System.out.println("        * DIGITE 3 MOSTRAR TEMPO DE EXECUÇÃO + TAXA DE COMPRESSÃO      ");
                    System.out.println("        * DIGITE 0 PARA SAIR           ");
                    System.out.println("--------------------------------------------------------------");
                    System.out.println();

                    boolean keepGoing2 = true;

                    while (keepGoing2) {
                        System.out.print("* ENTRADA: ");
                        opcaoFiltro = Integer.parseInt(sc.nextLine());

                    switch (opcaoFiltro) {
                       case 1:
                            System.out.println("* INICIANDO COMPRESSÃO DE " + arquivoOriginal + "...");
                            long inicioCompressao = System.currentTimeMillis();
                            Huffman.comprimirArquivo(arquivoOriginal, arquivoComprimidoHuff);
                            long fimCompressao = System.currentTimeMillis();
                            tempoCompressaoHuff = (fimCompressao - inicioCompressao);
                            System.out.println("* COMPRESSÃO CONCLUÍDA! (" + arquivoComprimidoHuff + ")");
                            break;
                        case 2:
                            System.out.println("* INICIANDO DESCOMPRESSÃO PARA " + arquivoDescomprimidoHuff + "...");
                            long inicioDescompressao = System.currentTimeMillis();
                            Huffman.descomprimirArquivo(arquivoComprimidoHuff, arquivoDescomprimidoHuff);
                            long fimDescompressao = System.currentTimeMillis();
                            tempoDescompressaoHuff = (fimDescompressao - inicioDescompressao);
                            System.out.println("* DESCOMPRESSÃO CONCLUÍDA! (" + arquivoDescomprimidoHuff + ")");
                            break;
                        case 3:
                            System.out.println();
                            System.out.println("--------------------------------------------------------------");

                            if (tempoCompressaoHuff == 0) {
                                System.out.println("* ARQUIVO NÃO FOI COMPRIMIDO!");
                                break;
                            } else if (tempoDescompressaoHuff == 0) {
                                System.out.println("* ARQUIVO NÃO FOI DESCOMPRIMIDO!");
                            } 

                            System.out.println("* TEMPO DE COMPRESSÃO: " + tempoCompressaoHuff + " MS");
                            System.out.println("* TEMPO DE DESCOMPRESSÃO: " + tempoDescompressaoHuff + " MS");
                            System.out.println("* TAMANHO DO ARQUIVO COMPRIMIDO: " + new File(arquivoComprimidoHuff).length() + " BYTES");
                            System.out.println("* TAMANHO DO ARQUIVO ORIGINAL: " + new File(arquivoOriginal).length() + " BYTES");
                            System.out.printf("* TAXA DE COMPRESSÃO: %.2f%%\n", 100 - (new File(arquivoComprimidoHuff).length() * 100.0 / new File(arquivoOriginal).length()));
                            System.out.println("--------------------------------------------------------------");
                            System.out.println();
                            break;
                            case 0:
                                keepGoing2 = false;
                                break;
                            default:
                                System.out.println("* NÃO EXISTE ESTA OPÇÃO.");
                                break;
                        }
                    }

                    break;
                case 2:
                    System.out.println();
                    System.out.println("--------------------------------------------------------------");
                    System.out.println("               MENU LZW             ");
                    System.out.println("        * DIGITE 1 PARA COMPRIMIR         ");
                    System.out.println("        * DIGITE 2 PARA DESCOMPRIMIR         ");
                    System.out.println("        * DIGITE 3 MOSTRAR TEMPO DE EXECUÇÃO + TAXA DE COMPRESSÃO        ");
                    System.out.println("        * DIGITE 0 PARA SAIR           ");
                    System.out.println("--------------------------------------------------------------");
                    System.out.println();

                    boolean keepGoing3 = true;

                    while (keepGoing3) {
                        System.out.print("* ENTRADA: ");
                        opcaoFiltro = Integer.parseInt(sc.nextLine());

                        switch (opcaoFiltro) {
                            case 1:
                                System.out.println("* INICIANDO COMPRESSÃO DE " + arquivoOriginal + "...");
                                long inicioCompressao = System.currentTimeMillis();
                                LZW.comprimirArquivo(arquivoOriginal, arquivoComprimidoLZW);
                                long fimCompressao = System.currentTimeMillis();
                                tempoCompressaoLZW = (fimCompressao - inicioCompressao);
                                System.out.println("* COMPRESSÃO CONCLUÍDA! (" + arquivoComprimidoLZW + ")");
                                break;
                            case 2:
                                System.out.println("* INICIANDO DESCOMPRESSÃO PARA " + arquivoDescomprimidoLZW + "...");
                                long inicioDescompressao = System.currentTimeMillis();
                                LZW.descomprimirArquivo(arquivoComprimidoLZW, arquivoDescomprimidoLZW);
                                long fimDescompressao = System.currentTimeMillis();
                                tempoDescompressaoLZW = (fimDescompressao - inicioDescompressao);
                                System.out.println("* DESCOMPRESSÃO CONCLUÍDA! (" + arquivoDescomprimidoLZW + ")");
                                break;
                            case 3:
                                System.out.println();
                                System.out.println("--------------------------------------------------------------");

                                if (tempoCompressaoLZW == 0) {
                                    System.out.println("* ARQUIVO NÃO FOI COMPRIMIDO!");
                                    break;
                                } else if (tempoDescompressaoLZW == 0) {
                                    System.out.println("* ARQUIVO NÃO FOI DESCOMPRIMIDO!");
                                }

                                System.out.println("* TEMPO DE COMPRESSÃO: " + tempoCompressaoLZW + " MS");
                                System.out.println("* TEMPO DE DESCOMPRESSÃO: " + tempoDescompressaoLZW + " MS");
                                System.out.println("* TAMANHO DO ARQUIVO COMPRIMIDO: " + new File(arquivoComprimidoLZW).length() + " BYTES");
                                System.out.println("* TAMANHO DO ARQUIVO ORIGINAL: " + new File(arquivoOriginal).length() + " BYTES");
                                System.out.printf("* TAXA DE COMPRESSÃO: %.2f%%\n", 100 - (new File(arquivoComprimidoLZW).length() * 100.0 / new File(arquivoOriginal).length()));                               
                                System.out.println("--------------------------------------------------------------");
                                System.out.println();
                                break;
                            case 0:
                                keepGoing3 = false;
                                break;
                            default:
                                System.out.println("* NÃO EXISTE ESTA OPÇÃO.");
                                break;
                        } 
                    }                   

                    break;
                case 3:
                    File file1 = new File("animeDataBase.huff.db");
                    file1.delete();
                    File file2 = new File("animeDataBase_descomprimido.huff.db");
                    file2.delete();
                    File file3 = new File("animeDataBase.lzw.db");
                    file3.delete();
                    File file4 = new File("animeDataBase_descomprimido.lzw.db");
                    file4.delete();
                    System.out.println("* TODOS OS ARQUIVOS DE COMPRESSÃO FORAM APAGADOS!");
                case 0:
                    keepGoing = false;
                    break;
                default:
                    System.out.println("* NÃO EXISTE ESTA OPÇÃO.");
                    break;
            }
        }
    }
}