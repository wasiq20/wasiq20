import java.io.*;
import java.util.*;

// Model class representing a Student
class Student implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private double gpa;

    public Student(String id, String name, double gpa) {
        this.id = id;
        this.name = name;
        this.gpa = gpa;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getGpa() { return gpa; }

    public void setName(String name) { this.name = name; }
    public void setGpa(double gpa) { this.gpa = gpa; }

    @Override
    public String toString() {
        return String.format("| %-10s | %-20s | %-6.2f |", id, name, gpa);
    }
}

// Controller managing persistence and CRUD operations
class StudentManager {
    private List<Student> students;
    private final String FILE_NAME = "students.dat";

    public StudentManager() {
        students = loadFromFile();
    }

    public void addStudent(String id, String name, double gpa) {
        for (Student s : students) {
            if (s.getId().equalsIgnoreCase(id)) {
                System.out.println("Error: Student with ID " + id + " already exists!");
                return;
            }
        }
        students.add(new Student(id, name, gpa));
        saveToFile();
        System.out.println("Student added successfully!");
    }

    public void displayAll() {
        if (students.isEmpty()) {
            System.out.println("No records found.");
            return;
        }
        System.out.println("+------------+----------------------+--------+");
        System.out.println("| Student ID | Name                 | GPA    |");
        System.out.println("+------------+----------------------+--------+");
        for (Student s : students) {
            System.out.println(s);
        }
        System.out.println("+------------+----------------------+--------+");
    }

    public void searchStudent(String query) {
        boolean found = false;
        for (Student s : students) {
            if (s.getId().equalsIgnoreCase(query) || s.getName().toLowerCase().contains(query.toLowerCase())) {
                System.out.println(s);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No student found matching: " + query);
        }
    }

    public void deleteStudent(String id) {
        boolean removed = students.removeIf(s -> s.getId().equalsIgnoreCase(id));
        if (removed) {
            saveToFile();
            System.out.println("Student deleted successfully!");
        } else {
            System.out.println("Student with ID " + id + " not found.");
        }
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(students);
        } catch (IOException e) {
            System.out.println("Error saving records: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private List<Student> loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Student>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}

// Entry point with interactive CLI
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        StudentManager manager = new StudentManager();

        while (true) {
            System.out.println("\n--- Student Record Management System ---");
            System.out.println("1. Add Student");
            System.out.println("2. Display All Students");
            System.out.println("3. Search Student");
            System.out.println("4. Delete Student");
            System.out.println("5. Exit");
            System.out.print("Choose an option (1-5): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.print("Enter Student ID: ");
                    String id = scanner.nextLine().trim();
                    System.out.print("Enter Student Name: ");
                    String name = scanner.nextLine().trim();
                    System.out.print("Enter GPA: ");
                    try {
                        double gpa = Double.parseDouble(scanner.nextLine().trim());
                        manager.addStudent(id, name, gpa);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid GPA format.");
                    }
                    break;
                case "2":
                    manager.displayAll();
                    break;
                case "3":
                    System.out.print("Enter Student ID or Name to search: ");
                    String query = scanner.nextLine().trim();
                    manager.searchStudent(query);
                    break;
                case "4":
                    System.out.print("Enter Student ID to delete: ");
                    String deleteId = scanner.nextLine().trim();
                    manager.deleteStudent(deleteId);
                    break;
                case "5":
                    System.out.println("Exiting system. Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}  