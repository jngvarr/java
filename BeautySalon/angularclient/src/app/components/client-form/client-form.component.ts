import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ClientService } from '../../services/client-service.service';
import { Client } from '../../model/entities/client';
import {log} from "node:util";

@Component({
  selector: 'app-Client-form',
  templateUrl: './Client-form.component.html',
  styleUrls: ['./Client-form.component.scss']
})
export class ClientFormComponent {

  client: Client;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private ClientService: ClientService) {
    this.client = new Client();
  }

  onSubmit() {
    this.ClientService.save(this.client).subscribe(result => this.gotoClientList());
  }

  gotoClientList() {
    this.router.navigate(['/clients']);
  }
}
