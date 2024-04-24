import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {catchError, Observable, tap} from "rxjs";
import {Client} from "../model/entities/client";
import {Consumable} from "../model/entities/consumable";

@Injectable({
  providedIn: 'root'
})
export class StorageService {
  private storagesUrl: string;

  constructor(private http: HttpClient) {
    this.storagesUrl = 'http://localhost:8083/storage';
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
  findById(clientId: number) {
    return this.http.get<Client>(this.storagesUrl + `/${clientId}`)
  }
  // public findAll(): Observable<Consumable[]> {
  //   let cons: Observable<Consumable[]> = this.http.get<Consumable[]>(this.storagesUrl);
  //   console.log(cons);
  //   return cons;
  // }

  public findByTitle(title: string): Observable<Consumable[]> {
    return this.http.get<Consumable[]>(this.storagesUrl + `/byTitle/${title}`);
  }
  public save(client: Client) {
    return this.http.post<Client>(this.storagesUrl + "/create", client);
  }
  public delete(id: number | undefined) {
    return this.http.delete<Consumable>(this.storagesUrl + `/delete/${id}`);
  }
}
