import java.io.IOException;
import java.io.RandomAccessFile;

public class Arquivo {
    public RandomAccessFile arq;
    
    public Arquivo(String path) throws Exception{
        arq = new RandomAccessFile(path, "rw");
    }
    
    public void create(Animes anime) throws Exception{
        arq.seek(arq.length());
        // tam + lapide + objeto
        byte[] objeto = anime.toByteArray();
        
        arq.writeChar(' ');
        arq.writeShort(objeto.length);
        arq.write(objeto);
    }

    public void read(int id) throws IOException {
        Animes anime = new Animes();
        char lapide;
        boolean resp = false;
        boolean resp2 = false;
    
        // Pula Cabeçalho
        arq.seek(4);

        // Loop para ler todos os registros
        while (arq.getFilePointer() < arq.length()) {
            lapide = arq.readChar();
            int tamanhoRegistro = arq.readShort();
    
            // Lê os bytes do registro
            byte[] ba = new byte[tamanhoRegistro];
            arq.read(ba);
            anime.fromByteArray(ba);
    
            // Verifica se o registro não está marcado como excluído e se o ID corresponde
            if (lapide != '*' && anime.getId() == id) {
                anime.write();
                resp2 = true;
                break; // Encerra o loop após encontrar o registro desejado
            } else if (lapide == '*' && anime.getId() == id) {
                resp = true;
            }
        }

        if (resp2 == false) {
            System.out.println("* OBJETO EXCLUÍDO OU NÃO EXISTE!");
        } else if (resp == true && id < 18495) {
            System.out.println("* OBJETO DESLOCADO PARA O FIM DO ARQUIVO!");
        }
    }

    public boolean update(Animes novo_anime) throws Exception {
        Animes anime = new Animes();
        char lapide;
    
        // Pula Cabeçalho
        arq.seek(4);
    
        // Loop para ler todos os registros
        while (arq.getFilePointer() < arq.length()) {
            long posicaoInicialRegistro = arq.getFilePointer();
    
            lapide = arq.readChar();
            int tamanhoRegistro = arq.readShort();
    
            // Lê os bytes do registro
            byte[] ba = new byte[tamanhoRegistro];
            arq.read(ba);
            anime.fromByteArray(ba);
    
            // Verifica se o registro não está marcado como excluído e se o ID corresponde
            if (lapide != '*' && anime.getId() == novo_anime.getId()) {
                byte[] objeto1 = anime.toByteArray();
                byte[] objeto2 = novo_anime.toByteArray();
    
                if (objeto2.length <= objeto1.length) {
                    // Atualiza objeto no mesmo local
                    arq.seek(posicaoInicialRegistro);
                    arq.writeChar(' ');
                    arq.writeShort(tamanhoRegistro);
                    arq.write(objeto2);
                    System.out.println("* OBJETO ATUALIZADO NO MESMO LOCAL COM SUCESSO!");
                } else {
                    // Marca o registro antigo como excluído
                    arq.seek(posicaoInicialRegistro);
                    arq.writeChar('*');
    
                    // Move o novo objeto para o final
                    arq.seek(arq.length());
                    arq.writeChar(' ');
                    arq.writeShort(objeto2.length);
                    arq.write(objeto2);
    
                    System.out.println("* OBJETO ATUALIZADO E DESLOCADO PARA O FINAL DO ARQUIVO!");
                }
                return true;
            }
        }
        return false;
    }

    public boolean delete(int id) throws Exception{
        Animes anime = new Animes();
        char lapide;
    
        // Pula Cabeçalho
        arq.seek(4);

        // Loop para ler todos os registros
        while (arq.getFilePointer() < arq.length()) {
            long posicaoInicialRegistro = arq.getFilePointer(); 

            lapide = arq.readChar(); 
            int tamanhoRegistro = arq.readShort(); 

            // Lê os bytes do registro
            byte[] ba = new byte[tamanhoRegistro];
            arq.read(ba);
            anime.fromByteArray(ba);

            // Verifica se o registro não está marcado como excluído e se o ID corresponde
            if (lapide != '*' && anime.getId() == id) {
                arq.seek(posicaoInicialRegistro);
                arq.writeChar('*');
                anime.write();
                return true; 
            }
        }

        return false;
    }

    public void close() throws Exception{
        arq.close();
    }
}

