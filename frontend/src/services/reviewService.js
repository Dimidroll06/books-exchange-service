import axios from 'axios';
import { setAuthToken } from './authService';

const api = axios.create({
    baseURL: '/server',
});
setAuthToken(localStorage.getItem('token'));

export const createReview = async (bookId, rating, comment) => {
    const res = await api.post('/rewiew', { bookId, rating, comment });
    return res.data;
};

export const getReviewsByBookId = async (bookId, page = 0, size = 10) => {
    /*
    Возвращает:
    {
      content: [ReviewResponseDTO],
      pageNumber: 0,
      pageSize: 10,
      totalElements: 50,
      totalPages: 5,
      isLast: false
    }
    */
    const res = await api.get(`/rewiew/by-book/${bookId}`, { params: { page, size } });
    return res.data;
};

export const getReviewsByUserId = async (userId, page = 0, size = 10) => {
    const res = await api.get(`/rewiew/by-user/${userId}`, { params: { page, size } });
    return res.data;
};

export const getReviewById = async (id) => {
    /*
    Возвращает ReviewResponseDTO:
    {
      id: 1,
      user: { id: 1, username: "user1", isAdmin: false },
      book: { id: 1, title: "Шестёрка воронов", author: "Ли Бардуго", ... },
      rating: 10,
      comment?: "Восхитительная книга"
    }
    */
    const res = await api.get(`/rewiew/${id}`);
    return res.data;
};

export const updateReview = async (id, reviewData) => {
    const res = await api.put(`/rewiew/${id}`, reviewData);
    return res.data;
};

export const deleteReview = async (id) => {
    const res = await api.delete(`/rewiew/${id}`);
    return res.data;
};