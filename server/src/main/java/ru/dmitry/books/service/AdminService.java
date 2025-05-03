package ru.dmitry.books.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ru.dmitry.books.dto.AdminBookResponseDTO;
import ru.dmitry.books.dto.ExchangeStatsDTO;
import ru.dmitry.books.dto.GenreCreateDTO;
import ru.dmitry.books.dto.GenreUpdateDTO;
import ru.dmitry.books.model.BookEntity;
import ru.dmitry.books.model.ExchangeEntity.ExchangeStatus;
import ru.dmitry.books.model.GenreEntity;
import ru.dmitry.books.repository.BookRepository;
import ru.dmitry.books.repository.ExchangeRepository;
import ru.dmitry.books.repository.GenreRepository;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final BookRepository bookRepository;
    private final ExchangeRepository exchangeRepository;
    private final GenreRepository genreRepository;

    public List<AdminBookResponseDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::toAdminBookResponseDTO)
                .collect(Collectors.toList());
    }

    public ExchangeStatsDTO getExchangeStats() {
        long totalExchanges = exchangeRepository.count();
        long pendingExchanges = exchangeRepository.countByStatus(ExchangeStatus.PENDING);
        long proceedExchanges = exchangeRepository.countByStatus(ExchangeStatus.PROCEED);
        long completedExchanges = exchangeRepository.countByStatus(ExchangeStatus.COMPLETED);
        long deniedExchanges = exchangeRepository.countByStatus(ExchangeStatus.DENIED);

        ExchangeStatsDTO stats = new ExchangeStatsDTO();
        stats.setTotalExchanges(totalExchanges);
        stats.setPendingExchanges(pendingExchanges);
        stats.setProceedExchanges(proceedExchanges);
        stats.setCompletedExchanges(completedExchanges);
        stats.setDeniedExchanges(deniedExchanges);

        return stats;
    }

    @Transactional
    public void createGenre(GenreCreateDTO genreCreateDTO) {
        GenreEntity genre = new GenreEntity();
        genre.setName(genreCreateDTO.getName());
        genreRepository.save(genre);
    }

    @Transactional
    public void updateGenre(Long id, GenreUpdateDTO genreUpdateDTO) {
        GenreEntity genre = genreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Genre not found"));

        genre.setName(genreUpdateDTO.getName());
        genreRepository.save(genre);
    }

    @Transactional
    public void deleteGenre(Long id) {
        if (!genreRepository.existsById(id)) {
            throw new EntityNotFoundException("Genre not found");
        }
        genreRepository.deleteById(id);
    }

    private AdminBookResponseDTO toAdminBookResponseDTO(BookEntity book) {
        AdminBookResponseDTO dto = new AdminBookResponseDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setGenre(book.getGenre() != null ? book.getGenre().getName() : "Unknown");
        return dto;
    }
}