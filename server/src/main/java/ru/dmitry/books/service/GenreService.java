package ru.dmitry.books.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ru.dmitry.books.dto.GenreDTO;
import ru.dmitry.books.model.GenreEntity;
import ru.dmitry.books.repository.GenreRepository;

/**
 *
 * @author dmitry
 */
@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;

    public GenreEntity createEntity(GenreDTO dto) {
        GenreEntity genre = new GenreEntity();
        genre.setName(dto.name());
        genre.setBooks(new ArrayList<>());
        GenreEntity savedGenre = genreRepository.save(genre);
        return savedGenre;
    }

    public GenreEntity getGenreById(Long id) {
        GenreEntity genre = genreRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Genre not found"));
        return genre;
    }

    public List<GenreEntity> getAllGenres() {
        return genreRepository.findAll();
    }

    @Transactional
    public GenreEntity updateGenre(Long id, GenreDTO dto) {
        GenreEntity genre = genreRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Genre not found"));

        genre.setName(dto.name());

        return genre;
    }

    // @Transactional
    // public void removeGenre(Long id) {
    // if (!genreRepository.existsById(id)) {
    // throw new EntityNotFoundException("Genre not found");
    // }

    // Жанры не имеет смысла удалять. Тем более, если уже загружены книги с ними
    // }
}
