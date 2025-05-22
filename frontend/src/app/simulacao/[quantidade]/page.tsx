import SimulacaoPageClient from "@/components/SimulacaoPageClient";
import { redirect } from "next/navigation";

interface Props {
  params: {
    quantidade: number;
  };
}

export default async function SimulacaoPage({ params }: Props) {
  const { quantidade } = await params;

  if (isNaN(quantidade) || quantidade <= 1 || quantidade > 10) {
    redirect("/");
  }

  return <SimulacaoPageClient quantidadeCriaturas={quantidade} />;
}
