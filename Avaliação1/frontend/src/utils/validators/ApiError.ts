export type ErrorResponse = {
  error: string;
  message: string;
  status: number;
  timestamp: string;
};

export class ApiError extends Error {
  public readonly status: number;
  public readonly timestamp: string;
  public readonly error: string;
  public readonly rawMessage: string;

  constructor({ error, message, status, timestamp }: ErrorResponse) {
    // remove "quantidade: " se estiver presente
    const cleanMessage = message.includes(": ")
      ? message.split(": ").slice(1).join(": ")
      : message;

    super(cleanMessage);
    this.name = "ApiError";
    this.status = status;
    this.timestamp = timestamp;
    this.error = error;
    this.rawMessage = message;
  }
}
