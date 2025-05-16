// utils/validators/iterationStatusSchema.ts
import { z } from "zod";

export const CreatureResponseSchema = z.object({
  id: z.number(),
  x: z.number(),
  gold: z.number(),
});

export const IterationStatusSchema = z.object({
  statusCreatures: z.array(CreatureResponseSchema),
  inactiveCreatures: z.array(CreatureResponseSchema),
  iterationCount: z.number(),
  finished: z.boolean(),
});

// Inferindo o tipo TypeScript a partir do schema
export type IterationStatusDTO = z.infer<typeof IterationStatusSchema>;
