import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Vigenere {
    private final String chave;
    
    public Vigenere(String chave) {
        this.chave = chave.toUpperCase();
    }
    
    // Criptografa um array de bytes usando a cifra de Vigenère
    public byte[] criptografar(byte[] dados) {
        if (dados == null || dados.length == 0 || chave.isEmpty()) {
            return dados;
        }
        
        byte[] resultado = new byte[dados.length];
        int chaveIndex = 0;
        
        for (int i = 0; i < dados.length; i++) {
            // Converte o byte para um valor sem sinal (0-255)
            int dadoByte = dados[i] & 0xFF;
            
            // Obtém o caractere da chave
            char chaveChar = chave.charAt(chaveIndex % chave.length());
            int chaveValor = chaveChar - 'A'; // Converte para 0-25
            
            // Aplica a cifra de Vigenère
            int criptografado = (dadoByte + chaveValor) % 256;
            resultado[i] = (byte) criptografado;
            
            chaveIndex++;
        }
        
        return resultado;
    }
    
    // Descriptografa um array de bytes usando a cifra de Vigenère
    public byte[] descriptografar(byte[] dadosCriptografados) {
        if (dadosCriptografados == null || dadosCriptografados.length == 0 || chave.isEmpty()) {
            return dadosCriptografados;
        }
        
        byte[] resultado = new byte[dadosCriptografados.length];
        int chaveIndex = 0;
        
        for (int i = 0; i < dadosCriptografados.length; i++) {
            // Converte o byte para um valor sem sinal (0-255)
            int dadoByte = dadosCriptografados[i] & 0xFF;
            
            // Obtém o caractere da chave
            char chaveChar = chave.charAt(chaveIndex % chave.length());
            int chaveValor = chaveChar - 'A'; // Converte para 0-25
            
            // Aplica a cifra de Vigenère inversa
            int descriptografado = (dadoByte - chaveValor + 256) % 256;
            resultado[i] = (byte) descriptografado;
            
            chaveIndex++;
        }
        
        return resultado;
    }
    
    // Criptografa um arquivo completo
    public void criptografarArquivo(String caminhoOriginal, String caminhoDestino) throws IOException {
        byte[] dados = Files.readAllBytes(Paths.get(caminhoOriginal));
        byte[] dadosCriptografados = criptografar(dados);
        Files.write(Paths.get(caminhoDestino), dadosCriptografados);
    }
    
    // Descriptografa um arquivo completo
    public void descriptografarArquivo(String caminhoCriptografado, String caminhoDestino) throws IOException {
        byte[] dadosCriptografados = Files.readAllBytes(Paths.get(caminhoCriptografado));
        byte[] dadosDescriptografados = descriptografar(dadosCriptografados);
        Files.write(Paths.get(caminhoDestino), dadosDescriptografados);
    }
}
