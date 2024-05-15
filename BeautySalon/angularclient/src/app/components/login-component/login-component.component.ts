import { Component } from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import * as Http from "http";

@Component({
  selector: 'login',
  templateUrl: './login-component.component.html',
  styleUrl: './login-component.component.scss'
})
export class LoginComponentComponent {
  model: any = {};
  loading: boolean=false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: Http
  ) { }

  ngOnInit() {
    sessionStorage.setItem('token', '');
  }

  login() {
    let url = 'http://localhost:8082/login';
    let result = this.http.post(url, {
      userName: this.model.username,
      password: this.model.password
    }).map(res => res.json()).subscribe(isValid => {
      if (isValid) {
        sessionStorage.setItem(
          'token',
          btoa(this.model.username + ':' + this.model.password)
        );
        this.router.navigate(['']);
      } else {
        alert("Authentication failed.");
      }
    });
  }
}
