import {Injectable} from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  public apiUrl: string = '/api'

  constructor() {
  }
}
