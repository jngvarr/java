import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {HttpClient} from "@angular/common/http";
import {ApiService} from "./api-service";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  logged: boolean = false;
  username: string | undefined;

  constructor(private router: Router,
              private http: HttpClient,
              private apiService: ApiService) {
  }

  login(username: string | undefined, token: string) {
    if (typeof sessionStorage !== 'undefined') {
      sessionStorage.setItem('token', token);
    } else {
      alert("Authentication failed. Please check your username and password.");
    }
    this.logged = true;
    this.username = username;
    this.router.navigate(['']);
  }

  logout() {
    const token = sessionStorage.getItem('token');
    if (token) {
      this.http.get(`${this.apiService.apiUrl}/users/logout`).subscribe(
        () => {
          sessionStorage.removeItem('token');
          this.logged = false;
          this.username = undefined;
          this.router.navigate(['/login']);
        },
        (error) => {
          console.error('Logout failed', error);
        }
      );
    } else {
      this.router.navigate(['/login']);
    }
  }

  isLoggedIn(): boolean {
    return this.logged;
  }

  getUsername(): string | undefined {
    return this.username;
  }
}
