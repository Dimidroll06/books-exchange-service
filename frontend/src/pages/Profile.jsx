import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";

import {
  Box,
  Typography,
  Card,
  CardContent,
  Grid,
  Tabs,
  Tab,
  List,
  ListItem,
  ListItemText,
  CircularProgress,
  Avatar,
} from "@mui/material";
import RequestExchangeForm from "../components/RequestExchangeForm";

import { getBookCopiesByOwnerId } from "../services/bookCopyService";
import { getBookById } from "../services/bookService";
import {
  getExchangesBySenderId,
  getExchangesByGetterId,
} from "../services/exchangeService";
import { getReviewsByUserId } from "../services/reviewService";
import { getUserById } from "../services/authService";

export default function Profile() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [user, setUser] = useState(null);
  const [activeTab, setActiveTab] = useState(0);
  const [books, setBooks] = useState([]);
  const [sentExchanges, setSentExchanges] = useState([]);
  const [receivedExchanges, setReceivedExchanges] = useState([]);
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);

        const copiesRes = await getBookCopiesByOwnerId(id);
        const bookDetails = await Promise.all(
          copiesRes.content.map((copy) =>
            getBookById(copy.bookId).then((data) => ({
              ...copy,
              bookTitle: data.title,
            }))
          )
        );
        setBooks(bookDetails);

        const sentRes = await getExchangesBySenderId(id);
        const receivedRes = await getExchangesByGetterId(id);
        setSentExchanges(sentRes.content || []);
        setReceivedExchanges(receivedRes.content || []);

        const reviewsRes = await getReviewsByUserId(id);
        setReviews(reviewsRes.content || []);

        setUser(await getUserById(id));
      } catch (error) {
        console.error("Ошибка при загрузке профиля:", error);
        alert("Не удалось загрузить данные профиля");
        navigate("/404");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [id, navigate]);

  const handleChangeTab = (_, newValue) => {
    setActiveTab(newValue);
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
      <Card>
        <CardContent>
          <Box sx={{ display: "flex", alignItems: "center", mb: 2 }}>
            <Avatar sx={{ width: 64, height: 64, mr: 2 }}>
              {user.username.charAt(0)}
            </Avatar>
            <div>
              <Typography variant="h5">{user.username}</Typography>
              <Typography color="text.secondary">
                ID: {user.id} •{" "}
                {user.isAdmin ? "Администратор" : "Пользователь"}
              </Typography>
            </div>
          </Box>
        </CardContent>
      </Card>

      <Tabs
        value={activeTab}
        onChange={handleChangeTab}
        indicatorColor="primary"
        textColor="primary"
        centered
        sx={{ mt: 3 }}
      >
        <Tab label="Книги" />
        <Tab label="Обмены" />
        <Tab label="Отзывы" />
      </Tabs>

      {/* КНИГИ */}
      {activeTab === 0 && (
        <>
          <Typography variant="h6" gutterBottom>
            Мои книги
          </Typography>

          <Grid container spacing={2} sx={{ mt: 1 }}>
            {books.length > 0 ? (
              books.map((book) => (
                <Grid item xs={12} sm={6} md={4} key={book.id}>
                  <Card>
                    <CardContent>
                      <Typography variant="h6">{book.bookTitle}</Typography>
                      <Typography variant="body2" color="text.secondary">
                        ID экземпляра: {book.id}
                      </Typography>

                      {/* Форма предложения обмена */}
                      <RequestExchangeForm bookCopyId={book.id} />
                    </CardContent>
                  </Card>
                </Grid>
              ))
            ) : (
              <Typography sx={{ m: 2 }}>Нет доступных книг</Typography>
            )}
          </Grid>
        </>
      )}

      {/* ОБМЕНЫ */}
      {activeTab === 1 && (
        <>
          <Typography variant="h6" sx={{ mt: 2 }}>
            Исходящие обмены
          </Typography>
          <List>
            {sentExchanges.length > 0 ? (
              sentExchanges.map((ex) => (
                <ListItem key={ex.id} divider>
                  <ListItemText
                    primary={`К книге ${ex.bookCopy?.bookId}`}
                    secondary={`Статус: ${ex.status}, Место: ${ex.location}`}
                  />
                </ListItem>
              ))
            ) : (
              <Typography sx={{ ml: 2 }}>Нет исходящих обменов</Typography>
            )}
          </List>

          <Typography variant="h6" sx={{ mt: 2 }}>
            Входящие обмены
          </Typography>
          <List>
            {receivedExchanges.length > 0 ? (
              receivedExchanges.map((ex) => (
                <ListItem key={ex.id} divider>
                  <ListItemText
                    primary={`От пользователя ${ex.from?.username}`}
                    secondary={`Статус: ${ex.status}, Место: ${ex.location}`}
                  />
                </ListItem>
              ))
            ) : (
              <Typography sx={{ ml: 2 }}>Нет входящих обменов</Typography>
            )}
          </List>
        </>
      )}

      {/* ОТЗЫВЫ */}
      {activeTab === 2 && (
        <List>
          {reviews.length > 0 ? (
            reviews.map((review) => (
              <ListItem key={review.id} divider>
                <ListItemText
                  primary={`К книге "${review.book?.title}"`}
                  secondary={
                    <>
                      <Typography component="span" variant="body2">
                        Рейтинг: {review.rating}/10
                      </Typography>
                      <br />
                      {review.comment && (
                        <Typography component="span" variant="body2">
                          "{review.comment}"
                        </Typography>
                      )}
                    </>
                  }
                />
              </ListItem>
            ))
          ) : (
            <Typography sx={{ m: 2 }}>Нет отзывов</Typography>
          )}
        </List>
      )}
    </Box>
  );
}
