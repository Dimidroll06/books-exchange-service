import { useState } from "react";
import { Button, TextField, Box, Typography } from "@mui/material";
import { addGenre } from "../services/adminService";

export default function AddGenreForm() {
  const [genreName, setGenreName] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!genreName.trim()) return;

    try {
      await addGenre(genreName);
      alert("Жанр успешно добавлен!");
      setGenreName("");
    } catch (error) {
      alert("Ошибка при добавлении жанра");
      console.error(error);
    }
  };

  return (
    <Box component="form" onSubmit={handleSubmit} noValidate>
      <Typography variant="h6" gutterBottom>
        Добавить жанр
      </Typography>
      <TextField
        label="Название жанра"
        value={genreName}
        onChange={(e) => setGenreName(e.target.value)}
        fullWidth
        required
        margin="normal"
      />
      <Button type="submit" variant="contained" color="primary">
        Добавить
      </Button>
    </Box>
  );
}
