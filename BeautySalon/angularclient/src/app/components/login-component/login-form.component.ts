import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {HttpClient} from "@angular/common/http";
import {catchError, map} from 'rxjs/operators';
import {of} from 'rxjs';
import {User} from "../../model/entities/user";

@Component({
  selector: 'login',
  templateUrl: './login-form.component.html',
  styleUrl: './login-form.component.scss'
})
export class LoginFormComponent implements OnInit{
  user: User = new User();
  loading: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient
  ) {
  }

  ngOnInit() {
    sessionStorage.setItem('token', '');
  }

  login() {
    let url = 'http://localhost:8082/login';
    this.http.post(url, {
      userName: this.user.userName,
      password: this.user.password
    }).pipe(
      map((res: any) => res.token),
      catchError(error => {
        console.error('Login failed', error);
        return of(null);
      })
    ).subscribe(token => {
      if (token) {
        sessionStorage.setItem('token', token);
        this.router.navigate(['']);
      } else {
        alert("Authentication failed.");
      }
    });
  }
  loadedData(){
return
  }
}
