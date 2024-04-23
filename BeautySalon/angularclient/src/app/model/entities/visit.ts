import {Time} from "@angular/common";
import {Service} from "./service";
import {Client} from "./client";
import {Employee} from "./employee";

export class Visit {

  public id?: number;
  public visitDate?: Date;
  public startTime?: Time;
  public service!: Service;
  public client!: Client;
  public master!: Employee
  ;
}
