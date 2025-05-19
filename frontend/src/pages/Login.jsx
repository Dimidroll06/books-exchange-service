import { useState } from "react";
import { login } from "../services/authService";
import { Box, Typography, TextField, Button, Alert } from "@mui/material";
import { useNavigate } from "react-router-dom";

export default function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await login(username, password);
      navigate("/");
      window.location.reload();
    } catch (err) {
      console.error(err);
      setError("Ошибка входа. Проверьте логин и пароль.");
    }
  };

  return (
    <Box sx={{ maxWidth: 400, mx: "auto", mt: 5 }}>
      <Typography variant="h5" gutterBottom>
        Вход
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
        <Button type="submit" variant="contained" fullWidth sx={{ mt: 2 }}>
          Войти
        </Button>
      </form>
      <Box sx={{ mt: 2 }}>
        <Typography variant="body2">
          Нет аккаунта?{" "}
          <a href="/register" style={{ textDecoration: "underline" }}>
            Зарегистрируйтесь
          </a>
        </Typography>
      </Box>
    </Box>
  );
}
