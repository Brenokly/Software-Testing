"use client";

interface ControlsProps {
  onIterate: () => void;
  onReset: () => void;
  onToggleAuto: () => void;
  isAutoRunning: boolean;
  isFinished: boolean;
}

export const Controls = ({
  onIterate,
  onReset,
  onToggleAuto,
  isAutoRunning,
  isFinished,
}: ControlsProps) => {
  const buttonBaseClasses =
    "px-5 py-3 text-white border-2 border-black rounded-md font-bold transition-all disabled:bg-gray-500 disabled:shadow-none disabled:translate-y-0 disabled:translate-x-0";
  const buttonEffectClasses =
    "shadow-[4px_4px_0px_0px_rgba(0,0,0,1)] hover:translate-x-[-2px] hover:translate-y-[-2px] active:shadow-none active:translate-x-[2px] active:translate-y-[2px]";

  return (
    <div className="absolute bottom-5 left-1/2 -translate-x-1/2 flex items-center justify-center gap-4 bg-white/80 px-4 py-3 rounded-lg shadow-lg z-50">
      <button
        onClick={onIterate}
        disabled={isFinished}
        className={`${buttonBaseClasses} bg-blue-600 hover:bg-blue-700 ${
          !isFinished && buttonEffectClasses
        }`}
      >
        Iterar
      </button>
      <button
        onClick={onToggleAuto}
        disabled={isFinished}
        className={`${buttonBaseClasses} ${
          isAutoRunning
            ? "bg-yellow-600 hover:bg-yellow-700"
            : "bg-green-600 hover:bg-green-700"
        } ${!isFinished && buttonEffectClasses}`}
      >
        {isAutoRunning ? "Pausar" : "Auto"}
      </button>
      <button
        onClick={onReset}
        className={`${buttonBaseClasses} bg-red-600 hover:bg-red-700 ${buttonEffectClasses}`}
      >
        Resetar
      </button>
    </div>
  );
};
