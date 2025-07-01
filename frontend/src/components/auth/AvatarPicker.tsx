import { getAllAvatars } from "@/utils/services/avatarService";
import Image from "next/image";

interface AvatarPickerProps {
  selectedValue: number;
  onSelect: (id: number) => void;
}

export const AvatarPicker: React.FC<AvatarPickerProps> = ({
  selectedValue,
  onSelect,
}) => {
  const avatars = getAllAvatars();

  return (
    <div>
      <label
        className="block text-yellow-300 text-sm font-bold mb-2"
        style={{ fontFamily: '"Press Start 2P", cursive' }}
      >
        Escolha seu Avatar
      </label>
      <div className="grid grid-cols-5 gap-2 p-2 bg-[#2a1a1a] border-2 border-black rounded-md">
        {avatars.map((avatar) => (
          <button
            type="button"
            key={avatar.id}
            onClick={() => onSelect(avatar.id)}
            className={`relative w-16 h-16 rounded-lg overflow-hidden transition-all duration-200 ${
              selectedValue === avatar.id
                ? "border-4 border-yellow-400 scale-110"
                : "border-2 border-transparent hover:border-yellow-400"
            }`}
          >
            <Image
              src={avatar.path}
              alt={`Avatar ${avatar.id}`}
              fill
              className="object-cover"
              sizes="(max-width: 768px) 10vw, (max-width: 1200px) 5vw, 64px"
            />
          </button>
        ))}
      </div>
    </div>
  );
};
