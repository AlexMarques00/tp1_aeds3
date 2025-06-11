import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Scanner;

public class CasamentoPadrao {
    public static void abrirCasamentoPadrao() throws Exception {
        Scanner sc = new Scanner(System.in);
        boolean keepGoing = true;

        HashExtensivo hash = new HashExtensivo(ReadCSV.getElementosCesto());
        ArvoreBMais arvore = new ArvoreBMais("ArvoreB+.db", ReadCSV.getOrdem());
        Arquivo arq = new Arquivo("animeDataBase.db");

        String padrao;
        int tipo;
        ArrayList<Long> posicoes;
        ArrayList<Integer> ids;

        while (keepGoing) {
            System.out.println("--------------------------------------------------------------");
            System.out.println("              MENU CASAMENTO DE PADRÃO               ");
            System.out.println("        * DIGITE 1 PARA LER PADRÃO COM KMP        ");
            System.out.println("        * DIGITE 2 PARA LER PADRÃO COM BOYER-MOORE        ");
            System.out.println("        * DIGITE 0 PARA SAIR         ");
            System.out.println("--------------------------------------------------------------");
            System.out.println();

            System.out.print("* ENTRADA: ");
            int menu = Integer.parseInt(sc.nextLine());

            switch (menu) {
                case 1:
                    System.out.print("* DIGITE O PADRÃO A SER LIDO (COM KMP): ");
                    padrao = sc.nextLine();

                    System.out.print("* DIGITE O TIPO DE PADRÃO (1 - TEXTO, 2 - INTEIRO, 3 - FLOAT): ");
                    tipo = sc.nextInt();
                    sc.nextLine();

                    switch (tipo) {
                        case 1:
                            posicoes = KMP.buscarPadrao(padrao, "animeDataBase.db");
                            break;
                        case 2:
                            int valorInt = Integer.parseInt(padrao);
                            byte[] padraoNum = ByteBuffer.allocate(4).putInt(valorInt).array();
                            posicoes = KMP.buscarPadrao(padraoNum, "animeDataBase.db");
                            break;
                        case 3:
                            float valorFloat = Float.parseFloat(padrao);
                            padraoNum = ByteBuffer.allocate(4).putFloat(valorFloat).array();
                            posicoes = KMP.buscarPadrao(padraoNum, "animeDataBase.db");
                            break;
                        default:
                            System.out.println("* TIPO INVÁLIDO! DIGITE 1, 2 OU 3.");
                            continue;
                    }

                    if (posicoes.isEmpty()) {
                        System.out.println("* PADRÃO NÃO ENCONTRADO!");
                    } else {
                        System.out.println("* PADRÃO ENCONTRADO NAS POSIÇÕES: " + posicoes);

                        // Chama a função que retorna os ids válidos (sem lápide)
                        ids = arq.resgatarIdsValidos(posicoes, "animeDataBase.db");

                        if (ids.isEmpty()) {
                            System.out.println("* NENHUM ANIME VÁLIDO ENCONTRADO NAS POSIÇÕES!");
                        } else {
                            System.out.println();
                            System.out.println("* IDS DOS ANIMES ENCONTRADOS: " + ids);
                            System.out.println("* LENDO ANIMES VÁLIDOS...");
                            System.out.println("--------------------------------------------------------------");
                            for (int id : ids) {
                                arq.read(id, 1, arvore, hash); // Chama o método read para cada id válido
                            }
                            System.out.println();
                            System.out.println("* ANIMES VÁLIDOS LIDOS COM SUCESSO!");
                        }
                    }
                    break;

                case 2:
                    System.out.print("* DIGITE O PADRÃO A SER LIDO (COM BOYER-MOORE): ");
                    padrao = sc.nextLine();

                    System.out.print("* DIGITE O TIPO DE PADRÃO (1 - TEXTO, 2 - INTEIRO, 3 - FLOAT): ");
                    tipo = sc.nextInt();
                    sc.nextLine();

                    switch (tipo) {
                        case 1:
                            posicoes = BoyerMoore.buscarPadrao(padrao, "animeDataBase.db");
                            break;
                        case 2:
                            int valorInt = Integer.parseInt(padrao);
                            byte[] padraoNum = ByteBuffer.allocate(4).putInt(valorInt).array();
                            posicoes = BoyerMoore.buscarPadrao(padraoNum, "animeDataBase.db");
                            break;
                        case 3:
                            float valorFloat = Float.parseFloat(padrao);
                            padraoNum = ByteBuffer.allocate(4).putFloat(valorFloat).array();
                            posicoes = BoyerMoore.buscarPadrao(padraoNum, "animeDataBase.db");
                            break;
                        default:
                            System.out.println("* TIPO INVÁLIDO! DIGITE 1, 2 OU 3.");
                            continue;
                    }

                    if (posicoes.isEmpty()) {
                        System.out.println("* PADRÃO NÃO ENCONTRADO!");
                    } else {
                        System.out.println("* PADRÃO ENCONTRADO NAS POSIÇÕES: " + posicoes);

                         // Chama a função que retorna os ids válidos (sem lápide)
                        ids = arq.resgatarIdsValidos(posicoes, "animeDataBase.db");

                        if (ids.isEmpty()) {
                            System.out.println("* NENHUM ANIME VÁLIDO ENCONTRADO NAS POSIÇÕES!");
                        } else {
                            System.out.println();
                            System.out.println("* IDS DOS ANIMES ENCONTRADOS: " + ids);
                            System.out.println("* LENDO ANIMES VÁLIDOS...");
                            System.out.println("--------------------------------------------------------------");
                            for (int id : ids) {
                                arq.read(id, 1, arvore, hash); // Chama o método read para cada id válido
                            }
                            System.out.println();
                            System.out.println("* ANIMES VÁLIDOS LIDOS COM SUCESSO!");
                        }
                    }
                    break;
                case 0:
                    keepGoing = false;
                    break;
                default:
                    System.out.println("* NÃO EXISTE ESTA OPÇÃO.");
                    break;
            }
        }

        arvore.close();
        hash.close();
        arq.close();
    }
}