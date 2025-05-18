import { useEffect, useState } from "react";
import { getBooks } from "../services/bookService";
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
} from "@mui/material";
import { useSearchParams } from "react-router-dom";

const genres = [{ id: "", name: "Все жанры" }];

export default function Home() {
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(false);
  const [totalPages, setTotalPages] = useState(1);

  const [searchParams, setSearchParams] = useSearchParams();
  const [title, setTitle] = useState(searchParams.get("q") || "");
  const [author, setAuthor] = useState("");
  const [genreId, setGenreId] = useState("");
  const [page, setPage] = useState(Number(searchParams.get("page")) || 1);

  useEffect(() => {
    setLoading(true);
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
