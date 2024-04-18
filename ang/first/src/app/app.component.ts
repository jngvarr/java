import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {FormsModule} from "@angular/forms";
import {bootstrapApplication} from "@angular/platform-browser";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, FormsModule],
  template: `
    <label for="name">Name:</label>
    <input type="text" id="name" [(ngModel)]="name" placeholder="Enter a name here" />
    <hr />
    <h1>Hello {{ name }}!</h1>
  `,
})
export class AppComponent {
  name = ''
  title = 'first';
}
bootstrapApplication(AppComponent);
