import { LoginForm } from "@/components/auth/LoginForm";

export default function LoginPage() {
  return (
    // O Header fica fixo no topo
    <>
      <main
        className="flex min-h-screen flex-col items-center justify-center p-4 sm:p-8"
        // Usando sua imagem 'fildAventure.jpg' como um fundo de cenário épico.
        // Certifique-se de que a imagem está em 'public/images/fildAventure.jpg'
        style={{
          backgroundImage: "url('/images/fildAventure.jpg')",
          backgroundSize: "cover",
          backgroundPosition: "center",
        }}
      >
        {/* Adicionamos um overlay escuro para garantir a legibilidade do formulário */}
        <div className="absolute inset-0 bg-black/30 backdrop-blur-sm" />

        {/* O formulário fica em uma camada acima do fundo e do overlay */}
        <div className="z-10">
          <LoginForm />
        </div>
      </main>
    </>
  );
}
