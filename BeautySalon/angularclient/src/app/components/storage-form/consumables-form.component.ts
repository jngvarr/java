import { Component } from '@angular/core';
import {Client} from "../../model/entities/client";
import {ActivatedRoute, Router} from "@angular/router";
import {ClientServiceService} from "../../services/client-service.service";
import {StorageService} from "../../services/storage.service";
import {Consumable} from "../../model/entities/consumable";

@Component({
  selector: 'app-consumables-form',
  templateUrl: './consumables-form.component.html',
  styleUrl: './consumables-form.component.scss'
})
export class ConsumablesFormComponent {
  consumable: Consumable;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private storageService: StorageService) {
    this.consumable = new Consumable();
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      const clientId = params['id'];
      if (clientId) {
        this.storageService.findById(clientId).subscribe(data => {
          this.consumable = data;
        });
      }
    });
  }

  onSubmit() {
    this.storageService.save(this.consumable).subscribe(result => this.gotoClientList());
  }

  gotoClientList() {
    this.router.navigate(['/clients']);
  }
}

