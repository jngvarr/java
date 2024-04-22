import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ClientListComponent } from './components/client-list/client-list.component';
import { ClientFormComponent } from './components/client-form/client-form.component';

const routes: Routes = [
  { path: 'clients', component: ClientListComponent },
  { path: 'clients/delete', component: ClientListComponent },
  { path: 'clients/create', component: ClientFormComponent },
  { path: 'clients/update', component: ClientFormComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
