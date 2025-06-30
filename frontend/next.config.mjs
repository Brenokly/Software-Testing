/** @type {import('next').NextConfig} */
const nextConfig = {
  // A configuração de imagens fica aqui
  images: {
    // Definimos os domínios externos dos quais podemos carregar imagens
    remotePatterns: [
      {
        protocol: "https",
        hostname: "placehold.co",
        port: "",
        pathname: "/**",
      },
      // Se você for usar outras fontes de imagem, adicione-as aqui
    ],
  },
};

export default nextConfig;
