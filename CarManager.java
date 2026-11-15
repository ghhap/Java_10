import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

public class CarManager {
    private Car[] cars;

    public CarManager() {
        // Початкові дані з ЛР9 — залишено для роботи старого меню.
        cars = new Car[]{
            new Car("Toyota", "Camry", 2020, 25000),
            new Car("BMW", "X5", 2022, 65000),
            new Car("Audi", "A4", 2019, 28000),
            new Car("Mercedes", "C200", 2021, 45000),
            new Car("Honda", "Civic", 2018, 18000),
            new Car("Ford", "Focus", 2017, 12000),
            new Car("Volkswagen", "Passat", 2020, 23000),
            new Car("Hyundai", "Elantra", 2023, 22000),
            new Car("Kia", "Sportage", 2022, 30000),
            new Car("Tesla", "Model 3", 2023, 40000)
        };
    }

    public void showAll() {
        for (int i = 0; i < cars.length; i++) {
            System.out.println(i + ". " + cars[i]);
        }
    }

    public void editCar(int index, String brand, String model, int year, double price) {
        if (index >= 0 && index < cars.length) {
            cars[index].update(brand, model, year, price);
            System.out.println("Автомобіль змінено!");
        } else {
            System.out.println("Неправильний індекс!");
        }
    }

    public void showExpensiveCars(double minPrice) {
        boolean found = false;
        for (Car car : cars) {
            if (car.getPrice() > minPrice) {
                System.out.println(car);
                found = true;
            }
        }
        if (!found) System.out.println("Автомобілів за цією умовою не знайдено.");
    }

    public void sortAscending() {
        Arrays.sort(cars, Comparator.comparingDouble(Car::getPrice));
        System.out.println("Сортування за зростанням ціни:");
        showAll();
    }

    public void sortDescending() {
        Arrays.sort(cars, Comparator.comparingDouble(Car::getPrice).reversed());
        System.out.println("Сортування за спаданням ціни:");
        showAll();
    }

    public void sortBrandAscending() {
        Arrays.sort(cars, Comparator.comparing(Car::getBrand, String.CASE_INSENSITIVE_ORDER));
        System.out.println("Сортування марок за зростанням (А-Я):");
        showAll();
    }

    public void sortBrandDescending() {
        Arrays.sort(cars, Comparator.comparing(Car::getBrand, String.CASE_INSENSITIVE_ORDER).reversed());
        System.out.println("Сортування марок за спаданням (Я-А):");
        showAll();
    }

    public void showBrandsStartingWithT() {
        Pattern pattern = Pattern.compile("^T.*", Pattern.CASE_INSENSITIVE);
        boolean found = false;
        System.out.println("Автомобілі, марка яких починається з T:");
        for (Car car : cars) {
            if (pattern.matcher(car.getBrand()).matches()) {
                System.out.println(car);
                found = true;
            }
        }
        if (!found) System.out.println("Відповідних автомобілів не знайдено.");
    }

    public void showMinMaxPrice() {
        if (cars.length == 0) {
            System.out.println("Список автомобілів порожній.");
            return;
        }
        Car cheapest = cars[0], mostExpensive = cars[0];
        for (Car car : cars) {
            if (car.getPrice() < cheapest.getPrice()) cheapest = car;
            if (car.getPrice() > mostExpensive.getPrice()) mostExpensive = car;
        }
        System.out.printf("Найменшу ціну %.2f $ має %s %s.%n", cheapest.getPrice(), cheapest.getBrand(), cheapest.getModel());
        System.out.printf("Найбільшу ціну %.2f $ має %s %s.%n", mostExpensive.getPrice(), mostExpensive.getBrand(), mostExpensive.getModel());
    }

    // ЛР10. 1. Читання текстового файлу в масив об'єктів Car[].
    // Формат: марка;модель;рік;ціна (роздільник — крапка з комою).
    public void loadFromText(String fileName) throws IOException {
        List<Car> loaded = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Path.of(fileName), StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) continue;
                String[] parts = line.split(";", -1);
                if (parts.length != 4) {
                    throw new IOException("Неправильний формат рядка " + lineNumber + ": потрібно 4 поля.");
                }
                try {
                    String brand = parts[0].trim();
                    String model = parts[1].trim();
                    int year = Integer.parseInt(parts[2].trim());
                    double price = Double.parseDouble(parts[3].trim());
                    if (brand.isEmpty() || model.isEmpty() || year <= 0 || !Double.isFinite(price) || price < 0) {
                        throw new IllegalArgumentException("Некоректні значення");
                    }
                    loaded.add(new Car(brand, model, year, price));
                } catch (IllegalArgumentException e) {
                    throw new IOException("Помилка у рядку " + lineNumber + ": " + line, e);
                }
            }
        }
        cars = loaded.toArray(new Car[0]);
        System.out.println("З текстового файлу завантажено " + cars.length + " автомобілів.");
        showAll();
    }

    // ЛР10. 2. Сортування масиву і запис у нетекстовий (бінарний) файл.
    public void saveSortedToBinary(String fileName) throws IOException {
        Arrays.sort(cars, Comparator.comparingDouble(Car::getPrice));
        try (DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(fileName)))) {
            out.writeInt(cars.length);
            for (Car car : cars) {
                out.writeUTF(car.getBrand());
                out.writeUTF(car.getModel());
                out.writeInt(car.getYear());
                out.writeDouble(car.getPrice());
            }
        }
        System.out.println("Відсортований за ціною масив збережено в " + fileName);
        showAll();
    }

    // ЛР10. 3. Зчитування бінарного файлу й відтворення даних на екрані.
    public void showFromBinary(String fileName) throws IOException {
        List<Car> restored = new ArrayList<>();
        try (DataInputStream in = new DataInputStream(
                new BufferedInputStream(new FileInputStream(fileName)))) {
            int count = in.readInt();
            if (count < 0 || count > 100000) throw new IOException("Некоректна кількість автомобілів у файлі.");
            for (int i = 0; i < count; i++) {
                String brand = in.readUTF();
                String model = in.readUTF();
                int year = in.readInt();
                double price = in.readDouble();
                restored.add(new Car(brand, model, year, price));
            }
        }
        System.out.println("З бінарного файлу " + fileName + " відновлено:");
        for (int i = 0; i < restored.size(); i++) {
            System.out.println(i + ". " + restored.get(i));
        }
    }
}
