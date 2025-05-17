package com.dmitry.books.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dmitry.books.dto.ExchangeStatusStatDTO;
import com.dmitry.books.dto.GenreCreateDTO;
import com.dmitry.books.model.BookEntity;
import com.dmitry.books.model.GenreEntity;
import com.dmitry.books.repository.BookRepository;
import com.dmitry.books.repository.ExchangeRepository;
import com.dmitry.books.repository.GenreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final BookRepository bookRepository;
    private final ExchangeRepository exchangeRepository;
    private final GenreRepository genreRepository;

    public List<BookEntity> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<ExchangeStatusStatDTO> getExchangeStats() {
        return exchangeRepository.countExchangesByStatus().stream()
                .map(obj -> new ExchangeStatusStatDTO(
                        (Integer) obj[0],
                        (Long) obj[1]
                ))
                .collect(Collectors.toList());
    }

    public void addGenre(GenreCreateDTO dto) {
        GenreEntity genre = new GenreEntity();
        genre.setName(dto.getName());
        genreRepository.save(genre);
    }
}