package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.exception.ControllerExceptionHandler;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    private BookingService bookingService;
    private MockMvc mockMvc;
    private ObjectMapper mapper;

    @BeforeEach
    public void initialize() {
        bookingService = Mockito.mock(BookingService.class);
        BookingController bookingController = new BookingController(bookingService);
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController)
                .setControllerAdvice(ControllerExceptionHandler.class)
                .build();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void createBookingTest() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.now().plusSeconds(2));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(10));

        Item item = new Item();
        item.setId(1L);
        item.setOwner(new User(1L, "owner", "owner@mail.ru"));

        User booker = new User(2L, "booker", "booker@mail.ru");

        bookingDto.setItem(item);
        bookingDto.setBooker(booker);

        Mockito
                .when(bookingService.createBooking(bookingDto, 2L))
                .thenReturn(bookingDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .header("X-Sharer-User-Id", 2L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(bookingDto.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.start", Matchers.is(bookingDto.getStart().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.end", Matchers.is(bookingDto.getEnd().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.item.name", Matchers.is(bookingDto.getItem().getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.booker.name", Matchers.is(bookingDto.getBooker().getName())));
    }

    @Test
    public void patchBookingTest() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.now().plusSeconds(2));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(10));

        Item item = new Item();
        item.setId(1L);
        item.setOwner(new User(1L, "owner", "owner@mail.ru"));

        User booker = new User(2L, "booker", "booker@mail.ru");

        bookingDto.setItem(item);
        bookingDto.setBooker(booker);

        Mockito
                .when(bookingService.patchBooking(1L, true, 1L))
                .thenReturn(bookingDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(bookingDto.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.start", Matchers.is(bookingDto.getStart().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.end", Matchers.is(bookingDto.getEnd().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.item.name", Matchers.is(bookingDto.getItem().getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.booker.name", Matchers.is(bookingDto.getBooker().getName())));
    }

    @Test
    public void getBookingByIdTest() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.now().plusSeconds(2));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(10));

        Item item = new Item();
        item.setId(1L);
        item.setOwner(new User(1L, "owner", "owner@mail.ru"));

        User booker = new User(2L, "booker", "booker@mail.ru");

        bookingDto.setItem(item);
        bookingDto.setBooker(booker);

        Mockito
                .when(bookingService.getBookingById(1L, 1L))
                .thenReturn(bookingDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(bookingDto.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.start", Matchers.is(bookingDto.getStart().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.end", Matchers.is(bookingDto.getEnd().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.item.name", Matchers.is(bookingDto.getItem().getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.booker.name", Matchers.is(bookingDto.getBooker().getName())));
    }

    @Test
    public void getCurrentUserBookingsTest() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.now().plusSeconds(2));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(10));

        Item item = new Item();
        item.setId(1L);
        item.setOwner(new User(1L, "owner", "owner@mail.ru"));

        User booker = new User(2L, "booker", "booker@mail.ru");

        bookingDto.setItem(item);
        bookingDto.setBooker(booker);

        Mockito
                .when(bookingService.getCurrentUserBookings(State.WAITING, 1L,  null, null))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings?state=WAITING")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(bookingDto.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].start", Matchers.is(bookingDto.getStart().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].end", Matchers.is(bookingDto.getEnd().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].item.name", Matchers.is(bookingDto.getItem().getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].booker.name", Matchers.is(bookingDto.getBooker().getName())));
    }

    @Test
    public void getOwnerBookings() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.now().plusSeconds(2));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(10));

        Item item = new Item();
        item.setId(1L);
        item.setOwner(new User(1L, "owner", "owner@mail.ru"));

        User booker = new User(2L, "booker", "booker@mail.ru");

        bookingDto.setItem(item);
        bookingDto.setBooker(booker);

        Mockito
                .when(bookingService.getOwnerBookings(State.WAITING,1L, null, null))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner?state=WAITING")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(bookingDto.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].start", Matchers.is(bookingDto.getStart().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].end", Matchers.is(bookingDto.getEnd().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].item.name", Matchers.is(bookingDto.getItem().getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].booker.name", Matchers.is(bookingDto.getBooker().getName())));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner?state=UNSUPPORTED_STATUS")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
