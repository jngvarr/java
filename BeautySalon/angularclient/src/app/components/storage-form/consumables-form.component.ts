import {Component} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {StorageService} from "../../services/storage.service";
import {Consumable} from "../../model/entities/consumable";
import {Unit} from "../../model/unit";

@Component({
  selector: 'app-consumables-form',
  templateUrl: './consumables-form.component.html',
  styleUrl: './consumables-form.component.scss'
})
export class ConsumablesFormComponent {
  consumable: Consumable;
  units: Unit[] | undefined;
  unitValues = Object.values(Unit);
  edit: boolean | undefined;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private storageService: StorageService) {
    this.consumable = new Consumable();
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      const consumableId = params['id'];
      if (consumableId) {
        this.storageService.findById(consumableId).subscribe(data => {
          this.consumable = data;
          this.edit = true;
        });
      }
    });
  }

  onSubmit() {
    this.storageService.save(this.consumable).subscribe(result => this.gotoConsumableList());
  }

  gotoConsumableList() {
    this.router.navigate(['/storage']);
  }
}


