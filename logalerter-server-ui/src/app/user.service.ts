import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private baseUrl = 'http://localhost:8090/users';

  constructor(private http: HttpClient) { }

  getUser(email: string): Observable<any> {
    console.log("getUser email=["+email+"]");
    let encodedEmail = btoa(email);
    return this.http.get(`${this.baseUrl}/${encodedEmail}`);
  }

  getUsersList(): Observable<any> {
    return this.http.get(`${this.baseUrl}`);
  }
}