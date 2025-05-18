import axios from 'axios';
import { setAuthToken } from './authService';

const api = axios.create({
    baseURL: 'http://localhost:8081',
});
setAuthToken(localStorage.getItem('token'));

export const createExchange = async (bookCopyId, location) => {
    const res = await api.post('/exchange', { bookCopyId, location });
    return res.data;
};

export const sendExchange = async (id) => {
    const res = await api.put(`/exchange/send/${id}`);
    return res.data;
};

export const acceptExchange = async (id) => {
    const res = await api.put(`/exchange/accept/${id}`);
    return res.data;
};

export const rejectExchange = async (id) => {
    const res = await api.put(`/exchange/reject/${id}`);
    return res.data;
};

export const getExchangeById = async (id) => {
    /*
    Возвращает ExchangeResponseDTO:
    {
      id: 1,
      bookCopy: { id: 1, bookId: 1, ownerId: 1 },
      from: { id: 1, username: "user1", isAdmin: false },
      to: { id: 2, username: "user2", isAdmin: true },
      location: "Москва",
      status: "PENDING"
    }
    */
    const res = await api.get(`/exchange/${id}`);
    return res.data;
};

export const getMyExchanges = async (page = 0, size = 10) => {
    /*
    Возвращает:
    {
      content: [ExchangeResponseDTO],
      pageNumber: 0,
      pageSize: 10,
      totalElements: 50,
      totalPages: 5,
      isLast: false
    }
    */
    const res = await api.get('/exchange/my', { params: { page, size } });
    return res.data;
};

export const getExchangesByUser = async (userId, page = 0, size = 10) => {
    /*
    Возвращает:
    {
      content: [ExchangeResponseDTO],
      pageNumber: 0,
      pageSize: 10,
      totalElements: 50,
      totalPages: 5,
      isLast: false
    }
    */
    const res = await api.get(`/exchange/by-user/${userId}`, { params: { page, size } });
    return res.data;
};

export const getExchangesBySenderId = async (senderId, page = 0, size = 10) => {
    /*
    Возвращает:
    {
      content: [ExchangeResponseDTO],
      pageNumber: 0,
      pageSize: 10,
      totalElements: 50,
      totalPages: 5,
      isLast: false
    }
    */
    const res = await api.get(`/exchange/by-sender/${senderId}`, { params: { page, size } });
    return res.data;
};

export const getExchangesByGetterId = async (getterId, page = 0, size = 10) => {
    /*
    Возвращает:
    {
      content: [ExchangeResponseDTO],
      pageNumber: 0,
      pageSize: 10,
      totalElements: 50,
      totalPages: 5,
      isLast: false
    }
    */
    const res = await api.get(`/exchange/by-getter/${getterId}`, { params: { page, size } });
    return res.data;
};