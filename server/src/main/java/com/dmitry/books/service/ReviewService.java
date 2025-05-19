package com.dmitry.books.service;

import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dmitry.books.dto.BookRatingDTO;
import com.dmitry.books.dto.BookResponseDTO;
import com.dmitry.books.dto.PageDTO;
import com.dmitry.books.dto.ReviewRequestDTO;
import com.dmitry.books.dto.ReviewResponseDTO;
import com.dmitry.books.dto.UserDTO;
import com.dmitry.books.model.BookEntity;
import com.dmitry.books.model.ReviewEntity;
import com.dmitry.books.repository.BookRepository;
import com.dmitry.books.repository.ReviewRepository;
import com.dmitry.books.repository.UserRepository;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    public PageDTO<ReviewResponseDTO> getReviewsByFilter(Long userId, Long bookId, Pageable pageable) {
        Page<ReviewEntity> page = reviewRepository.findByFilters(bookId, userId, pageable);

        PageDTO<ReviewResponseDTO> result = new PageDTO<>();
        result.setContent(page.map(this::tDto).getContent());
        result.setPageNumber(page.getNumber());
        result.setPageSize(page.getSize());
        result.setTotalElements(page.getTotalElements());
        result.setTotalPages(page.getTotalPages());
        result.setLast(page.isLast());

        return result;
    }

    public Optional<ReviewResponseDTO> getReviewById(Long id) {
        return reviewRepository.findById(id)
            .map(this::tDto);
    }

    public Optional<BookRatingDTO> getBookRatingById(Long id) {
        return reviewRepository.findBookRatingInfo(id);
    }

    public void createReview(Long userId, ReviewRequestDTO review) {
        if (reviewRepository.findByFilters(review.getBookId(), userId, null).getTotalElements() != 0) {
            throw new EntityExistsException("You already posted review on this book");
        }
        ReviewEntity reviewEntity = toEntity(userId, review);
        reviewRepository.save(reviewEntity);
    }

    public void updateReview(Long id, Long userId, ReviewRequestDTO updatedReview) {
        ReviewEntity review = reviewRepository.findById(id)
                .orElseThrow(
                    () -> new EntityNotFoundException("Review not found")
                );
        
        if (!Objects.equals(review.getUserId(), userId)) {
            throw new SecurityException("You can't change this review!");
        }

        review.setBookId(updatedReview.getBookId());
        review.setRating(updatedReview.getRating());
        review.setComment(updatedReview.getComment());

        reviewRepository.save(review);
    }

    public void deleteReview(Long id, Long userId) {
        ReviewEntity review = reviewRepository.findById(id)
                .orElseThrow(
                    () -> new EntityNotFoundException("Review not found")
                );
        
        if (!Objects.equals(review.getUserId(), userId)) {
            throw new SecurityException("You can't delete this review!");
        }

        reviewRepository.deleteById(id);
    }

    // ДТОшечки))

    private ReviewResponseDTO tDto(ReviewEntity review) {
        ReviewResponseDTO dto = new ReviewResponseDTO();

        BookEntity book = bookRepository.findById(review.getBookId())
                .orElse(null);
        BookResponseDTO bookDto = new BookResponseDTO();
        if (book != null) {
            bookDto.setId(book.getId());
            bookDto.setTitle(book.getTitle());
            bookDto.setAuthor(book.getAuthor());
            bookDto.setDescription(book.getDescription());
        }

        dto.setBook(bookDto);

        UserDTO userDto = userRepository.getUserById(review.getUserId());
        dto.setUser(userDto);

        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());

        return dto;
    }

    private ReviewEntity toEntity(Long userId, ReviewRequestDTO dto) {
        ReviewEntity entity = new ReviewEntity();
        entity.setBookId(dto.getBookId());
        entity.setUserId(userId);
        entity.setRating(dto.getRating());
        entity.setComment(dto.getComment());

        return entity;
    }

}
