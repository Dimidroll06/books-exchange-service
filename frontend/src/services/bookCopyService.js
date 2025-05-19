import axios from 'axios';
import { setAuthToken } from './authService';

const api = axios.create({
    baseURL: import.meta.env.VITE_SERVER || "/books",
});
setAuthToken(localStorage.getItem('token'), api);

export const createBookCopy = async (bookId) => {
    const res = await api.post('/copies', { bookId });
    return res.data;
};

export const getBookCopyById = async (id) => {
    /*
    Возвращает BookCopyResponseDTO:
    {
      id: 1,
      bookId: 1,
      ownerId: 1
    }
    */
    const res = await api.get(`/copies/${id}`);
    return res.data;
};

export const deleteBookCopy = async (id) => {
    const res = await api.delete(`/copies/${id}`);
    return res.data;
};

export const getBookCopiesByOwnerId = async (ownerId, page = 0, size = 10) => {
    /*
    Возвращает:
    {
      content: [BookCopyResponseDTO],
      pageNumber: 0,
      pageSize: 10,
      totalElements: 50,
      totalPages: 5,
      isLast: false
    }
    */
    const res = await api.get(`/copies/by-owner/${ownerId}`, { params: { page, size } });
    return res.data;
};