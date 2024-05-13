import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Visit} from "../model/entities/visit";
import {Time} from "@angular/common";

@Injectable({
  providedIn: 'root'
})
export class ApptService {

  private apptUrl: string;

  constructor(private http: HttpClient) {
    // this.apptUrl = 'http://localhost:8085/visits';
    this.apptUrl = 'http://localhost:8765/visits';
  }

  public findAll(): Observable<Visit[]> {
    return this.http.get<Visit[]>(this.apptUrl);
  }

  public findByDate(date: Date): Observable<Visit[]> {
    return this.http.get<Visit[]>(this.apptUrl + `/by-date/${date}`);
  }

  findByStartTime(time: Time) {
    return this.http.get<Visit[]>(this.apptUrl + `/by-time/${time}`)
  }

  public save(appt: Visit) {
    return this.http.post<Visit>(this.apptUrl + "/create", appt);
  }

  public update(updatedVisit: Visit) {
    return this.http.put<Visit>(this.apptUrl + `/update/${updatedVisit.id}`, updatedVisit);
  }

    public delete(id: number | undefined) {
    return this.http.delete<Visit>(this.apptUrl + `/delete/${id}`);
  }

  findById(apptId: number) {
    return this.http.get<Visit>(this.apptUrl + `/${apptId}`)
  }

  findByService(id: number) {
    return this.http.get<Visit[]>(this.apptUrl + `/by-service/${id}`)
  }

  findByClient(value: string) {
    return this.http.get<Visit[]>(this.apptUrl + `/by-client/${value}`)
  }

  findByMaster(value: number) {
    return this.http.get<Visit[]>(this.apptUrl + `/by-master/${value}`)
  }

}
