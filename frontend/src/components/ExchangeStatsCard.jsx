import { Card, CardContent, Typography, List, ListItem } from "@mui/material";
import { ExchangeStates } from "../enums/ExchangeStates";

export default function ExchangeStatsCard({ stats }) {
  return (
    <Card>
      <CardContent>
        <Typography variant="h6" gutterBottom>
          Статистика обменов
        </Typography>
        <List>
          {stats.map((item) => (
            <ListItem key={item.status}>
              <strong>{ExchangeStates[item.status] || "Неизвестный статус"}:</strong> {item.count}
            </ListItem>
          ))}
        </List>
      </CardContent>
    </Card>
  );
}