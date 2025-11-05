package org.example;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Scanner;

public class Main {

    // Hardcoded secret (formato AWS-like para garantir detecção didática)
    public static final String AWS_SECRET_KEY = "AKIAEXAMPLEKEY12345";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // --- Entrada do usuário ---
        System.out.print("Digite seu nome: ");
        String userInput = scanner.nextLine();

        // --- Exposição de segredo (impressão de secret) ---
        if ("admin".equals(userInput)) {
            // exposição explícita de secret
            System.out.println("Bem-vindo admin. Chave: " + AWS_SECRET_KEY);
        } else {
            System.out.println("Olá " + userInput + "!");
        }

        // --- SQL Injection: concatenação e execução de query ---
        try {
            // apenas para compilação; não é necessário um DB real para SAST
            Connection conn = DriverManager.getConnection("jdbc:dummy://localhost:3306/demo");
            Statement stmt = conn.createStatement();
            String query = "SELECT * FROM users WHERE username = '" + userInput + "'";
            System.out.println("Executando query (demo): " + query);
            // A chamada abaixo é proposital: CodeQL detecta a concatenação sendo usada diretamente em executeQuery
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                System.out.println("Encontrado: " + rs.getString(1));
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            // ignorar erros em tempo de execução — SAST não precisa executar
        }

        // --- Command injection: executar input do usuário ---
        System.out.print("Digite um comando simples (ex: ls ou dir): ");
        String cmd = scanner.nextLine();
        try {
            // Perigoso: execução direta da entrada do usuário
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            // ignorar
        }

        // --- Path traversal / escrita insegura em arquivo ---
        System.out.print("Nome do arquivo para salvar (demo): ");
        String filename = scanner.nextLine();
        try {
            // Uso direto do input para construir o caminho
            File target = new File("uploads/" + filename);
            target.getParentFile().mkdirs();
            FileWriter fw = new FileWriter(target);
            fw.write("Conteúdo de teste para: " + userInput);
            fw.close();
            System.out.println("Arquivo salvo em: " + target.getPath());
        } catch (Exception e) {
            // ignorar
        }

        // --- Desserialização insegura (leitura de objeto de arquivo controlado) ---
        System.out.print("Arquivo para desserializar (demo): ");
        String objFile = scanner.nextLine();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(objFile))) {
            // Desserializar input controlado pelo usuário é perigoso
            Object o = ois.readObject();
            System.out.println("Objeto desserializado: " + o);
        } catch (Exception e) {
            // ignorar
        }

        scanner.close();
    }
}
