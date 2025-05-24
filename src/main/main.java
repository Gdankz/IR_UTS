package main;

import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        boolean loop = true;
        Scanner sc = new Scanner(System.in);
        Make_Dics mk = new Make_Dics();
        mk.MakeDics();

        while (loop) {
            System.out.print("1. Mencari kata kunci" +
                    "\nPilih: ");
            int angka = sc.nextInt();
            if (angka == 1) {
                System.out.print("\nMasukkan kata kunci: ");
                String query = sc.next();

//                mk.processQuery(query);
            } else {
                loop = false;
            }
        }
    }
}
