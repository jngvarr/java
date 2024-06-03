import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {User} from "../model/entities/user";
import {ApiService} from "./api-service";

@Injectable({
  providedIn: 'root'
})
export class RegistrationService {
  private usersUrl: string;

  constructor(private http: HttpClient, apiService : ApiService) {
    this.usersUrl = apiService.apiUrl + '/users';
  }

  save(user: User) {
    return this.http.post<User>(this.usersUrl + "/registration", user);
  }
}
