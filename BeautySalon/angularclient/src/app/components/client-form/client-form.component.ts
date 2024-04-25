import {Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ClientService} from '../../services/client.service';
import {Client} from '../../model/entities/client';

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
    private clientService: ClientService) {
    this.client = new Client();
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      const clientId = params['id'];
      if (clientId) {
        this.clientService.findById(clientId).subscribe(data => {
          this.client = data;
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
