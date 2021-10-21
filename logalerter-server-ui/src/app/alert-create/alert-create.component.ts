import { Alert } from '../alert';
import { Component, OnInit, Input } from '@angular/core';
import { AlertService } from '../alert.service';
import { Router, ActivatedRoute } from '@angular/router';
import { Note } from '../note';
import { Observable } from 'rxjs';


@Component({
  selector: 'app-alert-create',
  templateUrl: './alert-create.component.html',
  styleUrls: ['./alert-create.component.css']
})
export class AlertCreateComponent implements OnInit {

  id: number;
  alert: Alert;
  notes: Observable<Note[]>;
  submitted: boolean;

  constructor(private route: ActivatedRoute,private router: Router,
              private alertService: AlertService) { }

  ngOnInit() {
    this.submitted = false;

    this.alert = new Alert();

    this.id = this.route.snapshot.params['id'];

    if(this.id !== undefined) {
      this.alertService.getAlert(this.id)
        .subscribe(data => {
          console.log(data)
          this.alert = data;
        }, error => console.log(error));
      this.reloadData();
    }
  }

  reloadData() {
    this.notes = this.alertService.getNotesListForAlert(this.id);
  }
  list(){
    this.router.navigate(['alerts']);
  }

  onSubmit() { this.submitted = true;
    this.alertService.createAlert(this.alert).subscribe();
    this.router.navigate(['alerts']);
  }

  newAlert(){
    this.alert = new Alert();
  }
}