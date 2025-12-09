/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package minicompil;

/**
 *
 * @author HP
 */

import java.util.Scanner;

public class AnalyseurSyntaxique {

    private AnalyseurLexical AL;
    private String token;
    private boolean ok = true;

    public AnalyseurSyntaxique(String code) {
        AL = new AnalyseurLexical(code);
        token = AL.prochaintoken();
    }

 
    public void Z() {
        ok = true;
        I();
        if (ok && token.equals("fin") ) {
            System.out.println("chaine acceptee");
        } else {
            System.out.println("chaine non acceptee");
        }
    }


    private void I() {
        if (token.contains("<motcle")) {
            D();
        } else {
            E();
            if (token.equals("<operateurTernaire  ------>     ?")) {
                T();
            } else if (token.equals("<operateur  ------>     =>") || token.equals("<operateur  ------>     =")) {
                A();
            } else if (token.contains("<separateur") && token.contains(";")) {
                avancer(); 
            } else {
                erreur("Instruction invalide");
            }
        }
    }


    private void D() {
        avancer(); 
        if (token.contains("<identificateur")) {
            avancer(); 
            if (token.contains("<separateur") && token.contains(";")) {
                avancer();
            }
        } else erreur("identificateur attendu apres type");
    }


    private void A() {
        avancer(); 
        E();
        if (token.contains("<separateur") && token.contains(";")) {
            avancer();
        } else erreur("';' attendu apres affectation");
    }

 
    private void T() {
        avancer(); 
        E();
        if (token.equals("<operateurTernaire  ------>     :")) {
            avancer();
            E();
            if (token.contains("<separateur") && token.contains(";")) {
                avancer();
            } else erreur("';' attendu apres ternaire");
        } else erreur("':' attendu dans ternaire");
    }


    private void E() {
        if (token.contains("<identificateur") || token.contains("<nombre")) {
            avancer();
            OpExpr();
        } else if (token.contains("<separateur") && token.contains("(")) {
            Parens();
            OpExpr();
        } else {
            erreur("Expression invalide");
        }
    }


    private void OpExpr() {
        while (token.contains("<operateur") && !token.equals("<operateurTernaire  ------> ?")) {
            avancer();
            if (token.contains("<identificateur") || token.contains("<nombre")) {
                avancer();
            } else if (token.contains("<separateur") && token.contains("(")) {
                Parens();
            } else {
                erreur("Expression invalide apres operateur");
                break;
            }
        }
    }


    private void Parens() {
        if (token.contains("<separateur") && token.contains("(")) {
            avancer();
            E();
            if (token.contains("<separateur") && token.contains(")")) {
                avancer();
            } else {
                erreur("')' attendu");
            }
        }
    }


    private void avancer() {
        token = AL.prochaintoken();
    }


    private void erreur(String msg) {
        System.out.println("Erreur syntaxique : " + msg + " (token=    " + token + ")");
        ok = false;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("--- ANALYSEUR SYNTAXIQUE TERNAIRE  ---");

        while (true) {
            System.out.print("\nCode > ");
            String code = sc.nextLine(); 
            AnalyseurSyntaxique parser = new AnalyseurSyntaxique(code);
            parser.Z();
        }
    }
}

