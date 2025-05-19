// src/pages/BookPage.jsx

import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";

import {
  Box,
  Typography,
  Card,
  CardContent,
  Button,
  List,
  ListItem,
  ListItemText,
  Dialog,
  DialogTitle,
  DialogContent,
  TextField,
  DialogActions,
  CircularProgress,
  Container,
  Rating,
  Divider,
} from "@mui/material";

import { getBookById, getBookRating } from "../services/bookService";
import {
  createBookCopy,
  getBookCopyById,
  getBookCopiesByBookId,
} from "../services/bookCopyService";
import { createExchange } from "../services/exchangeService";
import { createReview, getReviewsByBookId } from "../services/reviewService";
import { getUser, getUserById } from "../services/authService";

export default function BookPage() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [book, setBook] = useState(null);
  const [ratingData, setRatingData] = useState(null);
  const [copies, setCopies] = useState([]);
  const [reviews, setReviews] = useState([]);

  const [loading, setLoading] = useState(true);

  const [exchangeDialogOpen, setExchangeDialogOpen] = useState(false);
  const [selectedCopyId, setSelectedCopyId] = useState(null);
  const [location, setLocation] = useState("");

  const [rating, setRating] = useState(5);
  const [comment, setComment] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const bookRes = await getBookById(id);
        const ratingRes = await getBookRating(id);
        const copiesRes = await getBookCopiesByBookId(id);
        const reviewsRes = await getReviewsByBookId(id);

        const enrichedCopies = await Promise.all(
          copiesRes.content.map(async (copy) => {
            const owner = await getUserById(copy.ownerId);
            return {
              ...copy,
              ownerName: owner.username,
            };
          })
        );

        setBook(bookRes);
        setRatingData(ratingRes);
        setCopies(enrichedCopies);
        setReviews(reviewsRes.content || []);
      } catch (error) {
        console.error("Ошибка загрузки данных:", error);
        alert("Не удалось загрузить данные о книге");
        navigate("/404");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [id, navigate]);

  const handleAddMyCopy = async () => {
    try {
      const res = await createBookCopy(id);
      const newCopy = await getBookCopyById(res.id);
      const owner = await getUserById(newCopy.ownerId);
      setCopies([
        {
          ...newCopy,
          ownerName: owner.username,
        },
        ...copies,
      ]);
    } catch (error) {
      console.error("Ошибка при добавлении экземпляра:", error);
      alert("Не удалось добавить экземпляр книги");
    }
  };

  const handleStartExchange = async (copyId) => {
    setSelectedCopyId(copyId);
    setExchangeDialogOpen(true);
  };

  const handleSubmitExchange = async () => {
    if (!location.trim()) {
      alert("Введите место обмена");
      return;
    }

    try {
      await createExchange(selectedCopyId, location);
      alert("Обмен успешно создан!");
    } catch (error) {
      console.error("Ошибка при создании обмена:", error);
      alert("Не удалось создать обмен");
    } finally {
      setExchangeDialogOpen(false);
      setLocation("");
    }
  };

  const handleSubmitReview = async () => {
    if (rating < 1 || rating > 10) {
      alert("Рейтинг должен быть от 1 до 10");
      return;
    }

    setIsSubmitting(true);
    try {
      await createReview(id, rating, comment);
      const user = getUser();
      setReviews([
        {
          comment,
          user,
          book: { id: book.id, title: book.title, author: book.author },
        },
        ...reviews,
      ]);
      setRating(5);
      setComment("");
    } catch (error) {
      console.error("Ошибка при отправке отзыва:", error);
      alert("Не удалось отправить отзыв");
    } finally {
      setIsSubmitting(false);
    }
  };

  if (loading) {
    return (
      <Box sx={{ display: "flex", justifyContent: "center", mt: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Container maxWidth="md" sx={{ mt: 4 }}>
      <Card>
        <CardContent>
          <Typography variant="h5" gutterBottom>
            {book.title}
          </Typography>
          <Typography variant="subtitle1" color="text.secondary">
            Автор: {book.author}
          </Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
            Жанр: {book.genre}
          </Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
            Рейтинг: {ratingData?.averageRating ?? "—"} (
            {ratingData?.reviewCount ?? 0} отзывов)
          </Typography>
          <Typography variant="body1" sx={{ mt: 2 }}>
            {book.description}
          </Typography>
          <Button
            variant="contained"
            color="primary"
            onClick={handleAddMyCopy}
            sx={{ mt: 2 }}
          >
            У меня есть эта книга
          </Button>
        </CardContent>
      </Card>

      <Box sx={{ mt: 4 }}>
        <Typography variant="h6">Оставить отзыв</Typography>
        <Box sx={{ mt: 2, display: "flex", flexDirection: "column", gap: 2 }}>
          <Rating
            name="rating"
            value={rating}
            onChange={(e, newValue) => setRating(newValue)}
            precision={1}
            max={10}
          />
          <TextField
            label="Комментарий"
            multiline
            rows={3}
            value={comment}
            onChange={(e) => setComment(e.target.value)}
          />
          <Button
            variant="contained"
            color="success"
            disabled={isSubmitting}
            onClick={handleSubmitReview}
            sx={{ alignSelf: "start" }}
          >
            {isSubmitting ? "Отправка..." : "Отправить отзыв"}
          </Button>
        </Box>
      </Box>

      <Box sx={{ mt: 4 }}>
        <Typography variant="h6">Отзывы пользователей</Typography>
        {reviews.length > 0 ? (
          <List>
            {reviews.map((review) => (
              <ListItem key={review.id} divider>
                <ListItemText
                  primary={`Пользователь: ${review.user?.username || "Аноним"}`}
                  secondary={
                    <>
                      <Typography component="span" variant="body2">
                        Рейтинг: {review.rating}/10
                      </Typography>
                      <br />
                      {review.comment && (
                        <Typography component="span" variant="body2">
                          "{review.comment}"
                        </Typography>
                      )}
                    </>
                  }
                />
              </ListItem>
            ))}
          </List>
        ) : (
          <Typography>Нет отзывов</Typography>
        )}
      </Box>

      <Typography variant="h6" sx={{ mt: 4 }}>
        Экземпляры этой книги
      </Typography>
      {copies.length > 0 ? (
        <List>
          {copies.map((copy) => (
            <ListItem key={copy.id} divider>
              <ListItemText
                primary={`Экземпляр ID: ${copy.id}`}
                secondary={`Владелец: ${copy.ownerName}`}
              />
              <Button
                variant="outlined"
                size="small"
                onClick={() => handleStartExchange(copy.id)}
              >
                Начать обмен
              </Button>
            </ListItem>
          ))}
        </List>
      ) : (
        <Typography sx={{ mt: 2 }}>Нет доступных экземпляров</Typography>
      )}

      <Dialog
        open={exchangeDialogOpen}
        onClose={() => setExchangeDialogOpen(false)}
      >
        <DialogTitle>Создать обмен</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            margin="dense"
            label="Место обмена"
            fullWidth
            variant="standard"
            value={location}
            onChange={(e) => setLocation(e.target.value)}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setExchangeDialogOpen(false)}>Отмена</Button>
          <Button onClick={handleSubmitExchange}>Создать</Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
}
