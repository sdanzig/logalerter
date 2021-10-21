import { Log } from '../log';
import { Component, OnInit, Input } from '@angular/core';
import { LogService } from '../log.service';
import { Router, ActivatedRoute } from '@angular/router';


@Component({
  selector: 'app-log-details',
  templateUrl: './log-details.component.html',
  styleUrls: ['./log-details.component.css']
})
export class LogDetailsComponent implements OnInit {

  id: number;
  log: Log;

  constructor(private route: ActivatedRoute,private router: Router,
              private logService: LogService) { }

  ngOnInit() {
    this.log = new Log();

    this.id = this.route.snapshot.params['id'];

    this.logService.getLog(this.id)
      .subscribe(data => {
        console.log(data)
        this.log = data;
      }, error => console.log(error));
  }

  list(){
    this.router.navigate(['logs']);
  }
}