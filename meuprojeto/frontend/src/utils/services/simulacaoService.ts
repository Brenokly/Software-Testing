// utils/services/simulacaoService.ts
import api from "@/utils/api";
import {
  IterationStatusDTO,
  IterationStatusSchema,
} from "@/utils/validators/iterationStatusSchema";

function validarResposta(data: unknown): IterationStatusDTO {
  const parsed = IterationStatusSchema.safeParse(data);
  if (!parsed.success) {
    console.error("Resposta da API inválida:", parsed.error);
    throw new Error("Erro ao validar dados da simulação");
  }
  return parsed.data;
}

export async function iniciarSimulacao(quantidade: number): Promise<IterationStatusDTO> {
  const response = await api.post("/iniciar", { quantidade });
  return validarResposta(response.data);
}

export async function iterar(): Promise<IterationStatusDTO> {
  const response = await api.post("/iterar");
  return validarResposta(response.data);
}

export async function resetar(): Promise<IterationStatusDTO> {
  const response = await api.post("/resetar");
  return validarResposta(response.data);
}

export async function finalizar(): Promise<IterationStatusDTO> {
  const response = await api.get("/finalizar");
  return validarResposta(response.data);
}

export async function getStatus(): Promise<IterationStatusDTO> {
  const response = await api.get("/status");
  return validarResposta(response.data);
}

export async function getCriaturaAtual(): Promise<number> {
  const response = await api.get<number>("/criatura-atual");
  return response.data;
}
