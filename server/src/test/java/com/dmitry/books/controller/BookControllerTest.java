package com.dmitry.books.controller;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.dmitry.books.dto.BookRatingDTO;
import com.dmitry.books.dto.BookResponseDTO;
import com.dmitry.books.dto.PageDTO;
import com.dmitry.books.exception.GlobalExceptionHandler;
import com.dmitry.books.service.BookService;
import com.dmitry.books.service.ReviewService;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookService bookService;

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private BookController bookController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testGetBooks() throws Exception {
        PageDTO<BookResponseDTO> page = new PageDTO<>();
        Mockito.when(bookService.getBooksByFilter(any(), any(), any(), any()))
                .thenReturn(page);

        mockMvc.perform(get("/book")
            .param("author", "John")
            .param("genreId", "1")
            .param("title", "Java")
            .param("page", "0")
            .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void testGetBookById() throws Exception {
        BookResponseDTO book = new BookResponseDTO();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAuthor("John Doe");
        Mockito.when(bookService.getBookById(1L)).thenReturn(Optional.of(book));

        mockMvc.perform(get("/book/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.author").value("John Doe"));
    }

    @Test
    void testGetBookById_NotFound() throws Exception {
        Mockito.when(bookService.getBookById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/book/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    void testGetBookRatingById() throws Exception {
        BookRatingDTO bookRating = new BookRatingDTO();
        bookRating.setAverageRating(10D);
        bookRating.setBookId(1L);
        bookRating.setReviewCount(2L);

        Mockito.when(reviewService.getBookRatingById(1L)).thenReturn(Optional.of(bookRating));
        mockMvc.perform(get("/book/rating/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookId").value(1))
                .andExpect(jsonPath("$.averageRating").value(10))
                .andExpect(jsonPath("$.reviewCount").value(2));
        }

    @Test
    void testCreateBook() throws Exception {
        mockMvc.perform(post("/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "title": "New Book",
                            "author": "Jane Doe",
                            "description": "Description",
                            "genreId": 1
                        }
                        """))
                .andExpect(status().isCreated());
    }

    @Test
    void testUpdateBook() throws Exception {
        mockMvc.perform(put("/book/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "title": "Updated Book",
                            "author": "Jane Doe",
                            "description": "Updated Description",
                            "genreId": 1
                        }
                        """))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteBook() throws Exception {
        mockMvc.perform(delete("/book/1"))
                .andExpect(status().isNoContent());
    }

}