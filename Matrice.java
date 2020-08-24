package Math_Matrix_Helper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.util.*;

/*
    Author: Tornaghi omar
*/

public class Matrice {
    private int numRighe;
    private int numColonne;
    private double matr[][];
    private static String SEPARATORE = " ";

    public Matrice() {
    }

    public Matrice(int dimI, int dimJ) {
        matr = new double[dimI][dimJ];
        numRighe = dimI;
        numColonne = dimJ;
    }

    public Matrice(double[][] matrIn, int dimI, int dimJ) {
        matr = new double[dimI][dimJ];
        numRighe = dimI;
        numColonne = dimJ;
        for (int i = 0; i < dimI; i++) {
            for (int j = 0; j < dimJ; j++) {
                matr[i][j] = matrIn[i][j];
            }
        }
    }

    // GET & SET

    public double getE(int i, int j) {
        try {
            return matr[i][j];
        } catch (Exception e) {
            return 0;
        }

    }

    public void setE(int i, int j, double val) {
        try {
            matr[i][j] = val;
        } catch (Exception e) {
        }
    }

    public int getNumRighe() {
        return numRighe;
    }

    public void setNumRighe(int val) {
        numRighe = val;
    }

    public int getNumColonne() {
        return numColonne;
    }

    public void setNumColonne(int val) {
        numColonne = val;
    }

    // VARIE OPERAZIONI

    // - DETERMINANTE

    public double calcolaDeterminante() {
        if (numRighe != numColonne)
            return -0000;
        return cDet(matr);
    }

    private double cDet(double matrIn[][]) {
        int n = numRighe;
        switch (n) {
            case 1:
                return matrIn[0][0];
            case 2:
                return (matrIn[0][0] * matrIn[1][1]) - (matrIn[0][1] * matrIn[1][0]);
            default:
                // Scelgo la riga(Sempre la prima)
                int numRiga = 0;
                int sommatoria = 0;
                for (int k = 0; k < n; k++) {
                    double valMatr = matrIn[numRiga][k];
                    if (valMatr != 0) {
                        double primoNum = Math.pow(-1, (numRiga + 1) + (k + 1));
                        double[][] matrRidotta = riduciMatr(matrIn, numRiga, k);
                        double detA = cDet(matrRidotta);
                        sommatoria += primoNum * valMatr * detA;
                    }
                }
                return sommatoria;
        }
    }

    private static double[][] riduciMatr(double[][] matrIn, int numRigaEscl, int numColonnaEscl) {
        int n = matrIn.length - 1;
        double[][] matrOut = new double[n][n];
        int iMatrOut = 0;
        int jMatrOut = 0;
        for (int i = 0; i < matrIn.length; i++) {
            if (i != numRigaEscl) {
                for (int j = 0; j < matrIn.length; j++) {
                    if (j != numColonnaEscl) {
                        matrOut[iMatrOut][jMatrOut] = matrIn[i][j];
                        jMatrOut++;
                        // Aggiorno iMatrOut e jMatrOut
                        if (jMatrOut == n) {
                            iMatrOut += 1;
                            jMatrOut = 0;
                        }
                    }
                }
            }
        }
        return matrOut;
    }

    // CALCOLO DEL RANGO
    public int calcolaRango() {
        Matrice ridScala = this.riduciScala();
        int numRZero = 0;
        for (int i = 0; i < ridScala.getNumRighe(); i++) {
            for (int j = 0; j < ridScala.getNumColonne(); j++) {
                if (ridScala.getE(i, j) != 0) {
                    numRZero++;
                    break;
                }
            }
        }
        return numRZero;
    }

    public Matrice riduciScala() {
        if (numRighe <= 0 || numColonne <= 0)
            return new Matrice(0, 0);
        Matrice scala = copiaMatriceSelf();
        Matrice risultato = new Matrice(numRighe, numColonne);
        int offset = 0;
        do {
            // Se la prima riga ha il primo elemento nullo, la scambio
            // con una riga che ha il primo elemento non nullo
            boolean tutteNulle = false;
            if (scala.getE(0, 0) == 0) {
                tutteNulle = true;
                for (int i = 0; i < scala.getNumRighe(); i++) {
                    if (scala.getE(i, 0) != 0)
                        scala = scambiaRiga(scala, 0, i);
                    tutteNulle = false;
                    break;
                }
            }
            // Azzero il primo elemento di ogni riga
            if (!tutteNulle) {
                for (int i = 1; i < scala.getNumRighe(); i++) {
                    if (scala.getE(i, 0) != 0) {
                        double coefficiente = -scala.getE(i, 0) / scala.getE(0, 0);
                        double[] rigaMoltiplicata = moltiplicaRiga(scala, 0, coefficiente);
                        scala = sommaRiga(scala, i, rigaMoltiplicata);
                    }
                }
            }
            // Considero la sottomatrice eliminando la prima riga e la prima colonna
            for (int j = 0; j < scala.getNumColonne(); j++) {
                risultato.setE(offset, j + offset, scala.getE(0, j));
            }
            offset++;
            scala = riduciMatrice(scala, 0, 0);
        } while (scala.getNumColonne() != 0 || scala.getNumRighe() != 0);
        return ottimizzaScala(risultato);
    }

    private Matrice copiaMatriceSelf() {
        Matrice copia = new Matrice(numRighe, numColonne);
        for (int i = 0; i < numRighe; i++) {
            for (int j = 0; j < numColonne; j++) {
                copia.setE(i, j, matr[i][j]);
            }
        }
        return copia;
    }

    private Matrice riduciMatrice(Matrice in, int rigaEscl, int colEscl) {
        int iOut = 0, jOut = 0;
        Matrice nuova = new Matrice(in.getNumRighe() - 1, in.getNumColonne() - 1);
        if (nuova.getNumRighe() == 0 || nuova.getNumColonne() == 0)
            return new Matrice(0, 0);
        for (int i = 0; i < in.numRighe; i++) {
            if (i != rigaEscl) {
                for (int j = 0; j < in.getNumColonne(); j++) {
                    if (j != colEscl) {
                        nuova.setE(iOut, jOut, in.getE(i, j));
                        jOut++;
                    }
                }
                iOut++;
                jOut = 0;
            }
        }
        return nuova;
    }

    private Matrice scambiaRiga(Matrice out, int from, int dest) {
        for (int j = 0; j < out.getNumColonne(); j++) {
            double temp = out.getE(dest, j);
            out.setE(dest, j, out.getE(from, j));
            out.setE(from, j, temp);
        }
        return out;
    }

    private double[] moltiplicaRiga(Matrice in, int riga, double num) {
        double[] rigaMoltiplicata = new double[in.getNumColonne()];
        for (int j = 0; j < in.getNumColonne(); j++) {
            rigaMoltiplicata[j] = in.getE(riga, j) * num;
        }
        return rigaMoltiplicata;
    }

    private Matrice sommaRiga(Matrice out, int numRigaDest, double[] riga) {
        for (int j = 0; j < out.getNumColonne(); j++) {
            out.setE(numRigaDest, j, out.getE(numRigaDest, j) + riga[j]);
        }
        return out;
    }

    private Matrice sostituisciRiga(Matrice out, int i, double[] riga) {
        for (int j = 0; j < out.getNumColonne(); j++) {
            out.setE(i, j, riga[j]);
        }
        return out;
    }

    public boolean checkScala() {
        int posPivotPrec = -1;
        for (int i = 0; i < numRighe; i++) {
            boolean f = false;
            int j = 0;
            for (j = 0; j < numColonne && !f; j++) {
                if (matr[i][j] != 0)
                    f = true;
            }
            if (f) {
                if (j <= posPivotPrec)
                    return false;
            }
            posPivotPrec = j;
        }
        return true;
    }

    private Matrice ottimizzaScala(Matrice s) {
        // Controllo se le posizioni dei pivot sono corrette
        int posPivotPrecedente = -1;
        for (int i = 0; i < s.getNumRighe(); i++) {
            int posPivotAttuale = 0;
            for (int j = 0; j < s.getNumColonne(); j++) {
                if (s.getE(i, j) != 0) {
                    posPivotAttuale = j;
                    break;
                }
            }
            if (posPivotAttuale == posPivotPrecedente) {
                // il pivot è nella stessa posizione
                double coefficiente = -s.getE(i, posPivotAttuale) / s.getE(i - 1, posPivotAttuale);
                double[] rigaMoltiplicata = moltiplicaRiga(s, i - 1, coefficiente);
                s = sommaRiga(s, i, rigaMoltiplicata);
            } else {
                if (posPivotAttuale < posPivotPrecedente) // Scambio righe
                    s = scambiaRiga(s, i - 1, i);
                else
                    posPivotPrecedente = posPivotAttuale;
            }
        }
        return s;
    }

    // MATRICE INVERSA

    public Matrice calcolaInversa() {
        if (numRighe != numColonne)
            return new Matrice(0, 0);
        // Affianco la matrice originale a quella identica
        Matrice id = this.generaMatriceIdentica();
        Matrice inversa = unisciMatrici(this, id);
        inversa = inversa.riduciScala();
        // Trasformo i pivot in 1
        inversa = ConvertiPivot(inversa);
        // Trasformo gli elementi sopra i pivot a 0 partendo dall'ultima riga
        // Si prende il meno valore sopra il pivot si moltiplica riga corrente e si
        // somma a quella sopra
        inversa = AzzeraSupPivot(inversa);
        // Estraggo la matrice inversa e ritorno
        return estraiMatriceInversa(inversa);
    }

    private Matrice ConvertiPivot(Matrice out) {
        for (int i = out.getNumRighe() - 1; i >= 0; i--) {
            boolean pivot = false;
            int posPivot = -1;
            for (int j = 0; j < out.getNumColonne() && !pivot; j++) {
                if (out.getE(i, j) != 0 && !pivot) {
                    // Pivot trovato
                    pivot = true;
                    posPivot = j;
                }
            }
            if (pivot) {
                double n = out.getE(i, posPivot);
                double coeff = 1 / n;
                double[] rigaMoltiplicata = moltiplicaRiga(out, i, coeff);
                out = sostituisciRiga(out, i, rigaMoltiplicata);
            }
        }
        return out;
    }

    private Matrice AzzeraSupPivot(Matrice out) {
        for (int i = out.getNumRighe() - 1; i > 0; i--) {
            boolean pivot = false;
            int posPivot = -1;
            for (int j = 0; j < out.getNumColonne() && !pivot; j++) {
                if (out.getE(i, j) != 0 && !pivot) {
                    // Pivot trovato
                    pivot = true;
                    posPivot = j;
                }
            }
            if (pivot) {
                // Controllo le posizioni sopra il pivot se != 0 moltiplico
                // rigaCorrente * -valoreSopra e sommo la riga a quella sopra
                for (int k = i - 1; k >= 0; k--) {
                    if (out.getE(k, posPivot) != 0) {
                        double[] rigaMoltiplicata = moltiplicaRiga(out, i, -out.getE(k, posPivot));
                        out = sommaRiga(out, k, rigaMoltiplicata);
                    }
                }
            }
        }
        return out;
    }

    private static Matrice unisciMatrici(Matrice a, Matrice b) {
        Matrice c = new Matrice(a.getNumRighe(), 2 * a.getNumColonne());
        int ind = 0;
        for (int i = 0; i < c.getNumRighe(); i++) {
            for (int j = 0; j < c.getNumColonne(); j++) {
                ind = j % (a.getNumColonne());
                if (j < a.getNumColonne()) {
                    // Copio a
                    c.setE(i, j, a.getE(i, ind));
                } else {
                    // Copio b
                    c.setE(i, j, b.getE(i, ind));
                }
            }
        }
        return c;
    }

    private static Matrice estraiMatriceInversa(Matrice in) {
        Matrice inv = new Matrice(in.getNumRighe(), in.getNumColonne() / 2);
        for (int i = 0; i < inv.getNumRighe(); i++) {
            for (int j = 0; j < inv.getNumColonne(); j++) {
                inv.setE(i, j, in.getE(i, j + in.getNumColonne() / 2));
            }
        }
        return inv;
    }

    // MATRICE TRASPOSTA
    public Matrice generaMatriceTrasposta() {
        Matrice t = new Matrice(numColonne, numRighe);
        for (int j = 0; j < numColonne; j++) {
            for (int i = 0; i < numRighe; i++) {
                t.setE(j, i, matr[i][j]);
            }
        }
        return t;
    }

    // SOMMA TRA MATRICI
    public static Matrice sommaMatrici(Matrice a, Matrice b) {
        if (a.getNumRighe() != b.getNumRighe() || a.getNumColonne() != b.getNumColonne())
            return new Matrice(0, 0);
        for (int i = 0; i < a.getNumRighe(); i++) {
            for (int j = 0; j < a.getNumColonne(); j++) {
                a.setE(i, j, a.getE(i, j) + b.getE(i, j));
            }
        }
        return a;
    }

    // PRODOTTO RIGHE PER COLONNE
    public static Matrice prodottoRiCol(Matrice a, Matrice b) {
        if (a.getNumColonne() != b.getNumRighe())
            return new Matrice(0, 0);
        Matrice c = new Matrice(a.getNumRighe(), b.getNumColonne());
        for (int i = 0; i < a.getNumRighe(); i++) {
            for (int k = 0; k < b.getNumColonne(); k++) {
                int prodottoRiga = 0;
                for (int j = 0; j < a.getNumColonne(); j++) {
                    prodottoRiga += a.getE(i, j) * b.getE(j, k);
                }
                c.setE(i, k, prodottoRiga);
            }
        }
        return c;
    }

    // MATRICE IDENTICA
    public Matrice generaMatriceIdentica() {
        Matrice id = new Matrice(this.numRighe, this.numColonne);
        int posU = 0;
        for (int i = 0; i < numRighe; i++) {
            for (int j = 0; j < numColonne; j++) {
                if (i == posU && j == posU)
                    id.setE(i, j, 1);
                else
                    id.setE(i, j, 0);

            }
            posU++;
        }
        return id;
    }

    // FILE

    public void caricaDaFile(String perc) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(perc));
            numRighe = Integer.parseInt(reader.readLine());
            numColonne = Integer.parseInt(reader.readLine());
            matr = new double[numRighe][numColonne];
            String line = reader.readLine();
            int i = 0;
            while (line != null && !line.isEmpty()) {
                int j = 0;
                String arg[] = line.split(SEPARATORE);
                for (String a : arg) {
                    if (!a.isEmpty()) {
                        matr[i][j] = Integer.parseInt(a);
                        j++;
                    }
                }
                i++;
                line = reader.readLine();
            }
            reader.close();
        } catch (

        IOException e) {
        }
    }

    public void caricaDaFileV2(String perc) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(perc));
            String txtFile = "";
            String line = reader.readLine();
            while (line != null && !line.isEmpty()) {
                txtFile += line + "\n";
                line = reader.readLine();
            }
            String[] righe = txtFile.split("\n");
            String[] numStr;
            if (righe.length > 0)
                numStr = righe[0].split(" ");
            else {
                matr = new double[0][0];
                numRighe = 0;
                numColonne = 0;
                reader.close();
                return;
            }
            numRighe = righe.length;
            numColonne = numStr.length;
            matr = new double[numRighe][numColonne];
            int i = 0, j = 0;
            for (String str : righe) {
                numStr = str.split(" ");
                for (String nS : numStr) {
                    matr[i][j] = Double.parseDouble(nS);
                    j++;
                }
                j = 0;
                i++;
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Errore nella lettura del file");
            matr = new double[0][0];
            numRighe = 0;
            numColonne = 0;
        }
    }

    public void salvaSuFile(String percorso) {
        try {
            FileWriter w;
            w = new FileWriter(percorso);
            BufferedWriter b;
            b = new BufferedWriter(w);
            b.write(this.toString());
            b.close();
        } catch (Exception ex) {
        }
    }

    // MISC

    public void generaRandom(int numeroRighe, int numeroColonne, int maxNum) {
        Random random = new Random();
        matr = new double[numeroRighe][numeroColonne];
        numRighe = numeroRighe;
        numColonne = numeroColonne;
        for (int i = 0; i < numRighe; i++) {
            for (int j = 0; j < numColonne; j++) {
                matr[i][j] = random.nextInt(maxNum);
            }
        }
    }

    @Override
    public String toString() {
        String out = new String();
        for (int i = 0; i < numRighe; i++) {
            for (int j = 0; j < numColonne; j++) {
                out += String.valueOf(matr[i][j]);
                if (j != numColonne - 1)
                    out += SEPARATORE;
            }
            out += "\n";
        }
        return out;
    }

    public void printMatrice() {
        System.out.print(this.toString());
    }
}
