package Math_Matrix_Helper;

class Prog {
    public static void main(String[] args) {
        Matrice A = new Matrice();
        Matrice B = new Matrice();
        A.caricaDaFile("A.txt");
        B.caricaDaFile("C.txt");
        A.printMatrice();
        B.printMatrice();
        System.out.println("A + B:");
        Matrice C = Matrice.sommaMatrici(A, B);
        C.printMatrice();
    }

}
