import java.util.*;
import java.time.*;
import java.time.format.*;
import java.time.temporal.ChronoUnit;

public class HotelBookingApp {

    // ─── DATA MODELS ───────────────────────────────────────────────────────────

    static class Hotel {
        int id;
        String name;
        String location;
        String type;
        int stars;
        double rating;
        String emoji;
        Map<String, Double> roomPrices = new LinkedHashMap<>();

        Hotel(int id, String name, String location, String type, int stars, double rating, String emoji) {
            this.id = id;
            this.name = name;
            this.location = location;
            this.type = type;
            this.stars = stars;
            this.rating = rating;
            this.emoji = emoji;
        }

        void addRoom(String type, double price) {
            roomPrices.put(type, price);
        }

        String getStars() {
            return "★".repeat(stars) + "☆".repeat(5 - stars);
        }

        void display() {
            System.out.printf("  [%d] %s %s%n", id, emoji, name);
            System.out.printf("      Location : %s%n", location);
            System.out.printf("      Type     : %s | Stars: %s | Rating: %.1f/5%n", type, getStars(), rating);
            System.out.println("      Rooms    :");
            roomPrices.forEach((r, p) ->
                System.out.printf("               - %-20s : ₹%.0f/night%n", r, p));
        }
    }

    static class Booking {
        static int counter = 1000;
        int bookingId;
        String guestName;
        String email;
        String phone;
        Hotel hotel;
        String roomType;
        LocalDate checkIn;
        LocalDate checkOut;
        int guests;
        double totalAmount;
        String status;
        LocalDateTime bookedAt;

        Booking(String guestName, String email, String phone, Hotel hotel,
                String roomType, LocalDate checkIn, LocalDate checkOut, int guests) {
            this.bookingId = ++counter;
            this.guestName = guestName;
            this.email = email;
            this.phone = phone;
            this.hotel = hotel;
            this.roomType = roomType;
            this.checkIn = checkIn;
            this.checkOut = checkOut;
            this.guests = guests;
            this.status = "Confirmed";
            this.bookedAt = LocalDateTime.now();
            calculateTotal();
        }

        void calculateTotal() {
            long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
            double nightly = hotel.roomPrices.get(roomType);
            double subtotal = nights * nightly;
            double tax = subtotal * 0.12;
            this.totalAmount = subtotal + tax;
        }

        long getNights() {
            return ChronoUnit.DAYS.between(checkIn, checkOut);
        }

        double getNightlyRate() {
            return hotel.roomPrices.get(roomType);
        }

        void display() {
            String line = "─".repeat(52);
            System.out.println("\n  ┌" + line + "┐");
            System.out.printf("  │  BOOKING CONFIRMATION #SRN-%d%s│%n", bookingId, " ".repeat(18));
            System.out.println("  ├" + line + "┤");
            System.out.printf("  │  Hotel    : %-38s│%n", hotel.emoji + " " + hotel.name);
            System.out.printf("  │  Location : %-38s│%n", hotel.location);
            System.out.printf("  │  Room     : %-38s│%n", roomType);
            System.out.printf("  │  Guest    : %-38s│%n", guestName);
            System.out.printf("  │  Email    : %-38s│%n", email);
            System.out.printf("  │  Phone    : %-38s│%n", phone);
            System.out.printf("  │  Guests   : %-38s│%n", guests);
            System.out.printf("  │  Check-in : %-38s│%n", checkIn.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
            System.out.printf("  │  Check-out: %-38s│%n", checkOut.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
            System.out.printf("  │  Nights   : %-38s│%n", getNights());
            System.out.println("  ├" + line + "┤");
            System.out.printf("  │  Nightly rate : ₹%-33.0f│%n", getNightlyRate());
            System.out.printf("  │  Subtotal     : ₹%-33.0f│%n", getNights() * getNightlyRate());
            System.out.printf("  │  Tax (12%%)    : ₹%-33.0f│%n", getNights() * getNightlyRate() * 0.12);
            System.out.printf("  │  TOTAL        : ₹%-33.0f│%n", totalAmount);
            System.out.println("  ├" + line + "┤");
            System.out.printf("  │  Status   : %-38s│%n", "✅ " + status);
            System.out.printf("  │  Booked at: %-38s│%n",
                bookedAt.format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")));
            System.out.println("  └" + line + "┘");
        }
    }

    // ─── APP STATE ─────────────────────────────────────────────────────────────

    static List<Hotel> hotels = new ArrayList<>();
    static List<Booking> bookings = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);

    // ─── MAIN ──────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        seedData();
        printBanner();

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("  Enter choice: ");
            switch (choice) {
                case 1 -> searchAndBook();
                case 2 -> viewMyBookings();
                case 3 -> cancelBooking();
                case 4 -> browseByCategory();
                case 5 -> { printGoodbye(); running = false; }
                default -> System.out.println("\n  ⚠  Invalid option. Please try again.\n");
            }
        }
        sc.close();
    }

    // ─── MENUS ─────────────────────────────────────────────────────────────────

    static void printBanner() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════╗");
        System.out.println("  ║        🏨  SERENOSTAYS BOOKING           ║");
        System.out.println("  ║   Find & book your perfect stay          ║");
        System.out.println("  ╚══════════════════════════════════════════╝");
        System.out.println();
    }

    static void printMainMenu() {
        System.out.println("  ┌─────────────────────────────────┐");
        System.out.println("  │          MAIN MENU              │");
        System.out.println("  ├─────────────────────────────────┤");
        System.out.println("  │  1. Search & Book a Hotel       │");
        System.out.println("  │  2. View My Bookings            │");
        System.out.println("  │  3. Cancel a Booking            │");
        System.out.println("  │  4. Browse by Category          │");
        System.out.println("  │  5. Exit                        │");
        System.out.println("  └─────────────────────────────────┘");
    }

    static void printGoodbye() {
        System.out.println("\n  Thank you for using SerenoStays. Safe travels! ✈️\n");
    }

    // ─── FEATURE 1: SEARCH & BOOK ──────────────────────────────────────────────

    static void searchAndBook() {
        System.out.println("\n  ═══ SEARCH HOTELS ═══");
        System.out.print("  Enter destination (city/country) or press Enter to see all: ");
        String dest = sc.nextLine().trim().toLowerCase();

        List<Hotel> results = dest.isEmpty()
            ? new ArrayList<>(hotels)
            : hotels.stream()
                .filter(h -> h.location.toLowerCase().contains(dest) ||
                             h.name.toLowerCase().contains(dest))
                .toList();

        if (results.isEmpty()) {
            System.out.println("\n  No hotels found for \"" + dest + "\". Try a different search.\n");
            return;
        }

        System.out.printf("%n  Found %d hotel(s):%n%n", results.size());
        results.forEach(h -> { h.display(); System.out.println(); });

        System.out.print("  Enter Hotel ID to book (or 0 to go back): ");
        int hotelId = readInt("");
        if (hotelId == 0) return;

        Hotel chosen = results.stream().filter(h -> h.id == hotelId).findFirst().orElse(null);
        if (chosen == null) {
            System.out.println("\n  ⚠  Hotel ID not found.\n");
            return;
        }

        bookHotel(chosen);
    }

    static void bookHotel(Hotel hotel) {
        System.out.println("\n  ═══ BOOK: " + hotel.emoji + " " + hotel.name + " ═══");

        // Guest details
        System.out.print("  Full name      : ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) { System.out.println("  ⚠  Name cannot be empty."); return; }

        System.out.print("  Email address  : ");
        String email = sc.nextLine().trim();

        System.out.print("  Phone number   : ");
        String phone = sc.nextLine().trim();

        // Dates
        LocalDate checkIn = readDate("  Check-in date  (dd-MM-yyyy): ");
        if (checkIn == null) return;
        LocalDate checkOut = readDate("  Check-out date (dd-MM-yyyy): ");
        if (checkOut == null) return;

        if (!checkOut.isAfter(checkIn)) {
            System.out.println("\n  ⚠  Check-out must be after check-in.\n");
            return;
        }

        System.out.print("  Number of guests: ");
        int guests = readInt("");
        if (guests < 1 || guests > 10) {
            System.out.println("  ⚠  Guests must be between 1 and 10.");
            return;
        }

        // Room selection
        System.out.println("\n  Available rooms:");
        List<String> roomList = new ArrayList<>(hotel.roomPrices.keySet());
        for (int i = 0; i < roomList.size(); i++) {
            System.out.printf("    %d. %-20s ₹%.0f/night%n",
                i + 1, roomList.get(i), hotel.roomPrices.get(roomList.get(i)));
        }
        System.out.print("  Select room (1-" + roomList.size() + "): ");
        int roomChoice = readInt("");
        if (roomChoice < 1 || roomChoice > roomList.size()) {
            System.out.println("  ⚠  Invalid room selection.");
            return;
        }
        String roomType = roomList.get(roomChoice - 1);

        // Preview cost
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        double rate = hotel.roomPrices.get(roomType);
        double subtotal = nights * rate;
        double tax = subtotal * 0.12;
        double total = subtotal + tax;

        System.out.println("\n  ─── Cost Summary ───────────────────");
        System.out.printf("  %d night(s) × ₹%.0f = ₹%.0f%n", nights, rate, subtotal);
        System.out.printf("  Tax (12%%)          = ₹%.0f%n", tax);
        System.out.printf("  Total              = ₹%.0f%n", total);
        System.out.println("  ────────────────────────────────────");

        System.out.print("\n  Confirm booking? (yes/no): ");
        String confirm = sc.nextLine().trim().toLowerCase();
        if (!confirm.equals("yes") && !confirm.equals("y")) {
            System.out.println("\n  Booking cancelled.\n");
            return;
        }

        Booking b = new Booking(name, email, phone, hotel, roomType, checkIn, checkOut, guests);
        bookings.add(b);
        b.display();
        System.out.println("\n  🎉 Booking confirmed! Your ID is SRN-" + b.bookingId + "\n");
    }

    // ─── FEATURE 2: VIEW BOOKINGS ──────────────────────────────────────────────

    static void viewMyBookings() {
        System.out.println("\n  ═══ MY BOOKINGS ═══");
        if (bookings.isEmpty()) {
            System.out.println("  No bookings found. Make your first booking!\n");
            return;
        }
        System.out.printf("  %-10s %-22s %-18s %-12s %-12s %s%n",
            "ID", "Hotel", "Guest", "Check-in", "Check-out", "Total");
        System.out.println("  " + "─".repeat(88));
        bookings.forEach(b -> System.out.printf(
            "  SRN-%-6d %-22s %-18s %-12s %-12s ₹%.0f%n",
            b.bookingId,
            truncate(b.hotel.name, 20),
            truncate(b.guestName, 16),
            b.checkIn.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
            b.checkOut.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
            b.totalAmount));
        System.out.println();

        System.out.print("  Enter booking ID to view details (or 0 to go back): ");
        int bid = readInt("");
        if (bid == 0) return;
        bookings.stream()
            .filter(b -> b.bookingId == bid)
            .findFirst()
            .ifPresentOrElse(Booking::display,
                () -> System.out.println("  ⚠  Booking not found.\n"));
        System.out.println();
    }

    // ─── FEATURE 3: CANCEL BOOKING ────────────────────────────────────────────

    static void cancelBooking() {
        System.out.println("\n  ═══ CANCEL BOOKING ═══");
        System.out.print("  Enter booking ID (e.g. 1001): ");
        int bid = readInt("");
        Booking b = bookings.stream().filter(bk -> bk.bookingId == bid).findFirst().orElse(null);
        if (b == null) {
            System.out.println("  ⚠  Booking ID not found.\n");
            return;
        }
        if (b.status.equals("Cancelled")) {
            System.out.println("  ⚠  This booking is already cancelled.\n");
            return;
        }
        System.out.printf("%n  Booking  : SRN-%d — %s%n", b.bookingId, b.hotel.name);
        System.out.printf("  Guest    : %s%n", b.guestName);
        System.out.printf("  Dates    : %s → %s%n",
            b.checkIn.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
            b.checkOut.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        System.out.printf("  Amount   : ₹%.0f%n", b.totalAmount);
        System.out.print("\n  Are you sure you want to cancel? (yes/no): ");
        String conf = sc.nextLine().trim().toLowerCase();
        if (conf.equals("yes") || conf.equals("y")) {
            b.status = "Cancelled";
            System.out.println("\n  ✅ Booking SRN-" + b.bookingId + " has been cancelled.\n");
        } else {
            System.out.println("\n  Cancellation aborted.\n");
        }
    }

    // ─── FEATURE 4: BROWSE BY CATEGORY ────────────────────────────────────────

    static void browseByCategory() {
        System.out.println("\n  ═══ BROWSE BY CATEGORY ═══");
        System.out.println("  1. Beach");
        System.out.println("  2. Mountain");
        System.out.println("  3. City");
        System.out.println("  4. Boutique");
        System.out.println("  5. Resort");
        System.out.println("  6. All hotels");
        int c = readInt("  Select category: ");
        String cat = switch (c) {
            case 1 -> "Beach";
            case 2 -> "Mountain";
            case 3 -> "City";
            case 4 -> "Boutique";
            case 5 -> "Resort";
            case 6 -> "";
            default -> null;
        };
        if (cat == null) { System.out.println("  ⚠  Invalid choice.\n"); return; }

        List<Hotel> list = cat.isEmpty() ? hotels
            : hotels.stream().filter(h -> h.type.equalsIgnoreCase(cat)).toList();

        System.out.printf("%n  %d hotel(s) found:%n%n", list.size());
        list.forEach(h -> { h.display(); System.out.println(); });
    }

    // ─── HELPERS ───────────────────────────────────────────────────────────────

    static int readInt(String prompt) {
        if (!prompt.isEmpty()) System.out.print(prompt);
        try {
            int v = Integer.parseInt(sc.nextLine().trim());
            return v;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    static LocalDate readDate(String prompt) {
        System.out.print(prompt);
        try {
            return LocalDate.parse(sc.nextLine().trim(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        } catch (DateTimeParseException e) {
            System.out.println("  ⚠  Invalid date format. Use dd-MM-yyyy (e.g. 10-07-2026).");
            return null;
        }
    }

    static String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }

    // ─── SEED DATA ─────────────────────────────────────────────────────────────

    static void seedData() {
        Hotel h1 = new Hotel(1, "The Jade Cove", "Seminyak, Bali", "Beach", 5, 4.9, "🌴");
        h1.addRoom("Standard Room",   8400);
        h1.addRoom("Deluxe Room",    11200);
        h1.addRoom("Suite",          16800);

        Hotel h2 = new Hotel(2, "Horizon Sky Hotel", "Mumbai, India", "City", 4, 4.7, "🏙️");
        h2.addRoom("Standard Room",  4200);
        h2.addRoom("Deluxe Room",    5800);
        h2.addRoom("Suite",          9000);

        Hotel h3 = new Hotel(3, "Snowpeak Lodge", "Manali, Himachal Pradesh", "Mountain", 4, 4.8, "⛰️");
        h3.addRoom("Standard Room",  3600);
        h3.addRoom("Deluxe Room",    4900);
        h3.addRoom("Suite",          7500);

        Hotel h4 = new Hotel(4, "Pearl Bay Resort", "Maldives", "Resort", 5, 4.9, "🏝️");
        h4.addRoom("Water Villa",   22000);
        h4.addRoom("Beach Suite",  35000);

        Hotel h5 = new Hotel(5, "Kala Boutique Inn", "Jaipur, Rajasthan", "Boutique", 3, 4.6, "🏡");
        h5.addRoom("Heritage Room",  2800);
        h5.addRoom("Royal Suite",    4500);

        Hotel h6 = new Hotel(6, "Urban Nest", "Bengaluru, India", "City", 4, 4.5, "🏨");
        h6.addRoom("Standard Room",  3200);
        h6.addRoom("Deluxe Room",    4400);

        Hotel h7 = new Hotel(7, "Sunrise Cliff Villa", "Santorini, Greece", "Beach", 5, 5.0, "🌅");
        h7.addRoom("Cliff Room",    15000);
        h7.addRoom("Infinity Suite",24000);

        Hotel h8 = new Hotel(8, "Forest Bloom Retreat", "Coorg, Karnataka", "Mountain", 4, 4.7, "🌿");
        h8.addRoom("Forest Cottage",  4800);
        h8.addRoom("Premium Cottage", 7200);

        hotels.addAll(List.of(h1, h2, h3, h4, h5, h6, h7, h8));
    }
}
