import java.io.File;
import java.util.ArrayList;
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

    public static void ordenarNaoOtimizado(Arquivo arq, int numArquivos, int numObjetos) throws Exception{
        Animes anime = new Animes();
        char lapide;
        Arquivo tmpArqs[] = new Arquivo[numArquivos*2];
        for(int i = 0; i < numArquivos*2; i++){
            tmpArqs[i] = new Arquivo("arq"+i+".db");
            File file = new File("arq"+i+".db");
            file.deleteOnExit();
        }

        // Pula o cabeçalho
        arq.arq.seek(4);
        Animes sessao[] = new Animes[numObjetos];
        // Loop para ler todos os registros
        while (arq.arq.getFilePointer() < arq.arq.length()) {
            for(int i = 0; i < numObjetos; i++){

                lapide = arq.arq.readChar();
                int tamanhoRegistro = arq.arq.readShort();
        
                // Lê os bytes do registro
                byte[] ba = new byte[tamanhoRegistro];
                arq.arq.read(ba);

                // Cria uma nova instância de Animes para cada posição do array
                anime = new Animes();
                anime.fromByteArray(ba);
        
                // Verifica se o registro não está marcado como excluído
                if (lapide == ' ') {
                    sessao[i] = anime;
                }
                
            }
            selecao(sessao, numObjetos);
            for(int i = 0; i < numObjetos; i++){
                sessao[i].write();
            }
        }

        for(int i = 0; i < numArquivos*2; i++){
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