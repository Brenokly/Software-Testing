import SimulacaoPageClient from "@/components/SimulacaoPageClient";
import { redirect } from "next/navigation";

interface Props {
  params: {
    quantidade: string; // <- string, porque vem da URL
  };
}

export default function SimulacaoPage({ params }: Props) {
  const quantidade = Number(params.quantidade);

  if (isNaN(quantidade) || quantidade <= 1 || quantidade > 10) {
    redirect("/");
  }

  return <SimulacaoPageClient quantidadeCriaturas={quantidade} />;
}
