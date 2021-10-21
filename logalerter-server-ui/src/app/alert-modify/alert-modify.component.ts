import { Alert } from '../alert';
import { Component, OnInit, Input } from '@angular/core';
import { AlertService } from '../alert.service';
import { Router, ActivatedRoute } from '@angular/router';
import { Note } from '../note';
import { Observable } from 'rxjs';


@Component({
  selector: 'app-alert-modify',
  templateUrl: './alert-modify.component.html',
  styleUrls: ['./alert-modify.component.css']
})
export class AlertModifyComponent implements OnInit {

  id: number;
  alert: Alert;
  newNote: Note;
  notes: Observable<Note[]>;
  submitted: boolean;

  constructor(private route: ActivatedRoute,private router: Router,
              private alertService: AlertService) { }

  ngOnInit() {
    this.submitted = false;

    this.alert = new Alert();

    this.newNote = new Note();
    this.newNote.email = 'unknown@xyz.com';

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
  alertDetails(id: number){
    this.router.navigate(['alert-details', id]);
  }

  onSubmit() {
    this.submitted = true;
    this.alertService.modifyAlert(this.alert).subscribe();
    this.router.navigate(['alerts']);
  }

  onSubmitNote(id: number) {
    this.submitted = true;
    this.newNote.alertId = this.alert.id;
    this.alertService.addNoteForAlert(this.newNote).subscribe();
    this.router.navigateByUrl('/', {skipLocationChange: true}).then(()=>
      this.router.navigate(['alert-details', id]));
  }

  newAlert(){
    this.alert = new Alert();
  }
}