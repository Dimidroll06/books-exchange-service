import { useEffect, useState } from "react";
import { Typography, Box } from "@mui/material";
import { getExchangeStats } from "../services/adminService";
import ExchangeStatsCard from "../components/ExchangeStatsCard";
import AddGenreForm from "../components/AddGenreForm";
import { ExchangeStates } from "../enums/ExchangeStates";
import { getUser } from "../services/authService";

const userIsAdmin = getUser() ? getUser().is_admin : false;

export default function AdminPanel() {
  const [stats, setStats] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const data = await getExchangeStats();
        console.log(data)
        setStats(data);
      } catch (error) {
        console.error("Ошибка при загрузке статистики:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchStats();
  }, []);

  if (!userIsAdmin) {
    return <Typography color="error">Доступ запрещён</Typography>;
  }

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" gutterBottom>
        Админ-панель
      </Typography>

      {loading ? (
        <Typography>Загрузка данных...</Typography>
      ) : (
        <ExchangeStatsCard stats={stats} />
      )}

      <Box sx={{ mt: 4 }}>
        <AddGenreForm />
      </Box>
    </Box>
  );
}
