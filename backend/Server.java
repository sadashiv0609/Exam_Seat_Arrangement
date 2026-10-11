package backend;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;

public class Server {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/generate", (HttpExchange exchange) -> {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                InputStream is = exchange.getRequestBody();
                Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");
                String requestBody = scanner.hasNext() ? scanner.next() : "";

                List<ExamGroup> groups = new ArrayList<>();
                List<Room> rooms = new ArrayList<>();
                String examType = "Exam";

                String[] sections = requestBody.split("===");
                for (String section : sections) {
                    String[] lines = section.trim().split("\n");
                    if (lines.length == 0) continue;

                    String sectionName = lines[0].trim();
                    if ("META".equalsIgnoreCase(sectionName) && lines.length > 1) {
                        examType = lines[1].trim();
                    } else if ("GROUPS".equalsIgnoreCase(sectionName)) {
                        for (int i = 1; i < lines.length; i++) {
                            String[] parts = lines[i].trim().split("\\|");
                            if (parts.length == 3) {
                                String dept = parts[0].trim();
                                String div = parts[1].trim();
                                String filePath = String.format("database/students_%s_%s.txt", dept, div);
                                groups.add(new ExamGroup(dept, div, parts[2].trim(), filePath));
                            }
                        }
                    } else if ("ROOMS".equalsIgnoreCase(sectionName)) {
                        for (int i = 1; i < lines.length; i++) {
                            String[] parts = lines[i].trim().split("\\|");
                            if (parts.length == 2) {
                                rooms.add(new Room(parts[0].trim(), Integer.parseInt(parts[1].trim())));
                            }
                        }
                    }
                }

                // Process dynamic list of groups
                List<SeatAllocation> fullList = SeatingEngine.interleave(groups);
                Map<String, List<SeatAllocation>> roomAllocations = SeatingEngine.partitionRooms(fullList, rooms);

                StringBuilder json = new StringBuilder("{");
                int rIdx = 0;
                for (var entry : roomAllocations.entrySet()) {
                    if (rIdx++ > 0) json.append(",");
                    json.append("\"").append(entry.getKey()).append("\":[");
                    for (int s = 0; s < entry.getValue().size(); s++) {
                        if (s > 0) json.append(",");
                        json.append(entry.getValue().get(s).toJson());
                    }
                    json.append("]");
                }
                json.append("}");

                byte[] bytes = json.toString().getBytes();
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, bytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();
            }
        });

        System.out.println("Seating API running at http://localhost:8080/generate");
        server.start();
    }
}