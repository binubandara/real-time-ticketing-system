// Interface representing a system log entry
export interface SystemLog {
  logTime: Date; // Timestamp of the log
  level: string; // Log severity level (INFO, WARNING, ERROR)
  message: string; // Log message content
}