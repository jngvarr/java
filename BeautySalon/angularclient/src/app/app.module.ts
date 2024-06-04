import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {AppRoutingModule} from './app-routing.module';
import {FormsModule} from '@angular/forms';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
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
import {ApptService} from "./services/appt.service";
import {StaffService} from "./services/staff.service";
import {NgOptimizedImage} from "@angular/common";
import { RegistrationFormComponent } from './components/registration-form/registration-form.component';
import { LoginFormComponent } from './components/login-component/login-form.component';
import {AuthService} from "./services/auth.service";
import {AuthInterceptor} from "./model/interceptors/auth.interceptor";
import {ApiService} from "./services/api-service";

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
    ApptListComponent,
    RegistrationFormComponent,
    LoginFormComponent
  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        HttpClientModule,
        FormsModule,
        NgOptimizedImage
    ],
  providers: [ClientService, ServiceForServices, StorageService, ApptService, StaffService, AuthService, ApiService,
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true } ],
  bootstrap: [AppComponent]
})
export class AppModule { }
