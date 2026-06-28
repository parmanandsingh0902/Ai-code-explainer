import api from './axios';

export const authApi = {
  register: (data) => api.post('/api/auth/register', data),
  login: (data) => api.post('/api/auth/login', data),
};

export const analysisApi = {
  explain: (data) => api.post('/api/analysis/explain', data),
  review: (data) => api.post('/api/analysis/review', data),
  complexity: (data) => api.post('/api/analysis/complexity', data),
  history: (params) => api.get('/api/analysis/history', { params }),
  getById: (id) => api.get(`/api/analysis/${id}`),
  delete: (id) => api.delete(`/api/analysis/${id}`),
  dashboard: () => api.get('/api/dashboard'),
};

export const adminApi = {
  users: () => api.get('/api/admin/users'),
  statistics: () => api.get('/api/admin/statistics'),
  deleteUser: (id) => api.delete(`/api/admin/user/${id}`),
};
