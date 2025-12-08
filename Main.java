/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package minicompil;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== MENU PRINCIPAL : Analyseur Lexical + Syntaxique ===");
        System.out.println("Options : (L)exical   (S)yntaxique   (B)oth ");

        while (true) {
            System.out.print("\nChoix > ");
            String choix = sc.nextLine().trim().toUpperCase();
            if (choix.isEmpty()) continue;

            switch (choix.charAt(0)) {
                case 'L':

                    System.out.println("\n--- MODE LEXICAL ---");
                    System.out.println("Tapez votre code :");
                    while (true) {
                        System.out.print("\nCode > ");
                        String ligne = sc.nextLine();
                        if (ligne.equalsIgnoreCase("STOP")) break;

                        String mot = ligne + '#';
                        AnalyseurLexical AL = new AnalyseurLexical(mot);
                        String token = "";

                        while (!token.equals("fin")) {
                            token = AL.prochaintoken();
                            if (!token.equals("fin")) {
                                System.out.println(token);
                            }
                        }
                    }
                    break;

                case 'S':
         
                    System.out.println("\n--- MODE SYNTAXIQUE ---");
                    System.out.println("Tapez votre code:");
                    while (true) {
                        System.out.print("\nCode > ");
                        String code = sc.nextLine();
                        if (code.equalsIgnoreCase("STOP")) break;

                        AnalyseurSyntaxique parser = new AnalyseurSyntaxique(code);
                        parser.Z();
                    }
                    break;

                case 'B':

                    System.out.println("\n--- MODE COMBINÉ : Lexical + Syntaxique ---");
                    System.out.println("Tapez votre code (ou tapez 'STOP' pour revenir au menu) :");
                    while (true) {
                        System.out.print("\nCode > ");
                        String ligne = sc.nextLine();
                        if (ligne.equalsIgnoreCase("STOP")) break;

                        System.out.println("\n[Etape 1] - Analyse lexicale :");
                        String mot = ligne + '#';
                        AnalyseurLexical AL = new AnalyseurLexical(mot);
                        String token = "";
                        while (!token.equals("fin")) {
                            token = AL.prochaintoken();
                            if (!token.equals("fin")) {
                                System.out.println(token);
                            }
                        }

                        System.out.println("\n[Etape 2] - Analyse syntaxique :");
                        AnalyseurSyntaxique parser = new AnalyseurSyntaxique(ligne);
                        parser.Z();
                    }
                    break;

                default:
                    System.out.println("Option invalide. Choisissez L, S, B.");
            }
        }
    }

}
