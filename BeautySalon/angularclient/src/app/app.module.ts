import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {AppRoutingModule} from './app-routing.module';
import {FormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {AppComponent} from './app.component';
import {ClientListComponent} from './components/client-list/client-list.component';
import {ClientFormComponent} from './components/client-form/client-form.component';
import {ClientServiceService} from './services/client-service.service';
import { ServiceListComponent } from './components/service-list/service-list.component';
import { ServiceFormComponent } from './components/service-form/service-form.component';


@NgModule({
  declarations: [
    AppComponent,
    ClientListComponent,
    ClientFormComponent,
    ServiceListComponent,
    ServiceFormComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [ClientServiceService],
  bootstrap: [AppComponent]
})
export class AppModule { }
