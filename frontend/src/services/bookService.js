import axios from 'axios';
import { setAuthToken } from './authService';

const api = axios.create({
  baseURL: import.meta.env.VITE_SERVER || "/books",
});

setAuthToken(localStorage.getItem('token'), api);

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
  let params = {};
  if (title) params.title = title;
  if (author) params.author = author;
  if (genreId) params.genreId = genreId;
  if (page) params.page = page;
  if (size) params.size = size;
  const res = await api.get('/book', { params });
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
  try {
    const res = await api.get(`/book/rating/${id}`);
    return res.data;
  } catch (error) {
    console.error(error);
    return {
      bookId: id,
      reviewCount: 0,
      averageRating: 10
    }
  }
};

export const createBook = async (bookData) => {
  const res = await api.post('/book', bookData);
  return res.data;
};


export const getAllGenres = async () => {
  /*
  Возвращает:
  [
  {
    id: 1,
    name: "фантастика"
  }
  ]  
  */
  const res = await api.get('/book/genre');
  return res.data
}

export const updateBook = async (id, bookData) => {
  const res = await api.put(`/book/${id}`, bookData);
  return res.data;
};

export const deleteBook = async (id) => {
  const res = await api.delete(`/book/${id}`);
  return res.data;
};