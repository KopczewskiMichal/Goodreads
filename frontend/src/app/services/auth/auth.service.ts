import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { switchMap, catchError,tap } from 'rxjs/operators';
import { User } from './../models/user.model';


@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl: string = environment.apiUrl;
  private http = inject(HttpClient);

  private userSubject = new BehaviorSubject<User | null>(null);
  public user$ = this.userSubject.asObservable();
  public isAdmin = false;

  public constructor() {
    this.loadUserFromStorage();
  }


  public login(identifier: string, password: string): Observable<User> {
    const url = `${this.apiUrl}/api/auth/login?identifier=${identifier}&password=${password}`;
    const userDetailsUrl = `${this.apiUrl}/api/user/profile/user-dto`;
  
    return this.http.post<{ id: number, isAdmin: boolean }>(url, {}, { withCredentials: true }).pipe(
      tap((response) => this.isAdmin = response.isAdmin),
      switchMap(() => this.http.get<User>(userDetailsUrl, { withCredentials: true })),
      tap((user) => {
        this.userSubject.next(user);
        localStorage.setItem('user', JSON.stringify(user));
      }),
      catchError((error) => {
        console.error('Login failed:', error);
        throw error;
      })
    );
  }

  public logout(): void {
    console.log('Logout');
    this.http.post(`${this.apiUrl}/api/auth/logout`, {}, { withCredentials: true, responseType: "text" }).subscribe(() => {
      this.userSubject.next(null);
      localStorage.removeItem('user');
      this.isAdmin = false;
    });
  }
  

  public get currentUser(): User | null {
    return this.userSubject.value;
  }

  public get isLoggedIn(): boolean {
    return !!this.userSubject.value;
  }
  

  private loadUserFromStorage(): void {
    const userData = localStorage.getItem('user');
    if (userData) {
      this.userSubject.next(JSON.parse(userData));
    }
  }
}
