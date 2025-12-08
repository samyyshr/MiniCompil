/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */


package minicompil;


import java.util.Scanner;


public class AnalyseurLexical {

    private String mot;
    private int position;
    private char TC;
    private boolean fin;

    public AnalyseurLexical(String mot) {
        this.mot = mot + "#"; 
        this.position = 0;
        this.TC = this.mot.charAt(0);
        this.fin= false;
    }

    private final int[][] autnmbr = {
        {1, 2, -1, -1},
        {1, 3, 4, -1},
        {-1, -1, -1, -1},
        {3, -1, 4, -1},
        {4, 4, 4, 4}
    };

    private final int[][] autident = {
        {1, -1, 1, -1, -1},
        {1, 1, 1, 2, -1}
    };

    private final int[][] autoper = {
        {1, -1, -1},
        {2, 3, -1},
        {3, 3, -1},
        {3, 3, 3}
    };

    private boolean estchiffre(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean estlettre(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean estsouligne(char c) {
        return c == '_';
    }

    private boolean estpoint(char c) {
        return c == '.';
    }

    private boolean estespace(char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }

    private boolean estseparateur(char c) {
        char[] separateurs = {'{', '}', '(', ')', '[', ']', ';', ','};
        for (int i = 0; i < separateurs.length; i++) {
            if (separateurs[i] == c) {
                return true;
            }
        }
        return false;
    }

    private boolean estoperateur(char c) {
        char[] oper = {'+', '-', '*', '/', '=', '<', '>', '!', '&', '|', '%'};
        for (int i = 0; i < oper.length; i++) {
            if (oper[i] == c) {
                return true;
            }
        }
        return false;
    }
    
    private boolean estTernaire(char c) {
    return c == '?' || c == ':';
}

    private boolean estcaracterenonreconnu(char c) {
        return !estchiffre(c) && !estlettre(c) && !estsouligne(c)
                && !estpoint(c) && !estespace(c) && !estseparateur(c)
                && !estoperateur(c) && c != '#' && c != '"' && c != '\'' && c != '\\';
    }


    private int numcolonnenombre(char c) {
        if (estchiffre(c)) return 0;
        else if (estpoint(c)) return 1;
        else if (estcaracterenonreconnu(c)) return 3;
        return 2;
    }

    private int numcolonneident(char c) {
        if (estlettre(c)) return 0;
        else if (estchiffre(c)) return 1;
        else if (estsouligne(c)) return 2;
        else if (estcaracterenonreconnu(c)) return 4;
        return 3;
    }

    private int numcolonneoperateur(char c) {
        if (estoperateur(c)) return 0;
        else if (estcaracterenonreconnu(c)) return 2;
        return 1;
    }

    private void caracteresuivant() {
        if (fin) return;

        if (position < mot.length() - 1) {
            position++;
            TC = mot.charAt(position);
        } else {
            fin = true;
        }
    }

    private boolean fin() {
        return (fin || TC == '#') && position >= mot.length() - 1;
    }

    public String prochaintoken() {
    if (fin) {
        return "fin";
    }

    // 1. Ignorer les espaces blancs
    while (!fin() && estespace(TC)) {
        caracteresuivant();
    }

    if (fin()) {
        fin = true;
        return "fin";
    }

    // 👉 AJOUT : stopper proprement sur #
    if (TC == '#') {
        fin = true;
        return "fin";
    }

    // AJOUT : Gestion des commentaires (//)
    if (TC == '/') {
        if (position + 1 < mot.length() && mot.charAt(position + 1) == '/') {
            while (!fin() && TC != '\n') {
                caracteresuivant();
            }
            return prochaintoken();
        }
    }

    char c = TC;

    if (estchiffre(c)) {
        return analysernombre();
    } 
    else if (estlettre(c) || estsouligne(c)) {
        return analyseridentificateur();
    } 
    else if (c == '"') {
        return analyserchaine();
    }
    else if (estTernaire(c)) {
        return analyserTernaire();
    }
    else if (estoperateur(c)) {
        return analyseroperateur();
    } 
    else if (estseparateur(c)) {
        return analyserseparateur();
    } 
    else {
        return analysererreur();
    }
}


    private String analysernombre() {
        int etat = 0;
        String valeur = "";

        while (!fin() && etat != -1 && etat != 4) {
            int col = numcolonnenombre(TC);
            etat = autnmbr[etat][col];

            if (etat != -1 && etat != 4) {
                valeur += TC;
                caracteresuivant();
            }
        }

        if (etat == 4) {
            return "<nombre  ------>     " + valeur + ">";
        } else {
            return "<erreur  ------>     " + valeur + ">";
        }
    }

    private String analyseridentificateur() {
        int etat = 0;
        String valeur = "";

        while (!fin() && etat != -1 && etat != 2) {
            int col = numcolonneident(TC);
            etat = autident[etat][col];

            if (etat != -1 && etat != 2) {
                valeur += TC;
                caracteresuivant();
            }
        }

        if (etat == 2 || (valeur.length() > 0 && !estlettre(TC) && !estchiffre(TC) && !estsouligne(TC))) {
            if (estmotclespecial(valeur)) {
                return "<motclespecial  ------>     " + valeur + ">";
            } else if (estmotcle(valeur)) {
                return "<motcle  ------>     " + valeur + ">";
            } else {
                return "<identificateur  ------>     " + valeur + ">";
            }
        } else {
            return "<erreur  ------>     " + valeur + ">";
        }
    }

    private String analyseroperateur() {
        int etat = 0;
        String valeur = "";

        while (!fin() && etat != -1 && etat != 3) {
            int col = numcolonneoperateur(TC);
            etat = autoper[etat][col];

            if (etat != -1 && etat != 3) {
                valeur += TC;
                caracteresuivant();
            }
        }

        if (etat == 2 || etat == 3) {
            return "<operateur  ------>     " + valeur + ">";
        } else {
            return "<erreur  ------>     " + valeur + ">";
        }
    }

   
    private String analyserchaine() {
        String valeur = "\"";
        caracteresuivant(); 

        while (!fin() && TC != '"') {
            valeur += TC;
            caracteresuivant();
        }

        if (TC == '"') {
            valeur += "\"";
            caracteresuivant(); 
            return "<chaine," + valeur + ">";
        } else {
            return "<erreur,chaine_non_fermee>";
        }
    }

    private String analyserseparateur() {
        char separateur = TC;
        caracteresuivant();
        return "<separateur  ------>     " + separateur + ">";
    }

    private String analysererreur() {
        char erreur = TC;
        caracteresuivant();
        return "<erreur  ------>     " + erreur +    ">";
    }
    
    
    private String analyserTernaire() {
    char operateur = TC;
    caracteresuivant();
    return "<operateurTernaire  ------>     " + operateur + ">";
}

    private boolean estmotclespecial(String mot) {
        return mot.equals("SAHRAOUI") || mot.equals("Samy");
    }

    private boolean estmotcle(String mot) {
        String[] motscles = {
            "char", "double", "float", "int", "long", "short", "void",
            "signed", "unsigned", "const", "volatile", "static", "extern", "auto", "register",
            "if", "else", "switch", "case", "default", "for", "while", "do", "break", "continue", "goto", "return",
            "struct", "union", "enum"
        };
        
        for (String mc : motscles) {
            if (mot.equals(mc)) return true;
        }
        return false;
    }
    
public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        System.out.println("--- ANALYSEUR LEXICAL (Mode Interactif) ---");
        System.out.println("Tapez votre code (ou tapez 'STOP' pour quitter) :");

        while (true) {
            System.out.print("\nCode > ");
            String mot = sc.nextLine()+'#';
  
            AnalyseurLexical AL = new AnalyseurLexical(mot);
            String token = "";
            

            while (!token.equals("fin")) {
                token = AL.prochaintoken();
                if (!token.equals("fin")) {
                    System.out.println(token);
                }
            }
        }
    }

    }