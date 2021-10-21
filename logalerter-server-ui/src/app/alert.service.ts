import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Alert } from './alert';
import { Note } from './note';

@Injectable({
  providedIn: 'root'
})
export class AlertService {
  private baseUrl = 'http://localhost:8090/alerts';

  constructor(private http: HttpClient) { }

  getAlert(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/${id}`);
  }

  getAlertsList(): Observable<any> {
    return this.http.get(`${this.baseUrl}`);
  }

  getNotesListForAlert(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/${id}/notes`);
  }

  createAlert(alert: Alert) {
    return this.http.post(`${this.baseUrl}/${alert.logId}`, alert, {
      params: {
        'label': alert.label,
        'regex': alert.regex,
        'severity': String(alert.severity)
      }});
  }

  modifyAlert(alert: Alert) {
    return this.http.put(`${this.baseUrl}/${alert.id}`, alert, {
      params: {
        'label': alert.label,
        'regex': alert.regex,
        'logId': String(alert.logId),
        'severity': String(alert.severity)
      }});
  }
  addNoteForAlert(newNote: Note) {
    return this.http.post(`${this.baseUrl}/${newNote.alertId}/notes`, newNote, {
      params: {
        'email': newNote.email,
        'info': newNote.info
      }});
  }
}