import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CarManager manager = new CarManager();
        int choice = -1;

        do {
            System.out.println("\n=== МЕНЮ ===");
            System.out.println("1. Показати всі автомобілі");
            System.out.println("2. Змінити автомобіль");
            System.out.println("3. Показати автомобілі дорожче заданої ціни");
            System.out.println("4. Сортувати за зростанням ціни");
            System.out.println("5. Сортувати за спаданням ціни");
            System.out.println("6. Сортувати за зростанням марки (А-Я)");
            System.out.println("7. Сортувати за спаданням марки (Я-А)");
            System.out.println("8. Показати марки, які починаються на T (регулярний вираз)");
            System.out.println("9. Показати найдешевший і найдорожчий автомобілі");
            System.out.println("10. Завантажити автомобілі з cars.txt");
            System.out.println("11. Відсортувати та записати у cars.dat");
            System.out.println("12. Прочитати cars.dat та показати автомобілі");
            System.out.println("0. Вихід");
            System.out.print("Ваш вибір: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Введіть номер пункту меню!");
                scanner.nextLine();
                continue;
            }
            choice = scanner.nextInt();
            try {
                switch (choice) {
                    case 1: manager.showAll(); break;
                    case 2:
                        manager.showAll();
                        System.out.print("Введіть індекс автомобіля: ");
                        int index = scanner.nextInt();
                        System.out.print("Марка: ");
                        String brand = scanner.next();
                        System.out.print("Модель: ");
                        scanner.nextLine();
                        String model = scanner.nextLine();
                        System.out.print("Рік: ");
                        int year = scanner.nextInt();
                        System.out.print("Ціна: ");
                        double price = scanner.nextDouble();
                        manager.editCar(index, brand, model, year, price);
                        break;
                    case 3:
                        System.out.print("Введіть мінімальну ціну: ");
                        manager.showExpensiveCars(scanner.nextDouble());
                        break;
                    case 4: manager.sortAscending(); break;
                    case 5: manager.sortDescending(); break;
                    case 6: manager.sortBrandAscending(); break;
                    case 7: manager.sortBrandDescending(); break;
                    case 8: manager.showBrandsStartingWithT(); break;
                    case 9: manager.showMinMaxPrice(); break;
                    case 10: manager.loadFromText("cars.txt"); break;
                    case 11: manager.saveSortedToBinary("cars.dat"); break;
                    case 12: manager.showFromBinary("cars.dat"); break;
                    case 0: System.out.println("Завершення програми."); break;
                    default: System.out.println("Невідомий пункт меню!");
                }
            } catch (IOException e) {
                System.out.println("Помилка роботи з файлом: " + e.getMessage());
            }
        } while (choice != 0);
        scanner.close();
    }
}
