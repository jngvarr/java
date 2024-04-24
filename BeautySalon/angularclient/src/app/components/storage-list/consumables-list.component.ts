import {Component, OnInit} from '@angular/core';
import {Consumable} from "../../model/entities/consumable";
import {Router} from "@angular/router";
import {StorageService} from "../../services/storage.service";

@Component({
  selector: 'app-consumables-list',
  templateUrl: './consumables-list.component.html',
  styleUrl: './consumables-list.component.scss'
})
export class ConsumablesListComponent implements OnInit{
  consumables: Consumable[] | undefined;
  consumable: Consumable | undefined;

  constructor(private storageService: StorageService, private router: Router) {
  }
  ngOnInit() {
    this.loadConsumables();
  }

  loadConsumables() {
    this.storageService.findAll().subscribe(
      data => {
        console.log("Получены данные:", data); // Вывод полученных данных в консоль
        this.consumables = data;
      },
      error => {
        console.error("Ошибка при загрузке данных:", error); // Вывод ошибки в консоль
      }
    );
  }
  // loadConsumables() {
  //   this.storageService.findAll().subscribe(data => {
  //     this.consumables = data;
  //
  //   });
  // }
  searchByTitle(title: string) {
    this.storageService.findByTitle(title).subscribe((data: Consumable[]) => {
      this.consumables = data;
    });
  }
  deleteConsumable(consumable: Consumable) {
    if (confirm('Вы уверены, что хотите удалить расходник?')) {
      this.storageService.delete(consumable.id).subscribe(() => {
        this.loadConsumables();
      });
    }
  }

  updateConsumable(consumable: Consumable) {
    this.router.navigate(['/consumables/update', consumable.id]);
  }
}
