import axios from 'axios';
import { setAuthToken } from './authService';

const api = axios.create({
    baseURL: 'http://localhost:8081',
});
setAuthToken(localStorage.getItem('token'));

export const addGenre = async (name) => {
    const res = await api.post('/admin/genres', { name });
    return res.data;
};

export const getAllBooks = async () => {
    /*
    Возвращает массив:
    [
      {
        id: 1,
        title: "Шестёрка воронов",
        author: "Ли Бардуго",
        description: "Шесть героев, одно опасное дело",
        genreId: 1
      }, ...
    ]
    */
    const res = await api.get('/admin/books');
    return res.data;
};

export const getExchangeStats = async () => {
    /*
    Возвращает массив:
    [
      {
        status: 0,
        count: 10
      }, ...
    ]
    */
    const res = await api.get('/admin/exchange-stats');
    return res.data;
};