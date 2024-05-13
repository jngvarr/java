import {Component} from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  log: boolean = false;
  title: string | undefined;
  image = '../assets/images/background.jpg';
  // constructor() {
  //   this.title = 'Beauty Salon';
  // }
  login() {
    if (!this.log) {
      this.log = true;
    }
  }

  registration() {

  }
}
