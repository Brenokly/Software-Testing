import { z } from 'zod';

// Esquema de Login (continua o mesmo)
export const loginSchema = z.object({
  login: z.string().min(1, { message: "O login é obrigatório." }),
  password: z.string().min(8, { message: "A senha deve ter no mínimo 8 caracteres." }),
});
export type LoginFormInputs = z.infer<typeof loginSchema>;


export const registerSchema = z.object({
    login: z.string()
        .min(3, { message: "O login deve ter no mínimo 3 caracteres." })
        .regex(/^[a-z0-9_]+$/, { message: "Login pode conter apenas letras minúsculas, números e underline." }),
    password: z.string().min(8, { message: "A senha deve ter no mínimo 8 caracteres." }),
    confirmPassword: z.string().min(8, { message: "A confirmação de senha é obrigatória." }),
    avatarId: z.number().min(1, { message: "Por favor, escolha um avatar." }),
})
.refine((data) => data.password === data.confirmPassword, {
    message: "As senhas não correspondem.",
    path: ["confirmPassword"],
});

export type RegisterFormInputs = z.infer<typeof registerSchema>;