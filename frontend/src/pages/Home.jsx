import { useEffect, useState } from "react";
import { getAllGenres, getBooks, createBook } from "../services/bookService";
import {
  Box,
  Typography,
  TextField,
  MenuItem,
  Button,
  Grid,
  Card,
  CardContent,
  Pagination,
  CircularProgress,
  Paper
} from "@mui/material";
import { useSearchParams } from "react-router-dom";

let genres = [{ id: "", name: "Все жанры" }];

export default function Home() {
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(false);
  const [totalPages, setTotalPages] = useState(1);

  const [searchParams, setSearchParams] = useSearchParams();
  const [title, setTitle] = useState(searchParams.get("q") || "");
  const [author, setAuthor] = useState("");
  const [genreId, setGenreId] = useState("");
  const [page, setPage] = useState(Number(searchParams.get("page")) || 1);

  const [showAddForm, setShowAddForm] = useState(false);
  const [newBook, setNewBook] = useState({
    title: "",
    author: "",
    description: "",
    genreId: 0,
  });
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    setLoading(true);
    genres.concat(getAllGenres());
    getBooks(title, author, genreId, page - 1, 10)
      .then((data) => {
        setBooks(data.content || []);
        setTotalPages(data.totalPages || 1);
      })
      .finally(() => setLoading(false));
  }, [title, author, genreId, page]);

  useEffect(() => {
    const params = {};
    if (title) params.q = title;
    if (author) params.author = author;
    if (genreId) params.genre = genreId;
    if (page > 1) params.page = page;
    setSearchParams(params);

    setTitle(searchParams.get("q") || "");
    setPage(Number(searchParams.get("page")) || 1);
    // eslint-disable-next-line
  }, [title, author, genreId, page, searchParams]);

  const handleSearch = (e) => {
    e.preventDefault();
    setPage(1);
  };

  const handleAddBookChange = (e) => {
    const { name, value } = e.target;
    setNewBook((prev) => ({ ...prev, [name]: value }));
  };

  const handleAddBookSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);

    try {
      await createBook(newBook);
      alert("Книга успешно добавлена!");

      setNewBook({ title: "", author: "", description: "", genreId: "" });
      setShowAddForm(false);

      getBooks(title, author, genreId, page - 1, 10).then((data) => {
        setBooks(data.content || []);
      });
    } catch (error) {
      alert("Ошибка при добавлении книги");
      console.error(error);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Box sx={{ mt: 3 }}>
      <Typography variant="h4" gutterBottom>
        Каталог книг
      </Typography>
      <Box
        component="form"
        onSubmit={handleSearch}
        sx={{ mb: 2, display: "flex", gap: 2, flexWrap: "wrap" }}
      >
        <TextField
          label="Название"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          size="small"
        />
        <TextField
          label="Автор"
          value={author}
          onChange={(e) => setAuthor(e.target.value)}
          size="small"
        />
        <TextField
          select
          label="Жанр"
          value={genreId}
          onChange={(e) => setGenreId(e.target.value)}
          size="small"
          sx={{ minWidth: 120 }}
        >
          {genres.map((g) => (
            <MenuItem key={g.id} value={g.id}>
              {g.name}
            </MenuItem>
          ))}
        </TextField>
        <Button type="submit" variant="contained">
          Найти
        </Button>
      </Box>

      <Box sx={{ mb: 2 }}>
        <Button variant="outlined" onClick={() => setShowAddForm(!showAddForm)}>
          {showAddForm ? "Скрыть форму" : "Не нашли книгу?"}
        </Button>
      </Box>

      {showAddForm && (
        <Paper sx={{ p: 2, mb: 3 }}>
          <Typography variant="h6" gutterBottom>
            Добавить новую книгу
          </Typography>
          <Box component="form" onSubmit={handleAddBookSubmit}>
            <TextField
              label="Название"
              name="title"
              value={newBook.title}
              onChange={handleAddBookChange}
              fullWidth
              required
              margin="normal"
            />
            <TextField
              label="Автор"
              name="author"
              value={newBook.author}
              onChange={handleAddBookChange}
              fullWidth
              required
              margin="normal"
            />
            <TextField
              label="Описание"
              name="description"
              multiline
              rows={3}
              value={newBook.description}
              onChange={handleAddBookChange}
              fullWidth
              required
              margin="normal"
            />
            <TextField
              select
              label="Жанр"
              name="genreId"
              value={newBook.genreId}
              onChange={handleAddBookChange}
              fullWidth
              required
              margin="normal"
            >
              {genres
                .filter((g) => g.id !== "")
                .map((g) => (
                  <MenuItem key={g.id} value={g.id}>
                    {g.name}
                  </MenuItem>
                ))}
            </TextField>
            <Box sx={{ mt: 2 }}>
              <Button type="submit" variant="contained" disabled={submitting}>
                {submitting ? "Добавление..." : "Добавить книгу"}
              </Button>
            </Box>
          </Box>
        </Paper>
      )}

      {loading ? (
        <Box sx={{ display: "flex", justifyContent: "center", mt: 4 }}>
          <CircularProgress />
        </Box>
      ) : (
        <>
          <Grid container spacing={2}>
            {books.map((book) => (
              <Grid item xs={12} sm={6} md={4} key={book.id}>
                <Card>
                  <CardContent>
                    <Typography variant="h6">{book.title}</Typography>
                    <Typography variant="body2" color="text.secondary">
                      {book.author}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Жанр: {book.genreName}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Рейтинг: {book.rating ?? "—"} ({book.reviewsCount ?? 0}{" "}
                      отзывов)
                    </Typography>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
          <Box sx={{ display: "flex", justifyContent: "center", mt: 3 }}>
            <Pagination
              count={totalPages}
              page={page}
              onChange={(_, value) => setPage(value)}
              color="primary"
            />
          </Box>
        </>
      )}
    </Box>
  );
}
