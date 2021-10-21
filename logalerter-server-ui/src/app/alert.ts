export class Alert {
  id: number;
  label: string;
  regex: string;
  logId: number;
  severity: number;
  occurrences: number;
  lastOccurrence: Date;
  lastUserEmail: string;
}