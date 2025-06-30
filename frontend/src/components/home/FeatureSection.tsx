import Image from "next/image"; // <-- CORREÇÃO: Importando o componente Image do Next.js
import React from "react";
import { IconType } from "react-icons";

interface FeatureSectionProps {
  title: string;
  description: string;
  imageUrl: string;
  imageAlt: string;
  Icon: IconType;
  reverse?: boolean;
}

export const FeatureSection: React.FC<FeatureSectionProps> = ({
  title,
  description,
  imageUrl,
  imageAlt,
  Icon,
  reverse = false,
}) => {
  const textContainerClasses = `w-full lg:w-1/2 p-8 flex flex-col justify-center items-center lg:items-start`;
  const imageContainerClasses = `w-full lg:w-1/2 p-8 flex items-center justify-center`;

  const textOrder = reverse ? "lg:order-last" : "";

  return (
    <section className="container mx-auto py-12 md:py-20 flex flex-wrap lg:flex-nowrap items-center">
      <div className={`${textContainerClasses} ${textOrder}`}>
        <div className="flex items-center gap-4 mb-4">
          <Icon className="text-5xl text-gray-700" />
          <h2
            className="text-3xl md:text-4xl font-bold text-gray-800"
            style={{ fontFamily: '"Press Start 2P", cursive' }}
          >
            {title}
          </h2>
        </div>
        <div className="bg-white bg-opacity-75 p-6 rounded-lg border-4 border-gray-300 shadow-md">
          <p className="text-lg text-gray-700 leading-relaxed text-center lg:text-left">
            {description}
          </p>
        </div>
      </div>
      <div className={imageContainerClasses}>
        {/* CORREÇÃO: Trocamos a tag <img> pelo componente <Image> */}
        <Image
          src={imageUrl}
          alt={imageAlt}
          width={600} // Largura original da imagem (para aspect ratio)
          height={400} // Altura original da imagem (para aspect ratio)
          className="w-full max-w-md border-4 border-black shadow-lg rounded-lg object-cover"
        />
      </div>
    </section>
  );
};
