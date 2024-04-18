import {NgModule} from '@angular/core';
import {BrowserModule, provideClientHydration} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {CarComponent} from './components/car/car.component';
import {FormsModule} from "@angular/forms";
import {DetailsComponent} from './components/details/details.component';
import {RouterModule, Routes} from "@angular/router";


const appRoutes: Routes = [
  {path: '', component: CarComponent},
  {path: 'details', component: DetailsComponent}
]

@NgModule({
  declarations: [
    AppComponent,
    CarComponent,
    DetailsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    RouterModule.forRoot(appRoutes)
  ],
  providers: [
    provideClientHydration()
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
