# 📚 Sistema de Gerenciamento de Base de Dados de Animes

Projeto desenvolvido na disciplina **Algoritmos e Estruturas de Dados III** (PUC Minas), com foco em manipulação de arquivos binários, estruturas de dados avançadas, compressão, criptografia e algoritmos de busca.

## 👩‍💻 Autores

- Júlia de Mello Teixeira  
- Alex Marques

## 🧩 Funcionalidades

- CRUD completo em arquivos binários
- Ordenação externa
- Indexação com:
  - Árvore B+
  - Hash Extensível
  - Lista Invertida
- Compressão com:
  - Huffman
  - LZW
- Casamento de padrões com:
  - Boyer-Moore
  - Knuth-Morris-Pratt (KMP)
- Criptografia com:
  - Vigenère (simétrica)
  - RSA (assimétrica)

## 🗂️ Estrutura do Projeto

```
TP_AED-sIII/
├── BaseAnimes.csv                # Base de dados original (CSV)
├── Main.java                     # Menu principal do sistema
├── Animes.java                   # Classe modelo dos animes
├── MyDate.java                   # Classe auxiliar para datas
├── ReadCSV.java                  # Importação do CSV para o banco binário
├── Arquivo.java                  # CRUD em arquivos binários
├── ArvoreBMais.java              # Implementação da Árvore B+
├── Pagina.java                   # Página da Árvore B+
├── HashExtensivo.java            # Hash extensível
├── Diretorio.java                # Diretório do hash extensível
├── Bucket.java                   # Cesto do hash extensível
├── ListaInvertida.java           # Lista invertida para buscas por atributos
├── Bloco.java                    # Bloco da lista invertida
├── Compressao.java               # Menu de compressão
├── Huffman.java                  # Compressão Huffman
├── LZW.java                      # Compressão LZW
├── VetorDeBits.java              # Auxiliar para compressão
├── CasamentoPadrao.java          # Menu de casamento de padrão
├── KMP.java                      # Algoritmo Knuth-Morris-Pratt
├── BoyerMoore.java               # Algoritmo Boyer-Moore
├── Criptografia.java             # Menu de criptografia
├── Vigenere.java                 # Criptografia Vigenère
├── RSA.java                      # Criptografia RSA
├── NoHuffman.java                # Nó da árvore de Huffman
└── FIM
```
Cada arquivo Java representa uma estrutura de dados, algoritmo ou funcionalidade do sistema. Os arquivos .db e .csv são gerados/explorados em tempo de execução. O projeto é modular e cada funcionalidade pode ser acessada pelo menu principal (Main.java).

## 🚀 Como Executar

1. Compile o projeto:
```bash
javac src/Main.java
````
2. Execute:
```bash
java src.Main
````
