package Math_Matrix_Helper;

class Prog {
    public static void main(String[] args) {
        Matrice A = new Matrice();
        A.caricaDaFile("A.txt");
        A = A.riduciScala();
        A.printMatrice();
    }

}
