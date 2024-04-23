import {Component, OnInit} from '@angular/core';
import {Client} from '../../model/entities/client';
import {ClientServiceService} from '../../services/client-service.service';
import {Router} from "@angular/router";

@Component({
  selector: 'app-client-list',
  templateUrl: './client-list.component.html',
  styleUrls: ['./client-list.component.scss']
})
export class ClientListComponent implements OnInit {

  clients: Client[] | undefined;
  client: Client | undefined;

  constructor(private clientService: ClientServiceService, private router: Router) {
  }

  ngOnInit() {
    this.loadClients();
  }

  loadClients() {
    this.clientService.findAll().subscribe(data => {
      this.clients = data;
    });
  }
  searchByName(name: string, lastName: string) {
    this.clientService.findByName(name, lastName).subscribe((data: Client[]) => {
      this.clients = data;
    });
  }
  searchByPhone(value: string) {
    this.clientService.findByPhone(value).subscribe((data: Client[]) => {
      this.clients = data;
    });
  }
  deleteClient(client: Client) {
    if (confirm('Вы уверены, что хотите удалить клиента?')) {
      this.clientService.delete(client.id).subscribe(() => {
        this.loadClients();
      });
    }
  }

  updateClient(client: Client) {
    this.router.navigate(['/clients/update', client.id]);
  }

}
