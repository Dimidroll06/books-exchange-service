package ru.dmitry.books.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ru.dmitry.books.dto.ReviewDTO;
import ru.dmitry.books.dto.ReviewResponseDTO;
import ru.dmitry.books.model.BookEntity;
import ru.dmitry.books.model.ReviewEntity;
import ru.dmitry.books.repository.BookRepository;
import ru.dmitry.books.repository.ReviewRepository;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;

    public ReviewResponseDTO createReview(ReviewDTO dto) {
        BookEntity book = bookRepository.findById(dto.bookId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        ReviewEntity review = new ReviewEntity();
        review.setBook(book);
        review.setUserId(dto.userId());
        review.setRating(dto.rating());
        review.setComment(dto.comment());

        ReviewEntity savedReview = reviewRepository.save(review);
        return toDto(savedReview);
    }

    public Page<ReviewResponseDTO> getReviewsByBookId(Long bookId, Pageable pageable) {
        Page<ReviewEntity> reviews = reviewRepository.findByBookId(bookId, pageable);
        return reviews.map(this::toDto);
    }

    public Page<ReviewResponseDTO> getReviewsByUserId(Long userId, Pageable pageable) {
        Page<ReviewEntity> reviews = reviewRepository.findByUserId(userId, pageable);
        return reviews.map(this::toDto);
    }

    @Transactional
    public ReviewResponseDTO updateReview(Long id, ReviewDTO dto) {
        ReviewEntity review = reviewRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Review not found"));

        BookEntity book = bookRepository.findById(dto.bookId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        review.setBook(book);
        review.setUserId(dto.userId());
        review.setRating(dto.rating());
        review.setComment(dto.comment());

        return toDto(review);
    }

    @Transactional
    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new EntityNotFoundException("Review not found");
        }
        reviewRepository.deleteById(id);
    }

    private ReviewResponseDTO toDto(ReviewEntity review) {
        return new ReviewResponseDTO(
                review.getId(),
                review.getBook().getId(),
                review.getBook().getTitle(),
                review.getUserId(),
                review.getRating(),
                review.getComment());
    }
}