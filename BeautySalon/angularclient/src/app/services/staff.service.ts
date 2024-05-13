import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Employee} from "../model/entities/employee";
import {Client} from "../model/entities/client";

@Injectable({
  providedIn: 'root'
})
export class StaffService {
  private staffUrl: string;

  constructor(private http: HttpClient) {
    this.staffUrl = 'http://localhost:8765/staff';
    // this.staffUrl = 'http://localhost:8084/staff';
  }

  public findAll(): Observable<Employee[]> {
    return this.http.get<Employee[]>(this.staffUrl);
  }

  public findByName(name: string, lastName: string): Observable<Employee[]> {
    return this.http.get<Employee[]>(this.staffUrl + `/by-name?name=${name}&lastName=${lastName}`);
  }

  findByFunction(value: string) {
    return this.http.get<Employee[]>(this.staffUrl + `/by-function/${value}`)
  }

  public save(employee: Employee) {
    return this.http.post<Employee>(this.staffUrl + "/create", employee);
  }

  public update(updatedEmployee: Employee) {
    return this.http.put<Employee>(this.staffUrl + `/update/${updatedEmployee.id}`, updatedEmployee);
  }

  public delete(id: number | undefined) {
    return this.http.delete<Employee>(this.staffUrl + `/delete/${id}`);
  }

  findById(employeeId: number) {
    return this.http.get<Employee>(this.staffUrl + `/${employeeId}`)
  }

  findByPhone(value: string) {
    return this.http.get<Employee[]>(this.staffUrl + `/by-contact/${value}`)
  }
}
