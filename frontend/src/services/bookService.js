import axios from 'axios';
import { setAuthToken } from './authService';

const api = axios.create({
    baseURL: 'http://localhost:8081',
});

setAuthToken(localStorage.getItem('token'));

export const getBooks = async (title, author, genreId, page = 0, size = 10) => {
    /*
    Возвращает:
    {
      content: [BookResponseDTO],
      pageNumber: 0,
      pageSize: 10,
      totalElements: 50,
      totalPages: 5,
      isLast: false
    }
    */
    const res = await api.get('/book', { params: { title, author, genreId, page, size } });
    return res.data;
};

export const getBookById = async (id) => {
    /*
    Возвращает BookResponseDTO:
    {
      id: 1,
      title: "Шестёрка воронов",
      author: "Ли Бардуго",
      description: "Шесть героев, одно опасное дело",
      genre: "Фантастика"
    }
    */
    const res = await api.get(`/book/${id}`);
    return res.data;
};

export const getBookRating = async (id) => {
    /*
    Возвращает:
    {
      bookId: 1,
      reviewCount: 15,
      averageRating: 9.2
    }
    */
    const res = await api.get(`/book/rating/${id}`);
    return res.data;
};

export const createBook = async (bookData) => {
  const res = await api.post('/book', bookData);
  return res.data;
};

export const updateBook = async (id, bookData) => {
  const res = await api.put(`/book/${id}`, bookData);
  return res.data;
};

export const deleteBook = async (id) => {
  const res = await api.delete(`/book/${id}`);
  return res.data;
};