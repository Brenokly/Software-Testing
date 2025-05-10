import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api/simulacao", // URL base, que pode ser alterada para produção
  headers: {
    "Content-Type": "application/json",
  },
});

export default api;
