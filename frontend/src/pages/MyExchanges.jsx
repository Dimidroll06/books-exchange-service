import { useEffect, useState } from "react";
import {
  Box,
  Typography,
  Card,
  CardContent,
  List,
  ListItem,
  ListItemText,
  Button,
  Divider,
  CircularProgress,
  Pagination,
} from "@mui/material";

import {
  getMyExchanges,
  sendExchange,
  acceptExchange,
  rejectExchange,
} from "../services/exchangeService";
import { getUser } from "../services/authService";

export default function MyExchanges() {
  const [exchanges, setExchanges] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);

  useEffect(() => {
    const fetchExchanges = async () => {
      setLoading(true);
      try {
        const data = await getMyExchanges(page - 1, 10);
        setExchanges(data.content || []);
        setTotalPages(data.totalPages || 1);
      } catch (error) {
        console.error("Ошибка загрузки обменов:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchExchanges();
  }, [page]);

  const handlePageChange = (_, value) => {
    setPage(value);
  };

  const handleSend = async (id) => {
    try {
      await sendExchange(id);
      setExchanges((prev) =>
        prev.map((ex) => (ex.id === id ? { ...ex, status: "sended" } : ex))
      );
    } catch (error) {
      alert("Не удалось отправить обмен");
      console.error(error);
    }
  };

  const handleAccept = async (id) => {
    try {
      await acceptExchange(id);
      setExchanges((prev) =>
        prev.map((ex) => (ex.id === id ? { ...ex, status: "accepted" } : ex))
      );
    } catch (error) {
      console.error(error);
      alert("Не удалось принять обмен");
    }
  };

  const handleReject = async (id) => {
    if (!window.confirm("Вы уверены, что хотите отклонить этот обмен?")) return;
    try {
      await rejectExchange(id);
      setExchanges((prev) =>
        prev.map((ex) => (ex.id === id ? { ...ex, status: "rejected" } : ex))
      );
    } catch (error) {
      console.error(error);
      alert("Не удалось отклонить обмен");
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
    <Box sx={{ p: 2 }}>
      <Typography variant="h5" gutterBottom>
        Мои обмены
      </Typography>

      {exchanges.length === 0 ? (
        <Typography>У вас пока нет обменов.</Typography>
      ) : (
        <>
          <List>
            {exchanges.map((exchange) => {
              const isCurrentUserFrom = getUser() ?
                exchange.from?.id ===
                getUser().user_id : false;
              const isCurrentUserTo = getUser() ?
                exchange.to?.id ===
                getUser().user_id : false;

              const renderActionButton = () => {
                if (exchange.status === "rejected") return null;

                if (exchange.status === "created" && isCurrentUserFrom) {
                  return (
                    <Button
                      size="small"
                      variant="contained"
                      onClick={() => handleSend(exchange.id)}
                    >
                      Отдал
                    </Button>
                  );
                }

                if (exchange.status === "sended" && isCurrentUserTo) {
                  return (
                    <>
                      <Button
                        size="small"
                        variant="contained"
                        onClick={() => handleAccept(exchange.id)}
                      >
                        Принять
                      </Button>
                      <Button
                        size="small"
                        variant="outlined"
                        color="error"
                        onClick={() => handleReject(exchange.id)}
                        sx={{ ml: 1 }}
                      >
                        Отклонить
                      </Button>
                    </>
                  );
                }

                return null;
              };

              return (
                <Card key={exchange.id} sx={{ mb: 2 }}>
                  <CardContent>
                    <ListItem disableGutters>
                      <ListItemText
                        primary={`Книга ID ${exchange.bookCopy?.bookId}`}
                        secondary={
                          <>
                            <strong>Статус:</strong> {exchange.status}
                            <br />
                            <strong>От:</strong>{" "}
                            {exchange.from?.username || "—"}
                            <br />
                            <strong>Получатель:</strong>{" "}
                            {exchange.to?.username || "—"}
                            <br />
                            <strong>Место:</strong>{" "}
                            {exchange.location || "Не указано"}
                          </>
                        }
                      />
                      <Box>{renderActionButton()}</Box>
                    </ListItem>
                  </CardContent>
                </Card>
              );
            })}
          </List>

          {/* Пагинация */}
          <Box sx={{ display: "flex", justifyContent: "center", mt: 3 }}>
            <Pagination
              count={totalPages}
              page={page}
              onChange={handlePageChange}
              color="primary"
            />
          </Box>
        </>
      )}
    </Box>
  );
}
