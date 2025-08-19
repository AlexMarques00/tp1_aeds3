import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;

public class RSA {
    private final BigInteger chavePublica;
    private final BigInteger chavePrivada;
    private final BigInteger modulo;
    
    // Construtor que gera um par de chaves RSA
    public RSA() {
        // Gera números primos pequenos para demonstração (em produção, usar números maiores)
        int bitLength = 512; // Tamanho das chaves em bits
        SecureRandom random = new SecureRandom();
        
        // Gera dois números primos p e q
        BigInteger p = BigInteger.probablePrime(bitLength / 2, random);
        BigInteger q = BigInteger.probablePrime(bitLength / 2, random);
        
        // Calcula n = p * q
        this.modulo = p.multiply(q);
        
        // Calcula φ(n) = (p-1) * (q-1)
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        
        // Escolhe e (chave pública) - geralmente 65537
        this.chavePublica = BigInteger.valueOf(65537);
        
        // Calcula d (chave privada) - inverso modular de e
        this.chavePrivada = chavePublica.modInverse(phi);
    }
    
    // Construtor com chaves já geradas (para usar chaves específicas)
    public RSA(BigInteger chavePublica, BigInteger chavePrivada, BigInteger modulo) {
        this.chavePublica = chavePublica;
        this.chavePrivada = chavePrivada;
        this.modulo = modulo;
    }
    
    // Criptografa um array de bytes usando RSA
    public byte[] criptografar(byte[] dados) {
        if (dados == null || dados.length == 0) {
            return dados;
        }
        
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            // RSA processa blocos pequenos devido ao tamanho da chave
            int blockSize = (modulo.bitLength() - 1) / 8 - 11; // PKCS#1 padding
            if (blockSize <= 0) blockSize = 1;
            
            for (int i = 0; i < dados.length; i += blockSize) {
                int endIndex = Math.min(i + blockSize, dados.length);
                byte[] bloco = new byte[endIndex - i];
                System.arraycopy(dados, i, bloco, 0, bloco.length);
                
                // Converte bytes para BigInteger
                BigInteger dadosBigInt = new BigInteger(1, bloco);
                
                // Criptografa: c = m^e mod n
                BigInteger criptografado = dadosBigInt.modPow(chavePublica, modulo);
                
                // Converte de volta para bytes com tamanho fixo
                byte[] blocosCriptografados = criptografado.toByteArray();
                
                // Escreve o tamanho do bloco original e o tamanho do bloco criptografado, depois o bloco
                baos.write(bloco.length); // Tamanho original do bloco
                baos.write((blocosCriptografados.length >> 8) & 0xFF);
                baos.write(blocosCriptografados.length & 0xFF);
                baos.write(blocosCriptografados);
            }
            
            return baos.toByteArray();
        } catch (Exception e) {
            System.err.println("Erro na criptografia RSA: " + e.getMessage());
            return dados;
        }
    }
    
    // Descriptografa um array de bytes usando RSA
    public byte[] descriptografar(byte[] dadosCriptografados) {
        if (dadosCriptografados == null || dadosCriptografados.length == 0) {
            return dadosCriptografados;
        }
        
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int index = 0;
            
            while (index < dadosCriptografados.length) {
                // Lê o tamanho original do bloco
                if (index >= dadosCriptografados.length) break;
                int tamanhoOriginal = dadosCriptografados[index] & 0xFF;
                index++;
                
                // Lê o tamanho do bloco criptografado
                if (index + 1 >= dadosCriptografados.length) break;
                
                int tamanhoBloco = ((dadosCriptografados[index] & 0xFF) << 8) | 
                                  (dadosCriptografados[index + 1] & 0xFF);
                index += 2;
                
                if (index + tamanhoBloco > dadosCriptografados.length) break;
                
                // Lê o bloco criptografado
                byte[] bloco = new byte[tamanhoBloco];
                System.arraycopy(dadosCriptografados, index, bloco, 0, tamanhoBloco);
                index += tamanhoBloco;
                
                // Converte para BigInteger
                BigInteger dadosBigInt = new BigInteger(1, bloco);
                
                // Descriptografa: m = c^d mod n
                BigInteger descriptografado = dadosBigInt.modPow(chavePrivada, modulo);
                
                // Converte de volta para bytes preservando o tamanho original
                byte[] blocosDescriptografados = descriptografado.toByteArray();
                
                // Se o array resultante for menor que o original, adiciona zeros à esquerda
                if (blocosDescriptografados.length < tamanhoOriginal) {
                    byte[] blocosCompletos = new byte[tamanhoOriginal];
                    System.arraycopy(blocosDescriptografados, 0, 
                                   blocosCompletos, tamanhoOriginal - blocosDescriptografados.length, 
                                   blocosDescriptografados.length);
                    baos.write(blocosCompletos);
                } else {
                    // Se for maior, pega apenas os últimos bytes (remove zeros extras à esquerda)
                    baos.write(blocosDescriptografados, 
                              blocosDescriptografados.length - tamanhoOriginal, 
                              tamanhoOriginal);
                }
            }
            
            return baos.toByteArray();
        } catch (Exception e) {
            System.err.println("Erro na descriptografia RSA: " + e.getMessage());
            return dadosCriptografados;
        }
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
    
    // Retorna as chaves como string para armazenamento
    public String getChavesComoString() {
        return chavePublica.toString() + ":" + chavePrivada.toString() + ":" + modulo.toString();
    }
    
    // Cria RSA a partir de string de chaves
    public static RSA fromChavesString(String chavesString) {
        String[] partes = chavesString.split(":");
        if (partes.length != 3) {
            throw new IllegalArgumentException("Formato de chaves inválido");
        }
        
        BigInteger chavePublica = new BigInteger(partes[0]);
        BigInteger chavePrivada = new BigInteger(partes[1]);
        BigInteger modulo = new BigInteger(partes[2]);
        
        return new RSA(chavePublica, chavePrivada, modulo);
    }
}
