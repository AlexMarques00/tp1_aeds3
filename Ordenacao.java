import java.io.File;
import java.util.Scanner;

public class Ordenacao {
    public static Scanner sc = new Scanner(System.in);
    public static int ultimoID;

    public static void abrirOrdenacao() throws Exception {
        int numObjetos = 0;
        int numArquivos = 0;
        boolean keepGoing = true;

        System.out.print("        * DIGITE O NOME DO ARQUIVO QUE DESEJA ORDENAR: ");
        String name = sc.nextLine();
        Arquivo arq = new Arquivo(name);
        File file = new File(name);
        file.deleteOnExit();

        arq.arq.seek(0);
        ultimoID = arq.arq.readInt();

        while (keepGoing) {
            System.out.println("--------------------------------------------------");
            System.out.println();
            System.out.println("              MENU ORDENACAO                           ");
            System.out.println("        * DIGITE 1 PARA ORDENAR SEM OTIMIZAÇÃO         ");
            System.out.println("        * DIGITE 2 PARA ORDENAR COM OTIMIZAÇÃO         ");
            System.out.println("        * DIGITE 0 PARA SAIR                           ");
            System.out.println("--------------------------------------------------");
            System.out.println();

            System.out.print("* ENTRADA: ");
            int ordenar = sc.nextInt();
            sc.nextLine();

            if (ordenar < 3 && ordenar > 0) {
                System.out.print("        * NÚMERO DE ARQUIVOS: ");
                numArquivos = sc.nextInt();
                sc.nextLine();
                System.out.print("        * NÚMERO DE OBJETOS POR SESSÃO: ");
                numObjetos = sc.nextInt();
                sc.nextLine();
            }

            switch (ordenar) {
                case 1:
                    ordenar(arq, numArquivos, numObjetos, 1);
                    System.out.println("* ARQUIVO ORDENADO COM SUCESSO!");
                    break;
                case 2:
                    ordenar(arq, numArquivos, numObjetos, 2);
                    System.out.println("* ARQUIVO ORDENADO COM SUCESSO!");
                    break;
                case 0:
                    keepGoing = false;
                    break;
                default:
                    System.out.println("* NÃO EXISTE ESTA OPÇÃO.");
                    break;
            }
        }

        arq.close();
    }

    public static void ordenar(Arquivo arq, int numArquivos, int numObjetos, int tipo) throws Exception {
        distribuir(arq, numArquivos, numObjetos, tipo);

        // Cria um array com os arquivos temporários
        Arquivo[] tmpArqs = new Arquivo[numArquivos * 2];
        for (int i = 0; i < numArquivos * 2; i++) {
            tmpArqs[i] = new Arquivo("arq" + i + ".db");
        }

        intercalar(arq, tmpArqs);
    }

    public static void distribuir(Arquivo arq, int numArquivos, int numObjetos, int tipo) throws Exception {
        char lapide;
        int tamanhoInicial = numObjetos; // Tamanho inicial da sessão
    
        // Cria arquivos temporários
        Arquivo[] tmpArqs = new Arquivo[numArquivos];
        for (int i = 0; i < numArquivos; i++) {
            tmpArqs[i] = new Arquivo("arq" + i + ".db");
        }
    
        // Pula o cabeçalho
        arq.arq.seek(4);
        int currentFile = 0;
    
        // Loop para ler todos os registros e distribuir em sessões nos arquivos temporários
        while (arq.arq.getFilePointer() < arq.arq.length()) {
            Animes sessao[] = new Animes[numObjetos]; // Reinicializa o array a cada sessão
            int sessaoSize = 0;
            boolean ordenada = true;
            int ultimoId = 0; // Último ID lido para verificar a ordenação
    
            // Lê registros para a sessão atual
            while (sessaoSize < numObjetos && arq.arq.getFilePointer() < arq.arq.length()) {
                lapide = arq.arq.readChar();
                int tamanhoRegistro = arq.arq.readShort();
    
                // Lê os bytes do registro
                byte[] ba = new byte[tamanhoRegistro];
                arq.arq.read(ba);
    
                // Verifica lapide
                if (lapide == ' ') {
                    Animes anime = new Animes();
                    anime.fromByteArray(ba);
    
                    // Verifica se a sessão ainda está ordenada
                    if (tipo == 2 && ordenada && sessaoSize > 0) {
                        if (anime.getId() < ultimoId) {
                            ordenada = false; // A ordenação foi quebrada
                        }
                    }
    
                    // Adiciona o objeto à sessão
                    sessao[sessaoSize] = anime;
                    sessaoSize++;
                    ultimoId = anime.getId(); // Atualiza o último ID lido
    
                    // Se a sessão estiver ordenada e estiver prestes a atingir o tamanho máximo, verifica a próxima sessão
                    if (tipo == 2 && ordenada && arq.arq.getFilePointer() < arq.arq.length() && sessaoSize + 1 == numObjetos) {
                        boolean proxOrdenada = verificarOrdenada(arq, numObjetos);
    
                        // Se a próxima sessão estiver ordenada, expande a sessão atual
                        if (proxOrdenada) {
                            numObjetos *= 2; // Aumenta o tamanho da sessão
                            sessao = expandirArray(sessao); // Expande o array
                        }
                    }
                }
            }
    
            // Se a sessão não estiver ordenada, volta ao tamanho inicial
            if (tipo == 2 && !ordenada) {
                numObjetos = tamanhoInicial;
            }
    
            // Ordena a sessão apenas se não estiver ordenada
            if (!ordenada) {
                selecao(sessao, sessaoSize);
            }
    
            // Escreve a sessão no arquivo temporário atual
            for (int i = 0; i < sessaoSize; i++) {
                tmpArqs[currentFile].create(sessao[i]);
            }
    
            // Avança para o próximo arquivo temporário
            currentFile++;
            if (currentFile == numArquivos) {
                currentFile = 0; // Volta ao primeiro arquivo temporário
            }
        }
    
        for (Arquivo tmpArq : tmpArqs) {
            tmpArq.close();
        }
    }

    public static void intercalar(Arquivo arq, Arquivo[] tmpArqs) throws Exception {
        System.out.print("        * DIGITE O NOME DO ARQUIVO ORDENADO: ");
        Arquivo arquivoFinal = new Arquivo(sc.nextLine());
        arquivoFinal.arq.seek(0);
        arquivoFinal.arq.writeInt(ultimoID);

        // Posiciona os ponteiros no início dos arquivos temporários
        for (int i = 0; i < tmpArqs.length; i++) {
            tmpArqs[i].arq.seek(0);
        }

        // Método merge sort externo feito com ajuda do DeepSeek
        Animes[] registros = new Animes[tmpArqs.length];
        boolean[] arquivoFinalizado = new boolean[tmpArqs.length];

        // Inicializa os registros
        for (int i = 0; i < tmpArqs.length; i++) {
            registros[i] = lerProximoRegistro(tmpArqs[i]);
            if (registros[i] == null) {
                arquivoFinalizado[i] = true;
            }
        }

        while (true) {
            int menorIndice = -1;
            Animes menorRegistro = null;

            for (int i = 0; i < tmpArqs.length; i++) {
                if (!arquivoFinalizado[i] && registros[i] != null) {
                    if (menorRegistro == null || registros[i].getId() < menorRegistro.getId()) {
                        menorRegistro = registros[i];
                        menorIndice = i;
                    }
                }
            }

            if (menorIndice == -1) {
                break; // Não há mais registros para intercalar
            }

            // Escreve o menor registro no arquivo final
            arquivoFinal.create(menorRegistro);
            menorRegistro.write();

            // Lê o próximo registro do arquivo de onde saiu o menor registro
            registros[menorIndice] = lerProximoRegistro(tmpArqs[menorIndice]);
            if (registros[menorIndice] == null) {
                arquivoFinalizado[menorIndice] = true;
            }
        }

        for (int i = 0; i < tmpArqs.length; i++) {
            tmpArqs[i].close(); // Fecha cada arquivo temporário
            File file = new File("arq" + i + ".db");
            file.delete(); // Marca o arquivo para ser excluído
        }
    }

    private static Animes lerProximoRegistro(Arquivo arquivo) throws Exception {
        if (arquivo.arq.getFilePointer() >= arquivo.arq.length()) {
            return null;
        }

        char lapide = arquivo.arq.readChar();
        int tamanhoRegistro = arquivo.arq.readShort();

        // Lê os bytes do registro
        byte[] ba = new byte[tamanhoRegistro];
        arquivo.arq.read(ba);

        // Verifica se o registro não está marcado como excluído
        if (lapide == ' ') {
            Animes anime = new Animes();
            anime.fromByteArray(ba);
            return anime;
        } else {
            return lerProximoRegistro(arquivo); // Ignora registros excluídos e lê o próximo
        }
    }

    // Método para verificar se a próxima sessão está ordenada
    private static boolean verificarOrdenada(Arquivo arq, int numObjetos) throws Exception {
        long posicaoAtual = arq.arq.getFilePointer();
        boolean ordenada = true;
        int ultimoId = 0;

        // Lê a próxima sessão
        for (int i = 0; i < numObjetos && arq.arq.getFilePointer() < arq.arq.length(); i++) {
            char lapide = arq.arq.readChar();
            int tamanhoRegistro = arq.arq.readShort();

            // Lê os bytes do registro
            byte[] ba = new byte[tamanhoRegistro];
            arq.arq.read(ba);

            // Verifica lapide
            if (lapide == ' ') {
                Animes anime = new Animes();
                anime.fromByteArray(ba);

                // Verifica se a sessão está ordenada
                if (i > 0 && anime.getId() < ultimoId) {
                    ordenada = false;
                    break;
                }

                ultimoId = anime.getId();
            }
        }

        arq.arq.seek(posicaoAtual); // Restaura a posição do ponteiro
        return ordenada;
    }

    // Método para expandir dinamicamente um array (deepSeek)
    private static Animes[] expandirArray(Animes[] original) {
        Animes[] novoArray = new Animes[original.length * 2]; // Dobra o tamanho do array
        System.arraycopy(original, 0, novoArray, 0, original.length);
        return novoArray;
    }

    // Método de ordenaçāo escolhido: seleção
    public static void selecao(Animes sessao[], int numObjetos) {
        for (int i = 0; i < (numObjetos - 1); i++) {
            int menor = i;
            for (int j = (i + 1); j < numObjetos; j++) {
                if (sessao[menor].getId() > sessao[j].getId()) {
                    menor = j;
                }
            }
            Animes tmp = sessao[menor];
            sessao[menor] = sessao[i];
            sessao[i] = tmp;
        }
    }
}