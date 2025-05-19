import { useState } from "react";
import { register } from "../services/authService";
import { Box, Typography, TextField, Button, Alert } from "@mui/material";
import { useNavigate } from "react-router-dom";

export default function Register() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (password !== confirmPassword) {
      setError("Пароли не совпадают");
      return;
    }

    try {
      await register(username, password);
      navigate("/login");
    } catch (err) {
      console.error(err);
      setError("Ошибка регистрации");
    }
  };

  return (
    <Box sx={{ maxWidth: 400, mx: "auto", mt: 5 }}>
      <Typography variant="h5" gutterBottom>
        Регистрация
      </Typography>
      {error && <Alert severity="error">{error}</Alert>}
      <form onSubmit={handleSubmit}>
        <TextField
          label="Логин"
          fullWidth
          margin="normal"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
        />
        <TextField
          label="Пароль"
          type="password"
          fullWidth
          margin="normal"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <TextField
          label="Подтвердите пароль"
          type="password"
          fullWidth
          margin="normal"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
          required
        />
        <Button type="submit" variant="contained" fullWidth sx={{ mt: 2 }}>
          Зарегистрироваться
        </Button>
      </form>
      <Box sx={{ mt: 2 }}>
        <Typography variant="body2">
          Уже есть аккаунт?{" "}
          <a href="/login" style={{ textDecoration: "underline" }}>
            Войдите
          </a>
        </Typography>
      </Box>
    </Box>
  );
}
