import api from "@/utils/api";
import { ApiError, ErrorResponse } from "@/utils/validators/ApiError";
import {
  IterationStatusDTO,
  IterationStatusSchema,
} from "@/utils/validators/iterationStatusSchema";
import { isAxiosError } from "axios";


function validarResposta(data: unknown): IterationStatusDTO {
  const parsed = IterationStatusSchema.safeParse(data);
  if (!parsed.success) {
    console.error("Resposta da API inválida:", parsed.error);
    throw new Error("Erro ao validar dados da simulação");
  }
  return parsed.data;
}

function tratarErro(error: unknown): never {
  if (isAxiosError(error)) {
    const data = error.response?.data;

    if (
      typeof data === "object" &&
      data !== null &&
      "message" in data &&
      "error" in data &&
      "status" in data &&
      "timestamp" in data
      ) {
      throw new ApiError(data as ErrorResponse);
      }

    throw new Error("Erro de API sem dados válidos.");
  }

  throw new Error("Erro desconhecido ao tentar comunicar com o servidor.");
}

export async function iniciarSimulacao(quantidade: number): Promise<IterationStatusDTO> {
  try {
    const response = await api.post("/iniciar", { quantidade });
    return validarResposta(response.data);
  } catch (error) {
    tratarErro(error);
  }
}

export async function iterar(): Promise<IterationStatusDTO> {
  try {
    const response = await api.post("/iterar");
    return validarResposta(response.data);
  } catch (error) {
    tratarErro(error);
  }
}

export async function resetar(): Promise<IterationStatusDTO> {
  try {
    const response = await api.post("/resetar");
    return validarResposta(response.data);
  } catch (error) {
    tratarErro(error);
  }
}

export async function finalizar(): Promise<IterationStatusDTO> {
  try {
    const response = await api.get("/finalizar");
    return validarResposta(response.data);
  } catch (error) {
    tratarErro(error);
  }
}

export async function getStatus(): Promise<IterationStatusDTO> {
  try {
    const response = await api.get("/status");
    return validarResposta(response.data);
  } catch (error) {
    tratarErro(error);
  }
}

export async function getCriaturaAtual(): Promise<number> {
  try {
    const response = await api.get<number>("/criatura-atual");
    return response.data;
  } catch (error) {
    tratarErro(error);
  }
}
