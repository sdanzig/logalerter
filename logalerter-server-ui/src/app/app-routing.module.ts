import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LogListComponent } from './log-list/log-list.component';
import { LogDetailsComponent } from './log-details/log-details.component';
import { AlertListComponent } from './alert-list/alert-list.component';
import { AlertDetailsComponent } from './alert-details/alert-details.component';
import { UserListComponent } from './user-list/user-list.component';
import { UserDetailsComponent } from './user-details/user-details.component';
import { AlertCreateComponent } from './alert-create/alert-create.component';
import { AlertModifyComponent } from './alert-modify/alert-modify.component';

const routes: Routes = [
  { path: 'logs', component: LogListComponent },
  { path: 'log-details/:id', component: LogDetailsComponent },
  { path: 'alerts', component: AlertListComponent },
  { path: 'alert-details/:id', component: AlertDetailsComponent },
  { path: 'create-alert', component: AlertCreateComponent },
  { path: 'modify-alert/:id', component: AlertModifyComponent },
  { path: 'users', component: UserListComponent },
  { path: 'user-details/:email', component: UserDetailsComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
