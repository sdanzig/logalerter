import { Alert } from '../alert';
import { Component, OnInit, Input } from '@angular/core';
import { AlertService } from '../alert.service';
import { Router, ActivatedRoute } from '@angular/router';
import { Note } from '../note';
import { Observable } from 'rxjs';


@Component({
  selector: 'app-alert-details',
  templateUrl: './alert-details.component.html',
  styleUrls: ['./alert-details.component.css']
})
export class AlertDetailsComponent implements OnInit {

  id: number;
  alert: Alert;
  notes: Observable<Note[]>;

  constructor(private route: ActivatedRoute,private router: Router,
              private alertService: AlertService) { }

  ngOnInit() {
    this.alert = new Alert();

    this.id = this.route.snapshot.params['id'];

    this.alertService.getAlert(this.id)
      .subscribe(data => {
        console.log(data)
        this.alert = data;
      }, error => console.log(error));
    this.reloadData();
  }

  reloadData() {
    this.notes = this.alertService.getNotesListForAlert(this.id);
  }
  modifyAlert(id: number){
    this.router.navigate(['modify-alert', id]);
  }
  list(){
    this.router.navigate(['alerts']);
  }
}