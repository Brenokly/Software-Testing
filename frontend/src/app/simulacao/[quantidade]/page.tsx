import SimulacaoPageClient from "@/components/SimulacaoPageClient";
import { redirect } from "next/navigation";

export default async function SimulacaoPage({
  params,
}: {
  params: Promise<{ quantidade: string }>;
}) {
  const { quantidade } = await params;

  // Converte a quantidade para número
  const quantidadeNum = Number(quantidade);

  if (isNaN(quantidadeNum) || quantidadeNum <= 1 || quantidadeNum > 10) {
    redirect("/");
  }

  return <SimulacaoPageClient quantidadeCriaturas={quantidadeNum} />;
}
