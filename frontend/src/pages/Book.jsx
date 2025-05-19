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
} from "@mui/material";

import { getBookById, getBookRating } from "../services/bookService";
import {
  createBookCopy,
  getBookCopyById,
  getBookCopiesByBookId,
} from "../services/bookCopyService";
import { createExchange } from "../services/exchangeService";
import { getUserById } from "../services/authService";

export default function Book() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [book, setBook] = useState(null);
  const [ratingData, setRatingData] = useState(null);
  const [copies, setCopies] = useState([]);
  const [loading, setLoading] = useState(true);

  const [exchangeDialogOpen, setExchangeDialogOpen] = useState(false);
  const [selectedCopyId, setSelectedCopyId] = useState(null);
  const [location, setLocation] = useState("");

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const bookRes = await getBookById(id);
        const ratingRes = await getBookRating(id);
        const copiesRes = await getBookCopiesByBookId(id);

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
      console.log(id);
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
