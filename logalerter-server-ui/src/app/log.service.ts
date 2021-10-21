import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LogService {
  private baseUrl = 'http://localhost:8090/logs';

  constructor(private http: HttpClient) { }

  getLog(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/${id}`);
  }

  getLogsList(): Observable<any> {
    return this.http.get(`${this.baseUrl}`);
  }
}