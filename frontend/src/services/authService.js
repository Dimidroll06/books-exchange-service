import axios from 'axios';

function parseJwt(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(
            atob(base64)
                .split('')
                .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
                .join('')
        );
        return JSON.parse(jsonPayload);
    } catch (e) {
        console.error(e);
        return null;
    }
}

const api = axios.create({
    baseURL: 'http://localhost:8080',
});

export const login = async (username, password) => {
    /*
    Возвращает:
    {
      token: "string"
    }
    */
    const response = await api.post('/login', { username, password });
    localStorage.setItem('token', response.data.token);

    const userData = parseJwt(response.data.token);
    if (userData) {
        localStorage.setItem('user', JSON.stringify({
            username: userData.username,
            user_id: userData.user_id,
            is_admin: userData.is_admin
        }));
    }

    return response.data;
};

export const register = async (username, password) => {
    /*
    Возвращает:
    {
      token: "string"
    }
    */
    const response = await api.post('/register', { username, password });
    return response.data;
};

export const setAuthToken = (token) => {
    if (token) {
        api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    } else {
        delete api.defaults.headers.common['Authorization'];
    }
};

export const getUser = () => {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
};

export const getUserById = async (id) => {
    const response = await api.get('/user/id?id='+id);
    return response.data
}

const token = localStorage.getItem('token');
if (token) {
    setAuthToken(token);
}