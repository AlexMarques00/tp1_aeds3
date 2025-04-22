import java.io.IOException;
import java.io.RandomAccessFile;

public class HashExtensivo {
    RandomAccessFile arqDiretorio;
    RandomAccessFile arqCestos;
    int quantidadeDadosPorCesto;
    Diretorio diretorio;

    public HashExtensivo(int n) throws Exception {
        arqDiretorio = new RandomAccessFile("Diretorio.db", "rw");
        arqCestos = new RandomAccessFile("Bucket.db", "rw");

        quantidadeDadosPorCesto = n;

        // Se o diretório ou os cestos estiverem vazios, cria um novo diretório e lista
        // de cestos
        if (arqDiretorio.length() == 0 || arqCestos.length() == 0) {
            diretorio = new Diretorio();
            diretorio.atualizaEndereco(0, 0); // Garante endereço inicial válido

            Bucket c = new Bucket(quantidadeDadosPorCesto);
            c.profundidadeLocal = 0; // Define profundidade local explicitamente

            arqDiretorio.seek(0);
            arqDiretorio.write(diretorio.toByteArray());

            arqCestos.seek(0);
            arqCestos.write(c.toByteArray());
        }
    }

    public boolean create(int id, long offset) throws IOException, Exception {
        byte[] bd = new byte[(int) arqDiretorio.length()];
        arqDiretorio.seek(0);
        arqDiretorio.read(bd);
        diretorio = new Diretorio();
        diretorio.fromByteArray(bd);

        // Identifica a hash do diretório,
        int i = diretorio.hash(id);
        long enderecoCesto = diretorio.endereço(i);

        // Recupera o cesto
        Bucket c = new Bucket(quantidadeDadosPorCesto);
        byte[] ba = new byte[c.size()];
        arqCestos.seek(enderecoCesto);
        arqCestos.read(ba);
        c.fromByteArray(ba);

        // Testa se a chave já não existe no cesto
        if (c.read(id) != -1)
            throw new Exception("Elemento já existe");

        // Testa se o cesto já não está cheio
        // Se não estiver, create o par de chave e dado
        if (!c.full()) {
            // Insere a chave no cesto e o atualiza
            c.create(id, offset);
            arqCestos.seek(enderecoCesto);
            arqCestos.write(c.toByteArray());
            return true;
        }

        // Duplica o diretório
        byte pl = c.profundidadeLocal;
        if (pl == diretorio.getProfundidadeGlobal()) {
            diretorio.duplica();
            // Atualiza o diretório no arquivo IMEDIATAMENTE
            arqDiretorio.seek(0);
            arqDiretorio.write(diretorio.toByteArray());
        }

        // Cria novos cestos
        Bucket c1 = new Bucket(quantidadeDadosPorCesto, pl + 1);
        Bucket c2 = new Bucket(quantidadeDadosPorCesto, pl + 1);

        // Redistribui elementos
        int mask = (int) Math.pow(2, pl);

        for (int j = 0; j < c.quantidade; j++) {
            int chave = c.elementos.get(j);
            long offsetOriginal = c.offsets.get(j);
            int novoHash = diretorio.hash2(chave, c.profundidadeLocal + 1);

            if (novoHash % (2 * mask) < mask) { // Verificação par/ímpar
                c1.create(chave, offsetOriginal);
            } else {
                c2.create(chave, offsetOriginal);
            }
        }

        // Atualiza diretório
        long novoEndereco = arqCestos.length();

        // Atualiza apenas as entradas que apontavam para o bucket original
        for (int j = 0; j < diretorio.getTamanho(); j++) {
            if (diretorio.endereço(j) == enderecoCesto) {
                // Verifica o bit na posição pl (profundidade local original)
                if ((j & mask) == 0) {
                    diretorio.atualizaEndereco(j, enderecoCesto); // Aponta para c1
                } else {
                    diretorio.atualizaEndereco(j, novoEndereco); // Aponta para c2
                }
            }
        }

        // Persiste alterações
        arqDiretorio.seek(0);
        arqDiretorio.write(diretorio.toByteArray());

        // Escreve os novos cestos atualizados
        arqCestos.seek(enderecoCesto);
        arqCestos.write(c1.toByteArray());

        long posNovoBucket = arqCestos.length();
        arqCestos.seek(posNovoBucket);
        arqCestos.write(c2.toByteArray());

        return create(id, offset);
    }

    public long read(int id) throws Exception {
        // Carrega o diretório
        byte[] bd = new byte[(int) arqDiretorio.length()];

        arqDiretorio.seek(0);
        arqDiretorio.read(bd);
        diretorio = new Diretorio();
        diretorio.fromByteArray(bd);

        // Identifica a hash do diretório,
        int i = diretorio.hash(id);

        // Recupera o cesto
        long enderecoCesto = diretorio.endereço(i);
        Bucket c = new Bucket(quantidadeDadosPorCesto);
        byte[] ba = new byte[c.size()];
        arqCestos.seek(enderecoCesto);
        arqCestos.read(ba);
        c.fromByteArray(ba);

        return c.read(id);
    }

    public boolean update(int id, long offset) throws Exception {
        // Carrega o diretório
        byte[] bd = new byte[(int) arqDiretorio.length()];
        arqDiretorio.seek(0);
        arqDiretorio.read(bd);
        diretorio = new Diretorio();
        diretorio.fromByteArray(bd);

        // Identifica a hash do diretório,
        int i = diretorio.hash(id);

        // Recupera o cesto
        long enderecoCesto = diretorio.endereço(i);
        Bucket c = new Bucket(quantidadeDadosPorCesto);
        byte[] ba = new byte[c.size()];
        arqCestos.seek(enderecoCesto);
        arqCestos.read(ba);
        c.fromByteArray(ba);

        // atualiza o dado
        if (!c.update(id, offset))
            return false;

        // Atualiza o cesto
        arqCestos.seek(enderecoCesto);
        arqCestos.write(c.toByteArray());
        return true;

    }

    public boolean delete(int id) throws IOException, Exception {
        // Carrega o diretório
        byte[] bd = new byte[(int) arqDiretorio.length()];
        arqDiretorio.seek(0);
        arqDiretorio.read(bd);
        diretorio = new Diretorio();
        diretorio.fromByteArray(bd);

        // Identifica a hash do diretório,
        int i = diretorio.hash(id);

        // Recupera o cesto
        long enderecoCesto = diretorio.endereço(i);
        Bucket c = new Bucket(quantidadeDadosPorCesto);
        byte[] ba = new byte[c.size()];
        arqCestos.seek(enderecoCesto);
        arqCestos.read(ba);
        c.fromByteArray(ba);

        // delete a chave
        if (!c.delete(id))
            return false;

        // Atualiza o cesto
        arqCestos.seek(enderecoCesto);
        arqCestos.write(c.toByteArray());
        return true;
    }

    public void print() {
        try {
            byte[] bd = new byte[(int) arqDiretorio.length()];
            arqDiretorio.seek(0);
            arqDiretorio.read(bd);
            diretorio = new Diretorio();
            diretorio.fromByteArray(bd);

            System.out.println("\nDIRETÓRIO ------------------");
            System.out.println(diretorio);

            System.out.println("\nCESTOS ---------------------");
            arqCestos.seek(0);

            while (arqCestos.getFilePointer() != arqCestos.length()) {
                System.out.println("Endereço: " + arqCestos.getFilePointer());

                Bucket c = new Bucket(quantidadeDadosPorCesto);
                byte[] ba = new byte[c.size()];
                arqCestos.read(ba);
                c.fromByteArray(ba);

                System.out.println(c + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() throws Exception {
        arqDiretorio.close();
        arqCestos.close();
    }
}
