import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LogListComponent } from './log-list/log-list.component';
import { LogDetailsComponent } from './log-details/log-details.component';
import { AlertListComponent } from './alert-list/alert-list.component';
import { AlertDetailsComponent } from './alert-details/alert-details.component';
import { HttpClientModule } from '@angular/common/http';
import { UserDetailsComponent } from './user-details/user-details.component';
import { UserListComponent } from './user-list/user-list.component';
import { AlertCreateComponent } from './alert-create/alert-create.component';
import { FormsModule } from '@angular/forms';
import { AlertModifyComponent } from './alert-modify/alert-modify.component';

@NgModule({
  declarations: [
    AppComponent,
    AlertDetailsComponent,
    AlertListComponent,
    LogDetailsComponent,
    LogListComponent,
    UserDetailsComponent,
    UserListComponent,
    AlertCreateComponent,
    AlertModifyComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
