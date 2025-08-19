import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Criptografia {
    // Instância RSA atual para operações
    private static RSA rsaAtual = null;
    
    public static void main(String[] args) throws Exception {
        abrirMenuCriptografia();
    }
    
    public static void abrirMenuCriptografia() throws Exception {
        Scanner sc = new Scanner(System.in);
        boolean keepGoing = true;

        while (keepGoing) {
            System.out.println();
            System.out.println("--------------------------------------------------------------");
            System.out.println("                 MENU CRIPTOGRAFIA                ");
            System.out.println("        * DIGITE 1 PARA CRIPTOGRAFAR UTILIZANDO VIGENÉRE         ");
            System.out.println("        * DIGITE 2 PARA CRIPTOPGRAFAR UTILIZANDO RSA         ");
            System.out.println("        * DIGITE 3 PARA HABILITAR/DESABILITAR CRIPTOGRAFIA NO CRUD         ");
            System.out.println("        * DIGITE 4 PARA APAGAR TODOS OS ARQUIVOS CRIPTOGRAFADOS CRIADOS ");
            System.out.println("        * DIGITE 0 PARA SAIR           ");
            System.out.println("--------------------------------------------------------------");
            
            // Mostra status da criptografia
            if (Animes.isCriptografiaHabilitada()) {
                String tipo = Animes.getTipoCriptografia();
                System.out.println("        [CRIPTOGRAFIA CRUD: HABILITADA - " + tipo + "]");
            } else {
                System.out.println("        [CRIPTOGRAFIA CRUD: DESABILITADA]");
            }
            System.out.println();

            System.out.print("* ENTRADA: ");
            int menu = Integer.parseInt(sc.nextLine());
            
            switch (menu) {
                case 1:
                    // Menu Vigenère inline
                    boolean keepGoingVigenere = true;
                    while (keepGoingVigenere) {
                        System.out.println();
                        System.out.println("--------------------------------------------------------------");
                        System.out.println("                 MENU VIGENÈRE                ");
                        System.out.println("        * DIGITE 1 PARA CRIPTOGRAFAR ARQUIVO DB         ");
                        System.out.println("        * DIGITE 2 PARA DESCRIPTOGRAFAR ARQUIVO DB         ");
                        System.out.println("        * DIGITE 3 PARA CRIPTOGRAFAR ARQUIVO COMPRIMIDO         ");
                        System.out.println("        * DIGITE 4 PARA DESCRIPTOGRAFAR ARQUIVO COMPRIMIDO         ");
                        System.out.println("        * DIGITE 0 PARA VOLTAR AO MENU ANTERIOR         ");
                        System.out.println("--------------------------------------------------------------");
                        System.out.println();

                        System.out.print("* ENTRADA: ");
                        int opcaoVigenere = Integer.parseInt(sc.nextLine());
                        
                        switch (opcaoVigenere) {
                            case 1:
                                criptografarArquivoDB(sc);
                                break;
                            case 2:
                                descriptografarArquivoDB(sc);
                                break;
                            case 3:
                                criptografarArquivoComprimido(sc);
                                break;
                            case 4:
                                descriptografarArquivoComprimido(sc);
                                break;
                            case 0:
                                keepGoingVigenere = false;
                                break;
                            default:
                                System.out.println("* OPÇÃO INVÁLIDA!");
                                break;
                        }
                    }
                    break;
                case 2:
                    // Menu RSA inline
                    boolean keepGoingRSA = true;
                    while (keepGoingRSA) {
                        System.out.println();
                        System.out.println("--------------------------------------------------------------");
                        System.out.println("                 MENU RSA                ");
                        System.out.println("        * DIGITE 1 PARA GERAR NOVAS CHAVES RSA ALEATÓRIAS         ");
                        System.out.println("        * DIGITE 2 PARA VISUALIZAR CHAVES ATUAIS         ");
                        System.out.println("        * DIGITE 3 PARA CRIPTOGRAFAR ARQUIVO DB         ");
                        System.out.println("        * DIGITE 4 PARA DESCRIPTOGRAFAR ARQUIVO DB         ");
                        System.out.println("        * DIGITE 5 PARA CRIPTOGRAFAR ARQUIVO COMPRIMIDO         ");
                        System.out.println("        * DIGITE 6 PARA DESCRIPTOGRAFAR ARQUIVO COMPRIMIDO         ");
                        System.out.println("        * DIGITE 0 PARA VOLTAR AO MENU ANTERIOR         ");
                        System.out.println("--------------------------------------------------------------");
                        System.out.println();

                        System.out.print("* ENTRADA: ");
                        int opcaoRSA = Integer.parseInt(sc.nextLine());
                        
                        switch (opcaoRSA) {
                            case 1:
                                gerarChavesRSA();
                                break;
                            case 2:
                                visualizarChavesRSA();
                                break;
                            case 3:
                                criptografarArquivoDBRSA(sc);
                                break;
                            case 4:
                                descriptografarArquivoDBRSA(sc);
                                break;
                            case 5:
                                criptografarArquivoComprimidoRSA(sc);
                                break;
                            case 6:
                                descriptografarArquivoComprimidoRSA(sc);
                                break;
                            case 0:
                                keepGoingRSA = false;
                                break;
                            default:
                                System.out.println("* OPÇÃO INVÁLIDA!");
                                break;
                        }
                    }
                    break;
                case 3:
                    configurarCriptografiaCrud(sc);
                    break;
                case 4:
                    apagarArquivosCriptografados();
                    break;
                case 0:
                    keepGoing = false;
                    break;
                default:
                    System.out.println("* OPÇÃO INVÁLIDA!");
                    break;
            }
        }
    }
    
    private static void criptografarArquivoDB(Scanner sc) throws IOException {
        System.out.print("* DIGITE A CHAVE DE CRIPTOGRAFIA (apenas letras): ");
        String chave = sc.nextLine().replaceAll("[^A-Za-z]", "").toUpperCase();
        
        if (chave.isEmpty()) {
            System.out.println("* CHAVE INVÁLIDA! Use apenas letras.");
            return;
        }
        
        String arquivoOriginal = "animeDataBase.db";
        String arquivoCriptografado = "animeDataBase.cripto.db";
        
        File arquivo = new File(arquivoOriginal);
        if (!arquivo.exists()) {
            System.out.println("* ARQUIVO " + arquivoOriginal + " NÃO ENCONTRADO!");
            return;
        }
        
        Vigenere vigenere = new Vigenere(chave);
        vigenere.criptografarArquivo(arquivoOriginal, arquivoCriptografado);
        
        System.out.println("* ARQUIVO CRIPTOGRAFADO COM SUCESSO: " + arquivoCriptografado);
        System.out.println("* CHAVE UTILIZADA: " + chave);
    }
    
    private static void descriptografarArquivoDB(Scanner sc) throws IOException {
        System.out.print("* DIGITE A CHAVE DE DESCRIPTOGRAFIA: ");
        String chave = sc.nextLine().replaceAll("[^A-Za-z]", "").toUpperCase();
        
        if (chave.isEmpty()) {
            System.out.println("* CHAVE INVÁLIDA! Use apenas letras.");
            return;
        }
        
        String arquivoCriptografado = "animeDataBase.cripto.db";
        String arquivoDescriptografado = "animeDataBase.descripto.db";
        
        File arquivo = new File(arquivoCriptografado);
        if (!arquivo.exists()) {
            System.out.println("* ARQUIVO " + arquivoCriptografado + " NÃO ENCONTRADO!");
            return;
        }
        
        Vigenere vigenere = new Vigenere(chave);
        vigenere.descriptografarArquivo(arquivoCriptografado, arquivoDescriptografado);
        
        System.out.println("* ARQUIVO DESCRIPTOGRAFADO COM SUCESSO: " + arquivoDescriptografado);
    }
    
    private static void criptografarArquivoComprimido(Scanner sc) throws IOException {
        System.out.println("* ESCOLHA O ARQUIVO COMPRIMIDO:");
        System.out.println("  1 - animeDataBase.huff.db (Huffman)");
        System.out.println("  2 - animeDataBase.lzw.db (LZW)");
        System.out.print("* OPÇÃO: ");
        
        int opcao = Integer.parseInt(sc.nextLine());
        String arquivoOriginal;
        String arquivoCriptografado;
        
        switch (opcao) {
            case 1:
                arquivoOriginal = "animeDataBase.huff.db";
                arquivoCriptografado = "animeDataBase.huff.cripto.db";
                break;
            case 2:
                arquivoOriginal = "animeDataBase.lzw.db";
                arquivoCriptografado = "animeDataBase.lzw.cripto.db";
                break;
            default:
                System.out.println("* OPÇÃO INVÁLIDA!");
                return;
        }
        
        File arquivo = new File(arquivoOriginal);
        if (!arquivo.exists()) {
            System.out.println("* ARQUIVO " + arquivoOriginal + " NÃO ENCONTRADO!");
            return;
        }
        
        System.out.print("* DIGITE A CHAVE DE CRIPTOGRAFIA (apenas letras): ");
        String chave = sc.nextLine().replaceAll("[^A-Za-z]", "").toUpperCase();
        
        if (chave.isEmpty()) {
            System.out.println("* CHAVE INVÁLIDA! Use apenas letras.");
            return;
        }
        
        Vigenere vigenere = new Vigenere(chave);
        vigenere.criptografarArquivo(arquivoOriginal, arquivoCriptografado);
        
        System.out.println("* ARQUIVO COMPRIMIDO CRIPTOGRAFADO COM SUCESSO: " + arquivoCriptografado);
        System.out.println("* CHAVE UTILIZADA: " + chave);
    }
    
    private static void descriptografarArquivoComprimido(Scanner sc) throws IOException {
        System.out.println("* ESCOLHA O ARQUIVO COMPRIMIDO CRIPTOGRAFADO:");
        System.out.println("  1 - animeDataBase.huff.cripto.db (Huffman)");
        System.out.println("  2 - animeDataBase.lzw.cripto.db (LZW)");
        System.out.print("* OPÇÃO: ");
        
        int opcao = Integer.parseInt(sc.nextLine());
        String arquivoCriptografado;
        String arquivoDescriptografado;
        
        switch (opcao) {
            case 1:
                arquivoCriptografado = "animeDataBase.huff.cripto.db";
                arquivoDescriptografado = "animeDataBase.huff.descripto.db";
                break;
            case 2:
                arquivoCriptografado = "animeDataBase.lzw.cripto.db";
                arquivoDescriptografado = "animeDataBase.lzw.descripto.db";
                break;
            default:
                System.out.println("* OPÇÃO INVÁLIDA!");
                return;
        }
        
        File arquivo = new File(arquivoCriptografado);
        if (!arquivo.exists()) {
            System.out.println("* ARQUIVO " + arquivoCriptografado + " NÃO ENCONTRADO!");
            return;
        }
        
        System.out.print("* DIGITE A CHAVE DE DESCRIPTOGRAFIA: ");
        String chave = sc.nextLine().replaceAll("[^A-Za-z]", "").toUpperCase();
        
        if (chave.isEmpty()) {
            System.out.println("* CHAVE INVÁLIDA! Use apenas letras.");
            return;
        }
        
        Vigenere vigenere = new Vigenere(chave);
        vigenere.descriptografarArquivo(arquivoCriptografado, arquivoDescriptografado);
        
        System.out.println("* ARQUIVO COMPRIMIDO DESCRIPTOGRAFADO COM SUCESSO: " + arquivoDescriptografado);
    }
    
    private static void configurarCriptografiaCrud(Scanner sc) {
        System.out.println();
        System.out.println("        * DIGITE 1 PARA HABILITAR CRIPTOGRAFIA NO CRUD");
        System.out.println("        * DIGITE 2 PARA DESABILITAR CRIPTOGRAFIA NO CRUD");
        System.out.print("* ENTRADA: ");
        int opcaoCripto = Integer.parseInt(sc.nextLine());
        
        switch (opcaoCripto) {
            case 1:
                System.out.println();
                System.out.println("        * ESCOLHA O TIPO DE CRIPTOGRAFIA:");
                System.out.println("        * DIGITE 1 PARA VIGENÈRE");
                System.out.println("        * DIGITE 2 PARA RSA");
                System.out.print("* ENTRADA: ");
                int tipoCriptografia = Integer.parseInt(sc.nextLine());
                
                switch (tipoCriptografia) {
                    case 1:
                        // Vigenère
                        System.out.print("* DIGITE A CHAVE DE CRIPTOGRAFIA VIGENÈRE (apenas letras): ");
                        String chaveVigenere = sc.nextLine().replaceAll("[^A-Za-z]", "").toUpperCase();
                        
                        if (chaveVigenere.isEmpty()) {
                            System.out.println("* CHAVE INVÁLIDA! Use apenas letras.");
                        } else {
                            Animes.habilitarCriptografia(chaveVigenere);
                            System.out.println("* CRIPTOGRAFIA VIGENÈRE HABILITADA NO CRUD COM CHAVE: " + chaveVigenere);
                            System.out.println("* AVISO: Novos registros serão salvos criptografados!");
                        }
                        break;
                    case 2:
                        // RSA
                        System.out.println();
                        System.out.println("        * DIGITE 1 PARA GERAR NOVAS CHAVES RSA NUMÉRICAS");
                        System.out.println("        * DIGITE 2 PARA USAR CHAVES RSA NUMÉRICAS ATUAIS (se disponíveis)");
                        System.out.print("* ENTRADA: ");
                        int opcaoRSA = Integer.parseInt(sc.nextLine());
                        
                        switch (opcaoRSA) {
                            case 1:
                                rsaAtual = new RSA();
                                Animes.habilitarCriptografiaRSA(rsaAtual);
                                System.out.println("* NOVAS CHAVES RSA NUMÉRICAS GERADAS E CRIPTOGRAFIA HABILITADA NO CRUD!");
                                System.out.println("* CHAVES: " + rsaAtual.getChavesComoString().substring(0, 50) + "...");
                                System.out.println("* AVISO: Novos registros serão salvos criptografados!");
                                break;
                            case 2:
                                if (rsaAtual != null) {
                                    Animes.habilitarCriptografiaRSA(rsaAtual);
                                    System.out.println("* CRIPTOGRAFIA RSA HABILITADA NO CRUD COM CHAVES NUMÉRICAS ATUAIS!");
                                    System.out.println("* AVISO: Novos registros serão salvos criptografados!");
                                } else {
                                    System.out.println("* NENHUMA CHAVE RSA NUMÉRICA ATUAL DISPONÍVEL! Gere novas chaves primeiro.");
                                }
                                break;
                            default:
                                System.out.println("* OPÇÃO INVÁLIDA!");
                                break;
                        }
                        break;
                    default:
                        System.out.println("* TIPO DE CRIPTOGRAFIA INVÁLIDO!");
                        break;
                }
                break;
            case 2:
                Animes.desabilitarCriptografia();
                System.out.println("* CRIPTOGRAFIA DO CRUD DESABILITADA!");
                break;
            default:
                System.out.println("* OPÇÃO INVÁLIDA!");
                break;
        }
    }
    
    private static void apagarArquivosCriptografados() {
        System.out.println("\n* APAGANDO ARQUIVOS CRIPTOGRAFADOS...");
        
        String[] arquivosCriptografados = {
            "animeDataBase.cripto.db",
            "animeDataBase.descripto.db",
            "animeDataBase.cripto.rsa.db",
            "animeDataBase.descripto.rsa.db",
            "animeDataBase.huff.cripto.db",
            "animeDataBase.huff.descripto.db",
            "animeDataBase.huff.cripto.rsa.db",
            "animeDataBase.huff.descripto.rsa.db",
            "animeDataBase.lzw.cripto.db",
            "animeDataBase.lzw.descripto.db",
            "animeDataBase.lzw.cripto.rsa.db",
            "animeDataBase.lzw.descripto.rsa.db",
            "animeDataBase.temp.db",
            "animeDataBase.temp.rsa.db",
            "animeDataBase.huff.temp.db",
            "animeDataBase.huff.temp.rsa.db",
            "animeDataBase.lzw.temp.db",
            "animeDataBase.lzw.temp.rsa.db"
        };
        
        int arquivosApagados = 0;
        for (String nomeArquivo : arquivosCriptografados) {
            File arquivo = new File(nomeArquivo);
            if (arquivo.exists() && arquivo.delete()) {
                System.out.println("  > Apagado: " + nomeArquivo);
                arquivosApagados++;
            } else if (arquivo.exists()) {
                System.out.println("  x Erro ao apagar: " + nomeArquivo);
            }
        }
        
        if (arquivosApagados == 0) {
            System.out.println("* NENHUM ARQUIVO CRIPTOGRAFADO ENCONTRADO!");
        } else {
            System.out.println("* TOTAL DE ARQUIVOS CRIPTOGRAFADOS APAGADOS: " + arquivosApagados);
        }
    }
    
    // Métodos RSA
    private static void gerarChavesRSA() {
        rsaAtual = new RSA();
        System.out.println("* NOVAS CHAVES RSA NUMÉRICAS GERADAS ALEATORIAMENTE COM SUCESSO!");
        System.out.println("* CHAVES: " + rsaAtual.getChavesComoString().substring(0, 50) + "...");
        System.out.println("* Use a opção 2 para visualizar as chaves numéricas completas.");
    }
    
    private static void visualizarChavesRSA() {
        if (rsaAtual == null) {
            System.out.println("* NENHUMA CHAVE RSA GERADA! Use a opção 1 para gerar novas chaves numéricas.");
            return;
        }
        
        System.out.println("* CHAVES RSA NUMÉRICAS ATUAIS:");
        System.out.println("* FORMATO: ChavePublica:ChavePrivada:Modulo (apenas números)");
        System.out.println("* " + rsaAtual.getChavesComoString());
        System.out.println("* GUARDE ESTAS CHAVES NUMÉRICAS EM LOCAL SEGURO!");
    }
    
    private static void criptografarArquivoDBRSA(Scanner sc) throws IOException {
        if (rsaAtual == null) {
            System.out.println("* NENHUMA CHAVE RSA DISPONÍVEL! Gere ou carregue chaves primeiro.");
            return;
        }
        
        String arquivoOriginal = "animeDataBase.db";
        String arquivoCriptografado = "animeDataBase.cripto.rsa.db";
        
        File arquivo = new File(arquivoOriginal);
        if (!arquivo.exists()) {
            System.out.println("* ARQUIVO " + arquivoOriginal + " NÃO ENCONTRADO!");
            return;
        }
        
        rsaAtual.criptografarArquivo(arquivoOriginal, arquivoCriptografado);
        
        System.out.println("* ARQUIVO CRIPTOGRAFADO COM RSA COM SUCESSO: " + arquivoCriptografado);
    }
    
    private static void descriptografarArquivoDBRSA(Scanner sc) throws IOException {
        if (rsaAtual == null) {
            System.out.println("* NENHUMA CHAVE RSA DISPONÍVEL! Gere ou carregue chaves primeiro.");
            return;
        }
        
        String arquivoCriptografado = "animeDataBase.cripto.rsa.db";
        String arquivoDescriptografado = "animeDataBase.descripto.rsa.db";
        
        File arquivo = new File(arquivoCriptografado);
        if (!arquivo.exists()) {
            System.out.println("* ARQUIVO " + arquivoCriptografado + " NÃO ENCONTRADO!");
            return;
        }
        
        rsaAtual.descriptografarArquivo(arquivoCriptografado, arquivoDescriptografado);
        
        System.out.println("* ARQUIVO DESCRIPTOGRAFADO COM RSA COM SUCESSO: " + arquivoDescriptografado);
    }
    
    private static void criptografarArquivoComprimidoRSA(Scanner sc) throws IOException {
        // Verifica se há instância RSA habilitada no sistema
        RSA rsa = Animes.getRSAInstance();
        if (rsa == null) {
            System.out.println("* NENHUMA INSTÂNCIA RSA DISPONÍVEL!");
            System.out.println("* Habilite a criptografia RSA no menu CRUD primeiro.");
            return;
        }
        
        System.out.println("* ESCOLHA O ARQUIVO COMPRIMIDO:");
        System.out.println("  1 - animeDataBase.huff.db (Huffman)");
        System.out.println("  2 - animeDataBase.lzw.db (LZW)");
        System.out.print("* OPÇÃO: ");
        
        int opcao = Integer.parseInt(sc.nextLine());
        String arquivoOriginal;
        String arquivoCriptografado;
        
        switch (opcao) {
            case 1:
                arquivoOriginal = "animeDataBase.huff.db";
                arquivoCriptografado = "animeDataBase.huff.cripto.rsa.db";
                break;
            case 2:
                arquivoOriginal = "animeDataBase.lzw.db";
                arquivoCriptografado = "animeDataBase.lzw.cripto.rsa.db";
                break;
            default:
                System.out.println("* OPÇÃO INVÁLIDA!");
                return;
        }
        
        File arquivo = new File(arquivoOriginal);
        if (!arquivo.exists()) {
            System.out.println("* ARQUIVO " + arquivoOriginal + " NÃO ENCONTRADO!");
            return;
        }
        
        rsa.criptografarArquivo(arquivoOriginal, arquivoCriptografado);
        
        System.out.println("* ARQUIVO COMPRIMIDO CRIPTOGRAFADO COM RSA COM SUCESSO: " + arquivoCriptografado);
        System.out.println("* USANDO A MESMA INSTÂNCIA RSA DO CRUD");
    }
    
    private static void descriptografarArquivoComprimidoRSA(Scanner sc) throws IOException {
        // Verifica se há instância RSA habilitada no sistema
        RSA rsa = Animes.getRSAInstance();
        if (rsa == null) {
            System.out.println("* NENHUMA INSTÂNCIA RSA DISPONÍVEL!");
            System.out.println("* Habilite a criptografia RSA no menu CRUD primeiro.");
            return;
        }
        
        System.out.println("* ESCOLHA O ARQUIVO COMPRIMIDO CRIPTOGRAFADO:");
        System.out.println("  1 - animeDataBase.huff.cripto.rsa.db (Huffman)");
        System.out.println("  2 - animeDataBase.lzw.cripto.rsa.db (LZW)");
        System.out.print("* OPÇÃO: ");
        
        int opcao = Integer.parseInt(sc.nextLine());
        String arquivoCriptografado;
        String arquivoDescriptografado;
        
        switch (opcao) {
            case 1:
                arquivoCriptografado = "animeDataBase.huff.cripto.rsa.db";
                arquivoDescriptografado = "animeDataBase.huff.descripto.rsa.db";
                break;
            case 2:
                arquivoCriptografado = "animeDataBase.lzw.cripto.rsa.db";
                arquivoDescriptografado = "animeDataBase.lzw.descripto.rsa.db";
                break;
            default:
                System.out.println("* OPÇÃO INVÁLIDA!");
                return;
        }
        
        File arquivo = new File(arquivoCriptografado);
        if (!arquivo.exists()) {
            System.out.println("* ARQUIVO " + arquivoCriptografado + " NÃO ENCONTRADO!");
            return;
        }
        
        rsa.descriptografarArquivo(arquivoCriptografado, arquivoDescriptografado);
        
        System.out.println("* ARQUIVO COMPRIMIDO DESCRIPTOGRAFADO COM RSA COM SUCESSO: " + arquivoDescriptografado);
        System.out.println("* USANDO A MESMA INSTÂNCIA RSA DO CRUD");
    }
}