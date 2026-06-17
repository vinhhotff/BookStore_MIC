package com.example.bookstore.book;

import com.example.bookstore.book.BookResponse;
import com.example.bookstore.book.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bookstore.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Test
    @org.springframework.security.test.context.support.WithMockUser
    public void testGetAllBooks() throws Exception {
        // Giả lập (Mock) dữ liệu trả về từ Service
        when(bookService.getAllBooks()).thenReturn(List.of(
                BookResponse.builder().id(1L).title("Sách Test 1").build()
        ));

        // Bắn request giả (Mock Request) vào API và kiểm tra kết quả
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk());
    }
}
