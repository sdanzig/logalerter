import { Component, OnInit } from '@angular/core';

import { Observable } from "rxjs";
import { LogService } from "../log.service";
import { Log } from "../log";
import { Router } from '@angular/router';

@Component({
  selector: 'app-log-list',
  templateUrl: './log-list.component.html',
  styleUrls: ['./log-list.component.css']
})
export class LogListComponent implements OnInit {

  logs: Observable<Log[]>;

  constructor(private logService: LogService,
              private router: Router) {
    this.logs = new Observable();
  }

  ngOnInit() {
    this.reloadData();
  }

  reloadData() {
    this.logs = this.logService.getLogsList();
  }

  logDetails(id: number){
    this.router.navigate(['log-details', id]);
  }
}