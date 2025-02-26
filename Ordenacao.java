import java.io.File;
import java.util.Scanner;

public class Ordenacao {
    public static Scanner sc = new Scanner(System.in);

    public static void main(String args[]) throws Exception{
        int numObjetos = 0;
        int numArquivos = 0;
        boolean keepGoing = true;
        Arquivo arq = new Arquivo("animeDataBase.db");
        
        while (keepGoing) {
        for (int i = 0; i < 50; i++) {
                System.out.print("-");
            }
    
            System.out.println();
            System.out.println("              MENU ORDENACAO                ");
            System.out.println("        * DIGITE 1 PARA ORDENAR SEM OTIMIZAÇÃO         ");
            System.out.println("        * DIGITE 2 PARA ORDENAR COM OTIMIZAÇÃO         ");
            System.out.println("        * DIGITE 0 PARA SAIR           ");
            
            for (int i = 0; i < 50; i++) {
                System.out.print("-");
            }
            System.out.println();

            System.out.print("        * ENTRADA: ");
            int ordenar = sc.nextInt();
            sc.nextLine();
            
            if(ordenar < 3 && ordenar > 0){
                System.out.print("        * NÚMERO DE ARQUIVOS: ");
                numArquivos = sc.nextInt();
                sc.nextLine();
                System.out.print("        * NÚMERO DE OBJETOS POR SESSÃO: ");
                numObjetos = sc.nextInt();
                sc.nextLine();
            }
            
            switch (ordenar) {
                case 1:
                    ordenarNaoOtimizado(arq, numArquivos, numObjetos);
                    System.out.println("* ARQUIVO ORDENADO COM SUCESSO!");
                    break;
                case 2:
                    ordenarOtimizado1(arq, numArquivos, numObjetos);
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
        sc.close();
    }

    public static void ordenarNaoOtimizado(Arquivo arq, int numArquivos, int numObjetos) throws Exception {
    char lapide;
    Arquivo tmpArqs[] = new Arquivo[numArquivos * 2];
    for (int i = 0; i < numArquivos * 2; i++) {
        tmpArqs[i] = new Arquivo("arq" + i + ".db");
        File file = new File("arq" + i + ".db");
        file.deleteOnExit();
    }

    // Pula o cabeçalho
    arq.arq.seek(4);
    int currentFile = 0;
    // Loop para ler todos os registros
    while (arq.arq.getFilePointer() < arq.arq.length()) {
        Animes sessao[] = new Animes[numObjetos]; // Reinicializa o array a cada sessão
        int sessaoSize = 0; // Contador de objetos válidos na sessão atual

        // Preenche a sessão com até numObjetos registros
        for (int i = 0; i < numObjetos; i++) {
            if (arq.arq.getFilePointer() >= arq.arq.length()) {
                break; // Sai do loop se atingir o final do arquivo
            }

            lapide = arq.arq.readChar();
            int tamanhoRegistro = arq.arq.readShort();

            // Lê os bytes do registro
            byte[] ba = new byte[tamanhoRegistro];
            arq.arq.read(ba);

            // Verifica se o registro não está marcado como excluído
            if (lapide == ' ') {
                Animes anime = new Animes();
                anime.fromByteArray(ba);
                sessao[sessaoSize] = anime; // Adiciona o objeto ao array
                sessaoSize++; // Incrementa o contador de objetos válidos
            } else {
                i--;
            }
        }

        // Ordena a sessão
        selecao(sessao, sessaoSize);
        for(int i = 0; i < sessaoSize; i++){
            tmpArqs[currentFile].create(sessao[i]);
        }
        currentFile++;
        if(currentFile == numArquivos){
            currentFile = 0;
        }
    }
    
    arq.arq.seek(0);
    int maxID = arq.arq.readInt();
    int numSessoesPorArq = maxID / numArquivos / numObjetos;
    // intercalacao
    for(int k = 0; k < numArquivos; k++){
        tmpArqs[k].arq.seek(0);
    }

    for(int i = 0; i < numSessoesPorArq; i++){
        
    }






    // Fecha os arquivos temporários
    for (int i = 0; i < numArquivos * 2; i++) {
        tmpArqs[i].close();
    }
}

    public static void ordenarOtimizado1(Arquivo arq, int numArquivos, int numObjetos) throws Exception{
        
    }

    public static void selecao (Animes sessao[], int numObjetos) {
        for(int i = 0; i < (numObjetos-1); i++){
            int menor = i;
            for(int j = (i+1); j < numObjetos; j++){
                if(sessao[menor].getId() > sessao[j].getId()) {
                    menor = j;
                }
            }
            Animes tmp = sessao[menor];
            sessao[menor] = sessao[i];
            sessao[i] = tmp;
        }
    }
}