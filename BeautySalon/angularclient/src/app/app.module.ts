import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {AppRoutingModule} from './app-routing.module';
import {FormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {AppComponent} from './app.component';
import {ClientListComponent} from './components/client-list/client-list.component';
import {ClientFormComponent} from './components/client-form/client-form.component';
import {ClientService} from './services/client.service';
import { ServiceListComponent } from './components/service-list/service-list.component';
import { ServiceFormComponent } from './components/service-form/service-form.component';
import { ConsumablesListComponent } from './components/storage-list/consumables-list.component';
import { ConsumablesFormComponent } from './components/storage-form/consumables-form.component';
import { StaffListComponent } from './components/staff-list/staff-list.component';
import { StaffFormComponent } from './components/staff-form/staff-form.component';
import { ApptFormComponent } from './components/appt-form/appt-form.component';
import { ApptListComponent } from './components/appt-list/appt-list.component';
import {ServiceForServices} from "./services/service-for-services.service";
import {StorageService} from "./services/storage.service";


@NgModule({
  declarations: [
    AppComponent,
    ClientListComponent,
    ClientFormComponent,
    ServiceListComponent,
    ServiceFormComponent,
    ConsumablesListComponent,
    ConsumablesFormComponent,
    StaffListComponent,
    StaffFormComponent,
    ApptFormComponent,
    ApptListComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [ClientService, ServiceForServices, StorageService],
  bootstrap: [AppComponent]
})
export class AppModule { }
