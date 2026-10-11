package backend;

import java.io.*;
import java.util.*;

public class SeatingEngine {

    public static List<Student> loadStudents(ExamGroup group) {
        List<Student> list = new ArrayList<>();
        File file = new File(group.getStudentFilePath());
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                list.add(new Student(parts[0].trim(), parts[1].trim(), group.getDepartment(), group.getDivision()));
            }
        } catch (Exception e) {}
        return list;
    }

    public static List<SeatAllocation> interleave(List<ExamGroup> groups) {
        // Load all groups into separate Queues
        List<Queue<Student>> queues = new ArrayList<>();
        for (ExamGroup g : groups) {
            Queue<Student> q = new LinkedList<>(loadStudents(g));
            if (!q.isEmpty()) {
                queues.add(q);
            }
        }

        List<SeatAllocation> combined = new ArrayList<>();
        int bench = 1;

        // Pointers tracking which group is currently filling Seat 1 (Left) and Seat 2 (Right)
        int leftIndex = 0;
        int rightIndex = 1;

        while (leftIndex < queues.size() || rightIndex < queues.size()) {
            Student s1 = null;
            Student s2 = null;

            // 1. Process Seat 1 (Left Seat)
            if (leftIndex < queues.size()) {
                Queue<Student> leftQ = queues.get(leftIndex);
                s1 = leftQ.poll();
                
                // If this group is now empty, find the next unassigned group
                if (leftQ.isEmpty()) {
                    leftIndex = Math.max(leftIndex, rightIndex) + 1;
                }
            }

            // 2. Process Seat 2 (Right Seat)
            if (rightIndex < queues.size()) {
                Queue<Student> rightQ = queues.get(rightIndex);
                s2 = rightQ.poll();
                
                // If this group is now empty, find the next unassigned group
                if (rightQ.isEmpty()) {
                    rightIndex = Math.max(leftIndex, rightIndex) + 1;
                }
            }

            // If both run out, we are completely done
            if (s1 == null && s2 == null) {
                break;
            }

            // Display cleanup: If the left seat is empty but right seat has a student,
            // shift the student to the left seat so the table looks clean.
            //if (s1 == null && s2 != null) {
            //    s1 = s2;
            //    s2 = null;
            //}

            combined.add(new SeatAllocation(bench++, s1, s2));
        }
        
        return combined;
    }

    public static Map<String, List<SeatAllocation>> partitionRooms(
            List<SeatAllocation> seating, List<Room> rooms) {

        Map<String, List<SeatAllocation>> roomMap = new LinkedHashMap<>();
        int cursor = 0;
        int totalBenches = seating.size();

        for (Room room : rooms) {
            List<SeatAllocation> roomAlloc = new ArrayList<>();
            for (int k = 0; k < room.getCapacity() && cursor < totalBenches; k++) {
                SeatAllocation original = seating.get(cursor++);
                roomAlloc.add(new SeatAllocation(k + 1, original.getStudent1(), original.getStudent2()));
            }
            roomMap.put(room.getRoomName(), roomAlloc);
            if (cursor >= totalBenches) break;
        }

        return roomMap;
    }
}