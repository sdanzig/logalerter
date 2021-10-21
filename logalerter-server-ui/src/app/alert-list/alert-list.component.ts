import { Component, OnInit } from '@angular/core';

import { Observable } from "rxjs";
import { AlertService } from "../alert.service";
import { Alert } from "../alert";
import { Router } from '@angular/router';

@Component({
  selector: 'app-alert-list',
  templateUrl: './alert-list.component.html',
  styleUrls: ['./alert-list.component.css']
})
export class AlertListComponent implements OnInit {

  alerts: Observable<Alert[]>;

  constructor(private alertService: AlertService,
              private router: Router) {
    this.alerts = new Observable();
  }

  ngOnInit() {
    this.reloadData();
  }

  reloadData() {
    this.alerts = this.alertService.getAlertsList();
  }

  alertDetails(id: number){
    this.router.navigate(['alert-details', id]);
  }
}