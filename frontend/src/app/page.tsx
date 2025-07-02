import { FeatureSection } from "@/components/home/FeatureSection";
import Link from "next/link";
import { GiChessRook, GiCrossedSwords, GiLaurelsTrophy } from "react-icons/gi";

export default function HomePage() {
  return (
    <div className="bg-gray-50 text-gray-800">
      {/* Seção Hero */}
      <main
        className="pt-24 min-h-screen flex items-center justify-center bg-cover bg-center"
        style={{
          backgroundImage: "url('/images/ceu.png')",
        }}
      >
        <div className="text-center bg-white bg-opacity-80 p-10 rounded-lg shadow-2xl border-4 border-black max-w-2xl mx-4">
          <h1
            className="text-4xl md:text-6xl font-bold mb-4 text-gray-900"
            style={{ fontFamily: '"Press Start 2P", cursive' }}
          >
            Simulador de Criaturas
          </h1>
          <p className="text-lg md:text-xl mb-8 text-gray-700">
            Uma aventura estratégica onde apenas o mais astuto sobrevive. Crie
            sua conta, desafie o horizonte e suba no ranking!
          </p>
          <Link
            href="/register"
            className="inline-block text-lg px-8 py-4 bg-green-500 text-white border-2 border-black rounded-md hover:bg-green-600 shadow-[4px_4px_0px_0px_rgba(0,0,0,1)] hover:translate-x-[-2px] hover:translate-y-[-2px] transition-all font-bold"
          >
            Comece sua Jornada Agora!
          </Link>
        </div>
      </main>

      {/* Seção 1: O Que é a Simulação? */}
      <div className="bg-[#a7f3d0]">
        <FeatureSection
          Icon={GiCrossedSwords}
          title="Explore e Conquiste"
          description="Controle um exército de criaturas em um horizonte dinâmico. A cada turno, suas criaturas saltam pelo mapa, impulsionadas por sua fortuna em ouro. O objetivo é simples: dominar as outras e acumular ainda mais riquezas."
          imageUrl="/images/feature-explore.png"
          imageAlt="Criaturas pixel art em um campo verde"
        />
      </div>

      {/* Seção 2: Clusters e Guardiões (Alternado) */}
      <div className="bg-[#fef9c3]">
        <FeatureSection
          Icon={GiChessRook}
          title="Alianças e Perigos"
          description="Quando criaturas se encontram, elas se unem em um poderoso Cluster, somando suas forças. Mas cuidado! O misterioso Guardião do Horizonte vaga pelo mapa, eliminando clusters para absorver seu poder. Apenas os mais fortes sobrevivem."
          imageUrl="/images/feature-alliances.png"
          imageAlt="Um cluster de criaturas enfrentando um guardião"
          reverse={true} // Alterna a ordem
        />
      </div>

      {/* Seção 3: Competição e Estatísticas */}
      <div className="bg-[#dbeafe]">
        <FeatureSection
          Icon={GiLaurelsTrophy}
          title="Suba no Ranking"
          description="Crie sua conta para competir. Cada simulação bem-sucedida aumenta sua pontuação global. Acompanhe as estatísticas, veja quem são os mestres da simulação e lute para cravar seu nome no topo do ranking da guilda!"
          imageUrl="/images/feature-ranking.png"
          imageAlt="Um quadro de líderes no estilo pixel art"
        />
      </div>

      <footer className="text-center p-8 bg-gray-800 text-white border-t-4 border-black">
        <p>
          &copy; 2025 Simulador de Criaturas. Desenvolvido para a disciplina de
          Teste de Software.
        </p>
      </footer>
    </div>
  );
}
