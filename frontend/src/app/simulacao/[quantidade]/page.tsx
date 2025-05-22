import SimulacaoPageClient from "@/components/SimulacaoPageClient";
import { redirect } from "next/navigation";

interface Props {
  params: {
    quantidade: string;
  };
}

export default function SimulacaoPage({ params }: Props) {
  const quantidadeCriaturas = Number(params.quantidade);

  if (
    isNaN(quantidadeCriaturas) ||
    quantidadeCriaturas <= 1 ||
    quantidadeCriaturas > 10
  ) {
    redirect("/");
  }

  return <SimulacaoPageClient quantidadeCriaturas={quantidadeCriaturas} />;
}
