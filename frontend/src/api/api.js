import axios from 'axios';

const API_BASE = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' },
});

// Attach JWT token to every request
api.interceptors.request.use((config) => {
  const stored = localStorage.getItem('auth');
  if (stored) {
    const { token } = JSON.parse(stored);
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
  }
  return config;
});

// Redirect to login on 401
api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      localStorage.removeItem('auth');
      window.location.href = '/login';
    }
    return Promise.reject(err);
  }
);
//Auth
export const authApi={
    login:(data)=>api.post('/auth/login',data),
    register:(data)=>api.post('/auth/register',data)
}
// Admin
export const adminApi = {
  getFloors: () => api.get('/admin/floors'),
  getFloor: (id) => api.get(`/admin/floors/${id}`),
  createFloor: (data) => api.post('/admin/floors', data),
  deleteFloor: (id) => api.delete(`/admin/floors/${id}`),
  getRoomsByFloor: (floorId) => api.get(`/admin/floors/${floorId}/rooms`),
  getRoom: (id) => api.get(`/admin/rooms/${id}`),
  createRoom: (data) => api.post('/admin/rooms', data),
  deleteRoom: (id) => api.delete(`/admin/rooms/${id}`),
  createDesk: (data) => api.post('/admin/desks', data),
  updateDesk: (id, data) => api.patch(`/admin/desks/${id}`, data),
  deleteDesk: (id) => api.delete(`/admin/desks/${id}`),
  updateRoomWalls: (id, data) => api.patch(`/admin/rooms/${id}/walls`, data),
};

// Bookings
export const bookingApi = {
  getFloors: () => api.get('/bookings/floors'),
  getFloor: (id) => api.get(`/bookings/floors/${id}`),
  getRoom: (id) => api.get(`/bookings/rooms/${id}`),
  getAvailableSeats: (roomId, date, startTime, endTime) =>
    api.get(`/bookings/rooms/${roomId}/seats`, { params: { date, startTime, endTime } }),
  createBooking: (data) => api.post('/bookings', data),
  cancelBooking: (id) => api.delete(`/bookings/${id}`),
  getMyBookings: () => api.get('/bookings/my'),
};

// Meetings
export const meetingApi = {
  getAvailableDesks: (date, startTime, endTime) =>
    api.get('/meetings/available-desks', { params: { date, startTime, endTime } }),
  bookMeeting: (data) => api.post('/meetings', data),
  cancelMeeting: (id) => api.delete(`/meetings/${id}`),
  getMyMeetings: () => api.get('/meetings/my'),
};

export default api;