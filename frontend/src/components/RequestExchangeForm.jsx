import React from "react";
import { Box, TextField, Button, Snackbar, Alert } from "@mui/material";
import { useState } from "react";
import { createNewExchange } from "../services/exchangeService";

export default function RequestExchangeForm({ bookCopyId }) {
  const [openForm, setOpenForm] = useState(false);
  const [location, setLocation] = useState("");
  const [loading, setLoading] = useState(false);
  const [snackbar, setSnackbar] = useState({
    open: false,
    message: "",
    severity: "success",
  });

  const handleSubmit = async () => {
    if (!location.trim()) {
      setSnackbar({
        open: true,
        message: "Введите адрес встречи",
        severity: "warning",
      });
      return;
    }

    setLoading(true);

    try {
      await createNewExchange({
        bookCopyId,
        location,
      });

      setSnackbar({
        open: true,
        message: "Обмен успешно отправлен!",
        severity: "success",
      });

      setLocation("");
      setOpenForm(false);
    } catch (e) {
      console.error(e);
      setSnackbar({
        open: true,
        message: "Ошибка отправки обмена",
        severity: "error",
      });
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    setLocation("");
    setOpenForm(false);
  };

  return (
    <Box sx={{ mt: 1 }}>
      {!openForm ? (
        <Button
          size="small"
          variant="outlined"
          onClick={() => setOpenForm(true)}
        >
          Попросить
        </Button>
      ) : (
        <Box component="form" noValidate autoComplete="off">
          <TextField
            label="Адрес встречи"
            value={location}
            onChange={(e) => setLocation(e.target.value)}
            size="small"
            fullWidth
            margin="dense"
          />
          <Box sx={{ display: "flex", gap: 1, mt: 1 }}>
            <Button
              size="small"
              variant="contained"
              disabled={loading}
              onClick={handleSubmit}
            >
              Отправить
            </Button>
            <Button
              size="small"
              variant="outlined"
              color="error"
              onClick={handleCancel}
            >
              Отмена
            </Button>
          </Box>
        </Box>
      )}

      {/* Уведомления */}
      <Snackbar
        open={snackbar.open}
        autoHideDuration={3000}
        onClose={() => setSnackbar((prev) => ({ ...prev, open: false }))}
        anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
      >
        <Alert
          onClose={() => setSnackbar((prev) => ({ ...prev, open: false }))}
          severity={snackbar.severity}
          sx={{ width: "100%" }}
        >
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Box>
  );
}
