import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Service} from "../model/entities/service";
import {Consumable} from "../model/entities/consumable";

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
  findById(serviceId: number) {
    return this.http.get<Service>(this.servicesUrl + `/${serviceId}`)
  }
  public save(client: Service) {
    return this.http.post<Service>(this.servicesUrl + "/create", client);
  }
  public delete(serviceId: number | undefined){
    return this.http.delete<Service>(this.servicesUrl + `/delete/${serviceId}`);
  }
  public findByTitle(title: string): Observable<Consumable[]> {
    return this.http.get<Consumable[]>(this.servicesUrl + `/byTitle/${title}`);
  }

  findByDescription(description: string) {
    return this.http.get<Consumable[]>(this.servicesUrl + `/byDescription/${description}`);
  }

  findByConsumable(value: string) {
    return this.http.get<Consumable[]>(this.servicesUrl + `/byConsumable/${Consumable}`);
  }
}
