package com.example.event.app;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    // Show all bookings
    @GetMapping
    public String listBookings(Model model) {
        model.addAttribute("bookings", bookingRepository.findAll());
        return "bookings-list";
    }

    // Form to create a new booking
    @GetMapping("/new/{eventId}")
    public String newBookingForm(@PathVariable Long eventId, Model model) {
        Event event = eventRepository.findById(eventId).orElse(null);

        if (event == null || event.getAvailableSeats() <= 0) {
            model.addAttribute("errorMessage", "Sorry, no seats available for this event.");
            return "error";
        }

        Booking booking = new Booking();
        booking.setEvent(event);
        model.addAttribute("booking", booking);
        return "booking-form";
    }

    // Save booking with notification and success message
    @PostMapping("/save")
    public String saveBooking(@ModelAttribute Booking booking, RedirectAttributes redirectAttributes,Model model) {
        Event event = eventRepository.findById(booking.getEvent().getId()).orElse(null);

        if (event != null && event.getAvailableSeats() >= booking.getSeats()) {

    event.setAvailableSeats(
        event.getAvailableSeats() - booking.getSeats()
    );

    eventRepository.save(event);

    bookingRepository.save(booking);
    System.out.println("Booking saved. ID = " + booking.getId());

            // create notification
            String message = "🎉 You successfully booked " +
                 booking.getSeats() +
                 " seat(s) for '" +
                 event.getName() +
                 "'.";
            Notification notification = new Notification(message);
            notificationRepository.save(notification);

            // flash message for user
            redirectAttributes.addFlashAttribute("successMessage", message);
        }

        model.addAttribute("booking", booking);
model.addAttribute("event", event);

int amount = booking.getSeats() * 200;
model.addAttribute("amount", amount);
System.out.println("Returning ticket page");
return "ticket"; // user will see success message on events page
    }
}
