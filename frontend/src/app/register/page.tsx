import { RegisterForm } from "@/components/auth/RegisterForm";
import { Header } from "@/components/layout/Header";

export default function RegisterPage() {
  return (
    <>
      <Header />
      <main
        className="flex min-h-screen flex-col items-center justify-center p-4 sm:p-8"
        style={{
          backgroundImage: "url('/images/fildAventure.jpg')",
          backgroundSize: "cover",
          backgroundPosition: "center",
        }}
      >
        <div className="absolute inset-0 bg-black/30 backdrop-blur-sm" />
        <div className="z-10">
          <RegisterForm />
        </div>
      </main>
    </>
  );
}
