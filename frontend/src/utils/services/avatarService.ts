
export const avatarImagePaths: string[] = [
  "/avatars/avatar1.png",
  "/avatars/avatar2.png",
  "/avatars/avatar3.png",
  "/avatars/avatar4.png",
  "/avatars/avatar5.png",
  "/avatars/avatar6.png",
  "/avatars/avatar7.png",
  "/avatars/avatar8.png",
  "/avatars/avatar9.png",
  "/avatars/avatar10.png",
];

export const getAvatarById = (id: number): string => {
    if (id <= 0 || id > avatarImagePaths.length) {
        return avatarImagePaths[0]; // Fallback para o primeiro avatar
    }
    return avatarImagePaths[id - 1];
};

export const getAllAvatars = (): { id: number; path: string }[] => {
    return avatarImagePaths.map((path, index) => ({
        id: index + 1,
        path: path,
    }));
};