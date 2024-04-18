import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'app-car',
  templateUrl: './car.component.html',
  styleUrl: './car.component.scss'
})
export class CarComponent implements OnInit {
  name: string | undefined;
  speed: number | undefined;
  model: string | undefined;
  colors: Colors = { car: '', salon: '', wheels: '' };
  options: string[] | undefined;
  isEdit: boolean = false;

  // test: any;

  constructor() {
  }


  ngOnInit() {
    this.name = 'AUDI';
    this.speed = 235;
    this.model = 'RS8';
    this.colors = {
      car: 'белый',
      salon: 'черный',
      wheels: 'серебристый'
    };
    this.options = ["ABS", "Автопилот", "Авто паркинг"]
    // this.test =true
  }

  carSelect(carName: string) {
    if (carName == 'bmv') {
      this.name = 'BMW';
      this.speed = 280;
      this.model = 'M5';
      this.colors = {
        car: 'синий',
        salon: 'белый',
        wheels: 'серебристый'
      };
      this.options = ["ABS", "Автопилот", "Авто паркинг"]
    } else if (carName == 'audi') {
      this.name = 'AUDI';
      this.speed = 235;
      this.model = 'RS8';
      this.colors = {
        car: 'белый',
        salon: 'черный',
        wheels: 'серебристый'
      };
      this.options = ["ABS", "Автопилот", "Панорамная крыша"]
    } else {
      this.name = 'Mercedes';
      this.speed = 180;
      this.model = 'clk-200';
      this.colors = {
        car: 'красный',
        salon: 'черный',
        wheels: 'серебристый'
      };
      this.options = ["ABS", "Автопилот"]
    }
  }

  addOpt(option: string) {
    this.options?.unshift(option);
    return false;
  }

  showEdit() {
    this.isEdit = !this.isEdit;
  }

  deleteOpt(option: any) {
    // console.log(option);
    // @ts-ignore
    for (let i = 0; i < this.options.length; i++) {
      // @ts-ignore
      if (this.options[i] == option) {
        this.options?.splice(i, 1);
        break;
      }
    }
  }
}

interface Colors {
  car: string;
  salon: string;
  wheels: string;
}
