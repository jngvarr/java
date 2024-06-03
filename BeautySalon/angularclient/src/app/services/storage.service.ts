import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {catchError, Observable, tap} from "rxjs";
import {Consumable} from "../model/entities/consumable";
import {ApiService} from "./api-service";

@Injectable({
  providedIn: 'root'
})
export class StorageService {
  private storagesUrl: string;

  constructor(private http: HttpClient, apiService : ApiService) {
    this.storagesUrl = apiService.apiUrl + '/storage';
  }
  public findAll(): Observable<Consumable[]> {
    return this.http.get<Consumable[]>(this.storagesUrl).pipe(
      tap(data => console.log(data)), // Вывод ответа в консоль
      catchError(error => {
        console.error(error); // Логирование ошибки, если возникает
        throw error; // Пробрасывание ошибки дальше
      })
    );
  }
  findById(consumableId: number) {
    return this.http.get<Consumable>(this.storagesUrl + `/${consumableId}`)
  }
  // public findAll(): Observable<Consumable[]> {
  //   let cons: Observable<Consumable[]> = this.http.get<Consumable[]>(this.storagesUrl);
  //   console.log(cons);
  //   return cons;
  // }

  public findByTitle(title: string): Observable<Consumable[]> {
    return this.http.get<Consumable[]>(this.storagesUrl + `/byTitle/${title}`);
  }
  public save(consumable: Consumable) {
    return this.http.post<Consumable>(this.storagesUrl + "/create", consumable);
  }
  public delete(id: number | undefined) {
    return this.http.delete<Consumable>(this.storagesUrl + `/delete/${id}`);
  }
}
