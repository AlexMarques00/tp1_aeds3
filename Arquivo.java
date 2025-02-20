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
        
        arq.writeShort(objeto.length);
        arq.write(' ');
        arq.write(objeto);
    }

    public void read(int id) throws Exception{
        // arq.seek(4);
        // int pos = 4;
        // while(pos < arq.length()){
        //     int tam = arq.readShort();
        //     if(arq.readByte() == ' '){
        //         Animes anime = new Animes();
        //     } else {
        //         arq.seek(arq.getFilePointer()+tam-1);
        //     }
        // }
    }

    public void update(int id) throws Exception{

    }

    public void delete(int id) throws Exception{
        
    }

    public void close() throws Exception{
        arq.close();
    }
}

