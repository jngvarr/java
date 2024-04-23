import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Service} from "../model/entities/service";

@Injectable({
  providedIn: 'root'
})
export class ServiceForServices {
  private servicesUrl: string;

  constructor(private http: HttpClient) {
    this.servicesUrl = 'http://localhost:8082/services';
  }

  public findAll(): Observable<Service[]> {
    return this.http.get<Service[]>(this.servicesUrl);
  }
}
