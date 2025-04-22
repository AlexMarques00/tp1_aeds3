import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class ArvoreBMais {
    public RandomAccessFile arq;
    private int ordem = 0; // Número máximo de filhos que uma página pode conter
    private int maxElementos = 0; // Variável igual a ordem - 1 para facilitar a clareza do código
    private int maxFilhos = 0; // Variável igual a ordem para facilitar a clareza do código

    // Variáveis usadas nas funções recursivas (já que não é possível passar valores por referência)
    private Animes elemAux;
    private long enderecoAux;
    private int paginaAux;
    private boolean cresceu;
    private boolean diminuiu;

    public ArvoreBMais (String path, int ordem) throws Exception {
         this.ordem = ordem;
         this.maxElementos = ordem - 1;
         this.maxFilhos = ordem;
 
         // Abre (ou cria) o arquivo, escrevendo uma raiz empty, se necessário.
         arq = new RandomAccessFile(path, "rw");
         if (arq.length() < 16) {
             arq.writeInt(-1); // raiz empty
             arq.writeInt(-1); // ponteiro lista excluídos
         }
    }

    // Testa se a árvore está empty. Uma árvore empty é identificada pela raiz == -1
    public boolean empty() throws IOException {
        int raiz;
        arq.seek(0);
        raiz = arq.readInt();
        return raiz == -1;
    }

    public boolean create(Animes elem, long enderecoDB) throws Exception {

        // Carrega a raiz
        arq.seek(0);
        int pagina;
        pagina = arq.readInt();

        // O processo de inclusão permite que os valores passados como referência
        // sejam substituídos por outros valores, para permitir a divisão de páginas
        // e crescimento da árvore. Assim, são usados os valores globais elemAux
        // e chave2Aux. Quando há uma divisão, as chaves promovidas são armazenadas
        // nessas variáveis.
        // Armazena tanto o elemento quanto seu endereço no DB
        elemAux = elem.clone();
        enderecoAux = enderecoDB;
        paginaAux = -1;
        cresceu = false;

        // Chamada recursiva para a inserção do par de chaves
        boolean inserido = create1(pagina);

        // Testa a necessidade de criação de uma nova raiz.
        if (cresceu) {

            // Cria a nova página que será a raiz. O ponteiro esquerdo da raiz
            // será a raiz antiga e o seu ponteiro direito será para a nova página.
            Pagina novaPagina = new Pagina(ordem);
            novaPagina.elementos = new ArrayList<>(this.maxElementos);
            novaPagina.offsets = new ArrayList<>(maxElementos);
            novaPagina.elementos.add(elemAux.getId());
            novaPagina.offsets.add(enderecoAux);
            novaPagina.filhos = new ArrayList<>(this.maxFilhos);
            novaPagina.filhos.add(pagina);
            novaPagina.filhos.add(paginaAux);

            // Acha o espaço em disco. Testa se há páginas excluídas.
            arq.seek(4);
            int end = arq.readInt();
            if(end==-1) {
                end = (int) arq.length();
            } else { // reusa um endereço e atualiza a lista de excluídos no cabeçalho
                arq.seek(end);
                Pagina pa_excluida = new Pagina(ordem);
                byte[] buffer = new byte[pa_excluida.TAMANHO_PAGINA];
                arq.read(buffer);
                pa_excluida.fromByteArray(buffer);
                arq.seek(4);
                arq.writeInt((int) pa_excluida.proxima);
            }

            arq.seek(end);
            int raiz = (int) arq.getFilePointer();
            arq.write(novaPagina.toByteArray());
            arq.seek(0);
            arq.writeInt(raiz);
            inserido = true;
        }

        return inserido;
    }

    // As inclusões são sempre feitas em uma folha.
    private boolean create1(long pagina) throws Exception {

        // Testa se passou para o filho de uma página folha. Nesse caso,
        // inicializa as variáveis globais de controle.
        if (pagina == -1) {
            cresceu = true;
            paginaAux = -1;
            return false;
        }

        // Lê a página passada como referência
        arq.seek(pagina);
        Pagina pa = new Pagina(ordem);
        byte[] buffer = new byte[pa.TAMANHO_PAGINA];
        arq.read(buffer);
        pa.fromByteArray(buffer);

        // Busca o próximo ponteiro de descida. Como pode haver repetição
        // da primeira chave, a segunda também é usada como referência.
        // Nesse primeiro passo, todos os pares menores são ultrapassados.
        int i = 0;
        while (i < pa.elementos.size() && (elemAux.compareTo(pa.elementos.get(i)) > 0)) {
            i++;
        }

        // Testa se o registro já existe em uma folha. Se isso acontecer, então
        // a inclusão é cancelada.
        if (i < pa.elementos.size() && pa.filhos.get(0) == -1 && elemAux.compareTo(pa.elementos.get(i)) == 0) {
            cresceu = false;
            return false;
        }

        // Continua a busca recursiva por uma nova página. A busca continuará até o
        // filho inexistente de uma página folha ser alcançado.
        boolean inserido;
        if (i == pa.elementos.size() || elemAux.compareTo(pa.elementos.get(i)) < 0)
            inserido = create1(pa.filhos.get(i));
        else
            inserido = create1(pa.filhos.get(i + 1));

        // A partir deste ponto, as chamadas recursivas já foram encerradas.
        // Assim, o próximo código só é executado ao retornar das chamadas recursivas.
        // A inclusão já foi resolvida por meio de uma das chamadas recursivas. Nesse
        // caso, apenas retorna para encerrar a recursão.
        // A inclusão pode ter sido resolvida porque o par de chaves já existia
        // (inclusão inválida)
        // ou porque o novo elemento coube em uma página existente.
        if (!cresceu)
            return inserido;

        // Se tiver espaço na página, faz a inclusão nela mesmo
        if (pa.elementos.size() < maxElementos) {

            // Puxa todos elementos para a direita, começando do último
            // para gerar o espaço para o novo elemento e insere o novo elemento
            pa.elementos.add(i, elemAux.getId());
            // Só adiciona offset se for folha
            if (pa.filhos.get(0) == -1) {  // Se for folha
                while (pa.offsets.size() < pa.elementos.size()) {
                    pa.offsets.add(-1L); // Preenche com valores padrão se necessário
                }
                pa.offsets.set(i, enderecoAux); 
            }

            pa.filhos.add(i + 1, paginaAux);
            // Escreve a página atualizada no arquivo
            arq.seek(pagina);
            arq.write(pa.toByteArray());

            // Encerra o processo de crescimento e retorna
            cresceu = false;
            return true;
        }

        // O elemento não cabe na página. A página deve ser dividida e o elemento
        // do meio deve ser promovido (sem retirar a referência da folha).
        // Cria uma nova página
        Pagina np = new Pagina(ordem);

        // Move a metade superior dos elementos para a nova página,
        // considerando que maxElementos pode ser ímpar
        int meio = maxElementos / 2;
        np.filhos.add(pa.filhos.get(meio)); // COPIA o primeiro ponteiro
        for (int j = 0; j < (maxElementos - meio); j++) {
            np.elementos.add(pa.elementos.remove(meio)); // MOVE os elementos
            if (pa.filhos.get(0) == -1) {  // Se for folha
                np.offsets.add(pa.offsets.remove(meio));  // MOVE endereços
            }
            np.filhos.add(pa.filhos.remove(meio + 1)); // MOVE os demais ponteiros
        }

        // Testa o lado de inserção
        // Caso 1 - Novo registro deve ficar na página da esquerda
        if (i <= meio) {
            pa.elementos.add(i, elemAux.getId());
            if (pa.filhos.get(0) == -1) {
                pa.offsets.add(i, enderecoAux);
            }
            pa.filhos.add(i + 1, paginaAux);

            // Se a página for folha, seleciona o primeiro elemento da página
            // da direita para ser promovido, mantendo-o na folha
            if (pa.filhos.get(0) == -1) {
                elemAux.setId(np.elementos.get(0));
                enderecoAux = np.offsets.get(0);
            // caso contrário, promove o maior elemento da página esquerda
            // removendo-o da página
            } else {
                elemAux.setId(pa.elementos.remove(pa.elementos.size() - 1));
                pa.filhos.remove(pa.filhos.size() - 1);
            }
        }

        // Caso 2 - Novo registro deve ficar na página da direita
        else {
            int j = maxElementos - meio;
            while (j > 0 && elemAux.compareTo(np.elementos.get(j - 1)) < 0) {
                j--;
            }
            np.elementos.add(j, elemAux.getId());
            if (pa.filhos.get(0) == -1) {
                np.offsets.add(j, enderecoAux);
            }
            np.filhos.add(j + 1, paginaAux);

            // Seleciona o primeiro elemento da página da direita para ser promovido
            elemAux.setId(np.elementos.get(0));

            // Se não for folha, remove o elemento promovido da página
            if (pa.filhos.get(0) != -1) {
                np.elementos.remove(0);
                np.filhos.remove(0);
            } else {
                enderecoAux = np.offsets.get(0);
            }
        }

        // Obtém um endereço para a nova página (página excluída ou fim do arquivo)
        arq.seek(4);
        int end = arq.readInt();
        if(end==-1) {
            end =(int) arq.length();
        } else { // reusa um endereço e atualiza a lista de excluídos no cabeçalho
            arq.seek(end);
            Pagina pa_excluida = new Pagina(ordem);
            buffer = new byte[pa_excluida.TAMANHO_PAGINA];
            arq.read(buffer);
            pa_excluida.fromByteArray(buffer);
            arq.seek(4);
            arq.writeInt(pa_excluida.proxima);
        }

        // Se a página era uma folha e apontava para outra folha,
        // então atualiza os ponteiros dessa página e da página nova
        if (pa.filhos.get(0) == -1) {
            np.proxima = pa.proxima;
            pa.proxima = end;
        }

        // Grava as páginas no arquivo
        paginaAux = end;
        arq.seek(paginaAux);
        arq.write(np.toByteArray());

        arq.seek(pagina);
        arq.write(pa.toByteArray());

        return true;
    }

   // Busca por um elemento e retorna o endereço no arquivo DB
    public long read(int id) throws Exception {
        // Recupera a raiz da árvore
        arq.seek(0);
        int raiz = arq.readInt();

        // Executa a busca recursiva
        if (raiz != -1)
            return read1(id, raiz);
        else {
            return -1; // Retorna -1 se a árvore estiver vazia
        }
    }

    // Método recursivo de busca
    private long read1(int id, long pagina) throws Exception {
        // Caso base: página inexistente
        if (pagina == -1) {
            return -1;
        }

        // Lê a página atual
        arq.seek(pagina);
        Pagina pa = new Pagina(ordem);
        byte[] buffer = new byte[pa.TAMANHO_PAGINA];
        arq.read(buffer);
        pa.fromByteArray(buffer);

        // Encontra a posição onde a chave deveria estar
        int i = 0;
        while (i < pa.elementos.size() && id > pa.elementos.get(i)) {
            i++;
        }

        // Se for folha, verifica se encontrou o elemento
        if (pa.filhos.get(0) == -1) {
            if (i < pa.elementos.size() && id == pa.elementos.get(i)) {
                // Retorna o offset correspondente ao elemento encontrado
                return pa.offsets.get(i);
            }
            
            // Verifica se pode estar na próxima folha (para chaves repetidas)
            if (i == pa.elementos.size() && pa.proxima != -1) {
                return read1(id, pa.proxima);
            }
            
            return -1; // Não encontrado
        } 
        // Se for nó interno, continua a busca recursiva
        else {
            if (i == pa.elementos.size() || id <= pa.elementos.get(i)) {
                return read1(id, pa.filhos.get(i));
            } else {
                return read1(id, pa.filhos.get(i + 1));
            }
        }
    }

    public boolean delete(int id) throws Exception {

        // Encontra a raiz da árvore
        arq.seek(0);
        int pagina;
        pagina = arq.readInt();

        // variável global de controle da redução do tamanho da árvore
        diminuiu = false;

        // Chama recursivamente a exclusão de registro (na elemAux e no
        // chave2Aux) passando uma página como referência
        boolean excluido = delete1(id, pagina);

        // Se a exclusão tiver sido possível e a página tiver reduzido seu tamanho,
        // por meio da fusão das duas páginas filhas da raiz, elimina essa raiz
        if (excluido && diminuiu) {

            // Lê a raiz
            arq.seek(pagina);
            Pagina pa = new Pagina(ordem);
            byte[] buffer = new byte[pa.TAMANHO_PAGINA];
            arq.read(buffer);
            pa.fromByteArray(buffer);

            // Se a página tiver 0 elementos, apenas atualiza o ponteiro para a raiz,
            // no cabeçalho do arquivo, para o seu primeiro filho e insere a raiz velha
            // na lista de páginas excluídas
            if (pa.elementos.size() == 0) {
                arq.seek(0);
                arq.writeInt(pa.filhos.get(0));

                arq.seek(4);
                int end = arq.readInt();  // cabeça da lista de páginas excluídas
                pa.proxima = end;
                arq.seek(4);
                arq.writeInt(pagina);
                arq.seek(pagina);
                arq.write(pa.toByteArray());
            }
        }

        return excluido;
    }

    // As exclusões são sempre feitas em folhas e a fusão é propagada para cima.
    private boolean delete1(int id, long pagina) throws Exception {

        // Declaração de variáveis
        boolean excluido = false;
        int diminuido;

        // Testa se o registro não foi encontrado na árvore, ao alcançar uma folha
        // inexistente (filho de uma folha real)
        if (pagina == -1) {
            diminuiu = false;
            return false;
        }

        // Lê o registro da página no arquivo
        arq.seek(pagina);
        Pagina pa = new Pagina(ordem);
        byte[] buffer = new byte[pa.TAMANHO_PAGINA];
        arq.read(buffer);
        pa.fromByteArray(buffer);

        // Encontra a página em que o par de chaves está presente
        // Nesse primeiro passo, salta todas os pares de chaves menores
        int i = 0;
        while (i < pa.elementos.size() && id > pa.elementos.get(i)) {
            i++;
        }

        // Chaves encontradas em uma folha
        if (i < pa.elementos.size() && pa.filhos.get(0) == -1 && id == pa.elementos.get(i)) {
        
            // Remove elemento + offset + filho
            pa.elementos.remove(i);
            pa.offsets.remove(i);  // Garantir remoção do offset correspondente
            pa.filhos.remove(i + 1);

            // Atualiza o registro da página no arquivo
            arq.seek(pagina);
            arq.write(pa.toByteArray());

            // Se a página contiver menos elementos do que o mínimo necessário,
            // indica a necessidade de fusão de páginas
            diminuiu = pa.elementos.size() < maxElementos / 2;
            return true;
        }

        // Se a chave não tiver sido encontrada (observar o return true logo acima),
        // continua a busca recursiva por uma nova página. A busca continuará até o
        // filho inexistente de uma página folha ser alcançado.
        // A variável diminuído mantem um registro de qual página eventualmente
        // pode ter ficado com menos elementos do que o mínimo necessário.
        // Essa página será filha da página atual
        if (i == pa.elementos.size() || id < pa.elementos.get(i)) {
            excluido = delete1(id, pa.filhos.get(i));
            diminuido = i;
        } else {
            excluido = delete1(id, pa.filhos.get(i + 1));
            diminuido = i + 1;
        }

        // A partir deste ponto, o código é executado após o retorno das chamadas
        // recursivas do método

        // Testa se há necessidade de fusão de páginas
        if (diminuiu) {

            // Carrega a página filho que ficou com menos elementos do
            // do que o mínimo necessário
            int paginaFilho = pa.filhos.get(diminuido);
            Pagina pFilho = new Pagina(ordem);
            arq.seek(paginaFilho);
            arq.read(buffer);
            pFilho.fromByteArray(buffer);

            // Cria uma página para o irmão (da direita ou esquerda)
            int paginaIrmaoEsq = -1, paginaIrmaoDir = -1;
            Pagina pIrmaoEsq = null, pIrmaoDir = null; // inicializados com null para controle de existência

            // Carrega os irmãos (que existirem)
            if (diminuido > 0) { // possui um irmão esquerdo, pois não é a primeira filho do pai
                paginaIrmaoEsq = pa.filhos.get(diminuido - 1);
                pIrmaoEsq = new Pagina(ordem);
                arq.seek(paginaIrmaoEsq);
                arq.read(buffer);
                pIrmaoEsq.fromByteArray(buffer);
            }
            if (diminuido < pa.elementos.size()) { // possui um irmão direito, pois não é o último filho do pai
                paginaIrmaoDir = pa.filhos.get(diminuido + 1);
                pIrmaoDir = new Pagina(ordem);
                arq.seek(paginaIrmaoDir);
                arq.read(buffer);
                pIrmaoDir.fromByteArray(buffer);
            }

            // Verifica se o irmão esquerdo existe e pode ceder algum elemento
            if (pIrmaoEsq != null && pIrmaoEsq.elementos.size() > maxElementos / 2) {

                // Se for folha, copia o elemento do irmão, já que o do pai será extinto ou
                // repetido
                if (pFilho.filhos.get(0) == -1) {
                    pFilho.elementos.add(0, pIrmaoEsq.elementos.remove(pIrmaoEsq.elementos.size() - 1));
                    pFilho.offsets.add(0, pIrmaoEsq.offsets.remove(pIrmaoEsq.offsets.size() - 1));
                    pa.elementos.set(diminuido - 1, pFilho.elementos.get(0));
                // Se não for folha, desce o elemento do pai
                } else {
                    pFilho.elementos.add(0, pa.elementos.get(diminuido - 1));
                    pa.elementos.set(diminuido - 1, pIrmaoEsq.elementos.remove(pIrmaoEsq.elementos.size() - 1));
                }

                // Reduz o elemento no irmão
                pFilho.filhos.add(0, pIrmaoEsq.filhos.remove(pIrmaoEsq.filhos.size() - 1));
            }

            // Senão, verifica se o irmão direito existe e pode ceder algum elemento
            else if (pIrmaoDir != null && pIrmaoDir.elementos.size() > maxElementos / 2) {
                // Se for folha
                if (pFilho.filhos.get(0) == -1) {

                    // move o elemento do irmão
                    pFilho.elementos.add(pIrmaoDir.elementos.remove(0));
                    pFilho.offsets.add(pIrmaoDir.offsets.remove(0)); 

                    // sobe o próximo elemento do irmão
                    pa.elementos.set(diminuido, pIrmaoDir.elementos.get(0));
                }

                // Se não for folha, rotaciona os elementos
                else {
                    // Copia o elemento do pai, com o ponteiro esquerdo do irmão
                    pFilho.elementos.add(pa.elementos.get(diminuido));
                    // Sobe o elemento esquerdo do irmão para o pai
                    pa.elementos.set(diminuido, pIrmaoDir.elementos.remove(0));
                }
                pFilho.filhos.add(pIrmaoDir.filhos.remove(0));
            }

            // Senão, faz a fusão com o irmão esquerdo, se ele existir
            else if (pIrmaoEsq != null) {
                // Se a página reduzida não for folha, então o elemento
                // do pai deve descer para o irmão
                if (pFilho.filhos.get(0) != -1) {
                    pIrmaoEsq.elementos.add(pa.elementos.remove(diminuido - 1));
                }
                pIrmaoEsq.elementos.addAll(pFilho.elementos);
                // Senão, apenas remove o elemento do pai
                if (pFilho.filhos.get(0) == -1) {
                    pIrmaoEsq.offsets.addAll(pFilho.offsets);
                    pIrmaoEsq.proxima = pFilho.proxima;
                }
                pIrmaoEsq.filhos.addAll(pFilho.filhos);
                pa.filhos.remove(diminuido); // remove o ponteiro para a própria página

                // Atualiza lista de excluídos
                arq.seek(4);
                pFilho.proxima = arq.readInt();
                arq.seek(4);
                arq.writeInt(paginaFilho);
            }

            // Senão, faz a fusão com o irmão direito, assumindo que ele existe
            else if (pIrmaoDir != null) {
                // Se a página reduzida não for folha, então o elemento
                // do pai deve descer para o irmão
                if (pFilho.filhos.get(0) != -1) {
                    pFilho.elementos.add(pa.elementos.remove(diminuido));
                }
                pFilho.elementos.addAll(pIrmaoDir.elementos);
                // Senão, apenas remove o elemento do pai
                if (pIrmaoDir.filhos.get(0) == -1) {
                    pFilho.offsets.addAll(pIrmaoDir.offsets); // Offsets
                    pFilho.proxima = pIrmaoDir.proxima;
                }
                pFilho.filhos.addAll(pIrmaoDir.filhos);
                pa.filhos.remove(diminuido + 1); // remove o ponteiro para o irmão direito

                // Atualiza lista de excluídos
                arq.seek(4);
                pIrmaoDir.proxima = arq.readInt();
                arq.seek(4);
                arq.writeInt(paginaIrmaoDir);
            }

            // testa se o pai também ficou sem o número mínimo de elementos
            diminuiu = pa.elementos.size() < maxElementos / 2;

            // Atualiza os demais registros
            arq.seek(pagina);
            arq.write(pa.toByteArray());
            arq.seek(paginaFilho);
            arq.write(pFilho.toByteArray());
            if (pIrmaoEsq != null) {
                arq.seek(paginaIrmaoEsq);
                arq.write(pIrmaoEsq.toByteArray());
            }
            if (pIrmaoDir != null) {
                arq.seek(paginaIrmaoDir);
                arq.write(pIrmaoDir.toByteArray());
            }
        }
        return excluido;
    }

    public void print() throws Exception {
        long raiz;
        arq.seek(0);
        raiz = arq.readInt();

        System.out.println("Raiz: " + String.format("%04d", raiz));
        
        if (raiz != -1)
            print1(raiz);
        System.out.println();
    }

    // Impressão recursiva
    private void print1(long pagina) throws Exception {
        if (pagina == -1)
            return;
        int i;
    
        // Lê a página
        arq.seek(pagina);
        Pagina pa = new Pagina(ordem);
        byte[] buffer = new byte[pa.TAMANHO_PAGINA];
        arq.read(buffer);
        pa.fromByteArray(buffer);
    
        // Imprime a página
        String endereco = String.format("%04d", pagina);
        System.out.print(endereco + "  " + pa.elementos.size() + ":"); // endereço e número de elementos
        
        boolean isFolha = pa.filhos.get(0) == -1;
        
        for (i = 0; i < pa.elementos.size(); i++) {
            System.out.print("(" + String.format("%04d", pa.filhos.get(i)) + ") " + pa.elementos.get(i));
            
            // Mostra offset se for folha
            if (isFolha && i < pa.offsets.size()) {
                System.out.print("@" + pa.offsets.get(i));
            }
            System.out.print(" ");
        }
        
        if (i > 0)
            System.out.print("(" + String.format("%04d", pa.filhos.get(i)) + ")");
        else
            System.out.print("(-001)");
        
        for (; i < maxElementos; i++) {
            System.out.print(" ------- (-001)");
        }
        
        if (pa.proxima == -1)
            System.out.println();
        else
            System.out.println(" --> (" + String.format("%04d", pa.proxima) + ")");
    
        // Chama recursivamente cada filho, se não for folha
        if (!isFolha) {
            for (i = 0; i < pa.elementos.size(); i++)
                print1(pa.filhos.get(i));
            print1(pa.filhos.get(i));
        }
    }

    public void close() throws Exception {
        arq.close();
    }
}
