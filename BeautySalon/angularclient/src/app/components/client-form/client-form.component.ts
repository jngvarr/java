import {Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ClientService} from '../../services/client.service';
import {Client} from '../../model/entities/client';

@Component({
  selector: 'app-client-form',
  templateUrl: './client-form.component.html',
  styleUrls: ['./client-form.component.scss']
})
export class ClientFormComponent {
  isEdit: boolean | undefined;
  client: Client;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private clientService: ClientService) {
    this.client = new Client();
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      const clientId = params['id'];
      if (clientId) {
        this.clientService.findById(clientId).subscribe(data => {
          this.client = data;
          this.isEdit = true;
        });
      }
    });
  }

  onSubmit() {
    this.clientService.save(this.client).subscribe(result => this.gotoClientList());
  }

  gotoClientList() {
    this.router.navigate(['/clients']);
  }
}
