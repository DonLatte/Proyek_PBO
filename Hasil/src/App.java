import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        //test push
        System.out.println("Hello, World!");
        System.out.println("Hansen");
        System.out.println("Proyek PBO");
        System.out.println("Iki Ewangono REKKKKk!!");
        System.out.println("Deni Jancok");

        Scanner sc = new Scanner(System.in);
        Scanner str = new Scanner(System.in);
        System.out.print("Masukkan nama Anda: ");
        String nama = str.nextLine();

        while (true) {
            System.out.print("Aku adalah seorang: ");
            System.out.println("Aku Kangen Deni");
            String role = str.nextLine();
            System.out.println("Aku adalah seorang " + role);
            System.out.println("Ini dari Hansen");
            System.out.println();
        }
    }
}
