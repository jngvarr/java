import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Client} from "../model/entities/client";
import {User} from "../model/entities/user";

@Injectable({
  providedIn: 'root'
})
export class RegistrationService {
  private usersUrl: string;

  constructor(private http: HttpClient) {
    this.usersUrl = 'http://localhost:8765/users';
  }

  save(user: User) {
    return this.http.post<User>(this.usersUrl + "/registration", user);
  }
}
