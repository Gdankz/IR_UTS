package main;

import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        boolean loop = true;
        Scanner sc = new Scanner(System.in);

        while (loop) {
            System.out.print("1. Mencari kata kunci" +
                    "\nPilih: ");
            int angka = sc.nextInt();
            if (angka == 1) {
                System.out.print("\nMasukkan kata kunci: ");
                String query = sc.next();

                System.out.println("\nHasil pencarian: " + query + "\n");
            } else {
                loop = false;
            }
        }
    }
}
