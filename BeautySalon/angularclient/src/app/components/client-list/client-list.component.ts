import { Component, OnInit } from '@angular/core';
import { Client } from '../../model/entities/client';
import { ClientService } from '../../services/client-service.service';

@Component({
  selector: 'app-client-list',
  templateUrl: './client-list.component.html',
  styleUrls: ['./client-list.component.scss']
})
export class ClientListComponent implements OnInit {

  clients: Client[] | undefined;

  constructor(private clientService: ClientService) {
  }

  ngOnInit() {
    this.clientService.findAll().subscribe(data => {
      this.clients = data;
    });
  }

  deleteClient(client: Client) {
    this.clientService.delete(client.id)
  }
}
