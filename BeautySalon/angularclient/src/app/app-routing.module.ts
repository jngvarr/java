import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ClientListComponent } from './components/client-list/client-list.component';
import { ClientFormComponent } from './components/client-form/client-form.component';
import {ServiceListComponent} from "./components/service-list/service-list.component";
import {Service} from "./model/entities/service";
import {ServiceFormComponent} from "./components/service-form/service-form.component";

const routes: Routes = [
  { path: 'clients', component: ClientListComponent },
  { path: 'services', component: ServiceListComponent },
  { path: 'services/create', component: ServiceFormComponent },

  { path: 'clients/delete', component: ClientListComponent },
  { path: 'clients/create', component: ClientFormComponent },
  { path: 'clients/update/:id', component: ClientFormComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
