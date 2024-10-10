package backend.manager;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.Scanner;

import com.example.client_frontend.Room;

public class Manager {

    public static void main(String[] args) {

        try( Socket socket = new Socket("localhost", 11111);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            System.out.println("Hello new manager !");
            System.out.println("Manager | Connected to Master");

            List<Room> rooms = new ArrayList<>();

            // Read rooms from JSON
            JSONParser parser = new JSONParser();
            try (Reader reader = new FileReader("backend/manager/areas.json")) {
                JSONArray jsonArray = (JSONArray) parser.parse(reader);

                for (Object obj : jsonArray) {
                    JSONObject jsonObject = (JSONObject) obj;

                    String roomName = (String) jsonObject.get("roomName");
                    long noOfPersons = (long) jsonObject.get("noOfPersons");
                    String area = (String) jsonObject.get("area");
                    long stars = (long) jsonObject.get("stars");
                    long noOfReviews = (long) jsonObject.get("noOfReviews");
                    String roomImage = (String) jsonObject.get("roomImage");
                    // Retrieve startDate and endDate from JSON
                    String startDateStr = (String) jsonObject.get("startDate");
                    String endDateStr = (String) jsonObject.get("endDate");

                    // Parse startDate and endDate strings into LocalDate objects
                    LocalDate startDate = LocalDate.parse(startDateStr);
                    LocalDate endDate = LocalDate.parse(endDateStr);
                    rooms.add(new Room(roomName, (int) noOfPersons, area, (int) stars, (int) noOfReviews, roomImage,startDate,endDate));
                }
                System.out.println("Manager | Rooms read from JSON");
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("Manager | Choose an action:");
                System.out.println("1. View rooms");
                System.out.println("2. Sent rooms to Master");
                System.out.println("3. Add a new room & Send it to Master");


                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline character

                switch (choice) {
                    case 1:
                        // View rooms
                        System.out.println("Rooms:");
                        for (Room room : rooms) {
                            System.out.println(room);
                        }
                        break;
                    case 2:
                        // Sent rooms to Master

                        out.writeObject(rooms);
                        out.flush();
                        out.reset();


                        System.out.println("Manager | Rooms sent to Master successfully");
                        String response = (String) in.readObject();
                        System.out.println("Manager | Response from Master:");
                        System.out.println(response);
                        break;
                    case 3:
                        // Add a new room
                        System.out.println("Enter room details:");
                        System.out.print("Room Name: ");
                        String roomName = scanner.nextLine();

                        int numberOfPersons;
                        do {
                            System.out.print("Number of Persons (positive integer): ");
                            try {
                                numberOfPersons = scanner.nextInt();
                                scanner.nextLine(); // Consume newline character
                                if (numberOfPersons <= 0) {
                                    System.out.println("Number of Persons must be a positive integer.");
                                }
                            } catch (InputMismatchException e) {
                                System.out.println("Please enter a valid integer for Number of Persons.");
                                scanner.nextLine(); // Consume invalid input
                                numberOfPersons = 0;
                            }
                        } while (numberOfPersons <= 0);

                        String area;
                        do {
                            System.out.print("Area: ");
                            area = scanner.nextLine();
                            if (area.isEmpty()) {
                                System.out.println("Area cannot be empty. Please enter a valid area.");
                            }
                        } while (area.isEmpty());

                        int stars;
                        do {
                            System.out.print("Stars (1 to 5): ");
                            try {
                                stars = scanner.nextInt();
                                scanner.nextLine(); // Consume newline character
                                if (stars < 1 || stars > 5) {
                                    System.out.println("Stars must be between 1 and 5.");
                                }
                            } catch (InputMismatchException e) {
                                System.out.println("Please enter a valid integer for Stars.");
                                scanner.nextLine(); // Consume invalid input
                                stars = 0;
                            }
                        } while (stars < 1 || stars > 5);

                        int numberOfReviews;
                        do {
                            System.out.print("Number of Reviews (positive integer): ");
                            try {
                                numberOfReviews = scanner.nextInt();
                                scanner.nextLine(); // Consume newline character
                                if (numberOfReviews <= 0) {
                                    System.out.println("Number of Reviews must be a positive integer.");
                                }
                            } catch (InputMismatchException e) {
                                System.out.println("Please enter a valid integer for Number of Reviews.");
                                scanner.nextLine(); // Consume invalid input
                                numberOfReviews = 0;
                            }
                        } while (numberOfReviews <= 0);

                        System.out.print("Room Image: ");
                        String roomImage = scanner.nextLine();
                        LocalDate startDate = null;
                        LocalDate endDate = null;
                        boolean validDates = false;
                        while (!validDates) {
                            // Prompt for startDate
                            System.out.print("Start Date (YYYY-MM-DD): ");
                            String startDateStr = scanner.nextLine();
                            // Prompt for endDate
                            System.out.print("End Date (YYYY-MM-DD): ");
                            String endDateStr = scanner.nextLine();

                            try {
                                startDate = LocalDate.parse(startDateStr);
                                endDate = LocalDate.parse(endDateStr);
                                // Check if endDate is after startDate
                                if (endDate.isAfter(startDate)) {
                                    validDates = true;
                                } else {
                                    System.out.println("End Date must be after Start Date.");
                                }
                            } catch (DateTimeParseException e) {
                                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
                            }
                        }

                        Room newRoom = new Room(roomName, numberOfPersons, area, stars, numberOfReviews, roomImage, startDate, endDate);
                        rooms.add(newRoom);

                        // For debugging reasons
                        System.out.println("Rooms inside Manager");
                        for (Room room : rooms){
                            System.out.println(room.toString());
                        }

                        // Send updated rooms to Master
                        out.writeObject(rooms);
                        out.flush();
                        out.reset(); // Reset ObjectOutputStream

                        System.out.println("Manager | Room added and sent successfully");
                        response = (String) in.readObject();
                        System.out.println("Manager | Response from Master:");
                        System.out.println(response);
                        break;
                    default:
                        System.out.println("Manager | Invalid choice. Please enter a valid option.");
                }
                System.out.println("Manager | Do you want to make another request? (yes/no): ");
                String choice2 = scanner.next();
                if (!choice2.equalsIgnoreCase("yes")){
                    out.writeObject(null);
                    System.out.println("Manager | Exiting...");

                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}