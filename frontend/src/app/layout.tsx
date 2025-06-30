import { Header } from "@/components/layout/Header";
import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";

export const metadata: Metadata = {
  title: "Simulador de Criaturas",
  description: "Simulação de Criaturas Saltitantes",
};

const inter = Inter({ subsets: ["latin"] });

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="pt-br">
      <body className={`${inter.className} antialiased font-retro`}>
        <Header />
        {children}
      </body>
    </html>
  );
}
