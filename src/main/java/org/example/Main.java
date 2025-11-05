package org.example;

import java.util.Scanner;

public class Main {
        // Vulnerabilidade: senha hardcoded
        // Vulnerabilidade: senha hardcoded
        private static final String SECRET_KEY = "123456";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Entrada do usuário
        System.out.print("Digite seu nome: ");
        String userInput = scanner.nextLine();

        // Vulnerabilidade: uso inseguro de entrada do usuário
        if (userInput.equals("admin")) {
            System.out.println("Olá admin! Chave secreta: " + SECRET_KEY);
        } else {
            System.out.println("Olá " + userInput + "!");
        }

        // Vulnerabilidade: função insegura (simulação)
        insecureMethod(userInput);

        scanner.close();
    }

    // Método propositalmente inseguro para SAST detectar
    private static void insecureMethod(String input) {
        // Aqui simulamos algo que seria potencialmente explorável
        System.out.println("Você digitou: " + input);
    }
}