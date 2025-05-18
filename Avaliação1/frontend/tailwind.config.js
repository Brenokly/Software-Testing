/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./app/**/*.{js,ts,jsx,tsx}", // Suporte ao App Router
    "./components/**/*.{js,ts,jsx,tsx}", // Seus componentes
  ],
  theme: {
    extend: {
      fontFamily: {
        retro: ["'Press Start 2P'", "cursive"], // Fonte estilo Terraria
      },
    },
  },
  plugins: [],
};
