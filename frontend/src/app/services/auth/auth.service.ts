import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  public apiUrl: string = environment.apiUrl;
  private http = inject(HttpClient);

  public login(identifier: string, password: string): Observable<any> {
    const url = `${this.apiUrl}/api/auth/login?identifier=${identifier}&password=${password}`;

    return this.http.post<{isAdmin: boolean; id: number}>(url, {},  { withCredentials: true }).pipe(
      map((response) => {
        
        console.log(response.isAdmin, response.id);
        
        return response;
      }),
      catchError((error: any) => {
        console.error('Login failed:', error);
        throw error;
      })
    );
  }
}
