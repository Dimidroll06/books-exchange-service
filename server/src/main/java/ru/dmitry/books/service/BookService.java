package ru.dmitry.books.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ru.dmitry.books.dto.BookCreateDTO;
import ru.dmitry.books.dto.BookResponseDTO;
import ru.dmitry.books.model.BookEntity;
import ru.dmitry.books.model.GenreEntity;
import ru.dmitry.books.repository.BookRepository;
import ru.dmitry.books.repository.GenreRepository;

/**
 * 
 * @author dmitry
 */
@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final GenreRepository genreRepository;

    // Create
    public BookResponseDTO createBook(BookCreateDTO dto) {
        BookEntity book = toEntity(dto);
        BookEntity savedBook = bookRepository.save(book);

        return toDto(savedBook);
    }

    // Get
    public BookResponseDTO getBookById(Long id) {
        BookEntity book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Book not found"));
        return toDto(book);
    }

    // public List<BookResponseDTO> getBooksByFilter(String filter) {
    // List<BookResponseDTO> books = new ArrayList<>();
    // for (BookEntity book : bookRepository.findAll()) {
    // if (book.getAuthor().contains(filter) || book.getTitle().contains(filter)) {
    // books.add(toDto(book));
    // }
    // }

    // return books;
    // }

    // public List<BookResponseDTO> getBooksByFilter(Long genreId) {
    // List<BookResponseDTO> books = new ArrayList<>();
    // for (BookEntity book : bookRepository.findAll()) {
    // if (Objects.equals(book.getGenre().getId(), genreId)) {
    // books.add(toDto(book));
    // }
    // }

    // return books;
    // }

    public Page<BookResponseDTO> findBooksByFilters(String title, String author, Long genreId, Pageable pageable) {
        if (genreId != null) {
            genreRepository.findById(genreId)
                    .orElseThrow(() -> new EntityNotFoundException("Genre not found"));
        }

        Page<BookEntity> books = bookRepository.findByFilters(
                title, author, genreId, pageable);

        return books.map(this::toDto);
    }

    // Update
    @Transactional
    public BookResponseDTO updateBook(Long id, BookCreateDTO dto) {
        BookEntity book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Book not found"));

        book.setTitle(dto.title());
        book.setAuthor(dto.author());
        book.setDescription(dto.description());

        GenreEntity genre = genreRepository.findById(dto.genreId()).orElseThrow(
                () -> new EntityNotFoundException("Genre not found"));

        book.setGenre(genre);

        return toDto(book);
    }

    // Udalit
    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException("Book not found");
        }

        bookRepository.deleteById(id);
    }

    // ДТОШЕЧКИ
    private BookResponseDTO toDto(BookEntity book) {
        return new BookResponseDTO(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getDescription(),
                book.getGenre().getName());
    }

    private BookEntity toEntity(BookCreateDTO dto) {
        BookEntity book = new BookEntity();
        book.setTitle(dto.title());
        book.setAuthor(dto.author());
        book.setDescription(dto.description());
        GenreEntity genre = genreRepository.findById(dto.genreId()).orElseThrow(
                () -> new EntityNotFoundException("Genre with this id is not found"));

        book.setGenre(genre);

        return book;
    }
}
