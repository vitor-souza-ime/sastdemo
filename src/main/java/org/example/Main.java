package org.example;

import java.io.File;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Scanner;

public class Main {
    // Vulnerabilidade 1: senha hardcoded (secret)
    private static final String SECRET_KEY = "123456";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Digite seu nome: ");
        String userInput = scanner.nextLine();

        // Vulnerabilidade 2: exposição de segredo (imprimir secret)
        if ("admin".equals(userInput)) {
            System.out.println("Olá admin! Chave secreta: " + SECRET_KEY);
        } else {
            System.out.println("Olá " + userInput + "!");
        }

        // Vulnerabilidade 3: SQL Injection (concatenação direta em query)
        try {
            // NOTA: apenas para demonstração estática — pode falhar em tempo de execução sem driver
            Connection conn = DriverManager.getConnection("jdbc:fake:demo"); // não usado realmente
            Statement stmt = conn.createStatement();
            String query = "SELECT * FROM users WHERE username = '" + userInput + "'";
            System.out.println("Executando query (demo): " + query);
            // stmt.executeQuery(query); // comentado para evitar runtime errors, mas CodeQL analisa a concatenação
        } catch (Exception e) {
            // ignorar
        }

        // Vulnerabilidade 4: Command Injection (usar input para executar comando)
        System.out.print("Digite um comando para demo (por exemplo: ls): ");
        String cmd = scanner.nextLine();
        try {
            // Perigoso: executar entrada direta do usuário.
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            // ignorar
        }

        // Vulnerabilidade 5: Path traversal / escrita insegura em arquivo
        System.out.print("Arquivo para salvar (nome): ");
        String filename = scanner.nextLine();
        try {
            // Se o usuário passar "../etc/passwd" ou um caminho absoluto, pode ser problemático
            File out = new File("data/" + filename);
            out.getParentFile().mkdirs();
            FileWriter fw = new FileWriter(out);
            fw.write("Conteúdo de teste para: " + userInput);
            fw.close();
            System.out.println("Arquivo salvo em: " + out.getPath());
        } catch (Exception e) {
            // ignorar
        }

        // Vulnerabilidade 6: Desserialização insegura (leitura de objeto a partir de arquivo controlado)
        System.out.print("Arquivo de objeto (demo de desserialização): ");
        String objFile = scanner.nextLine();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(objFile))) {
            // Ler objeto arbitrário vindo de arquivo controlado pelo usuário é perigoso
            Object obj = ois.readObject();
            System.out.println("Objeto desserializado: " + obj);
        } catch (Exception e) {
            // ignorar
        }

        scanner.close();
    }
}
