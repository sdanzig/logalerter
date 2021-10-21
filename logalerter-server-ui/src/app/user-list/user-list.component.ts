import { Component, OnInit } from '@angular/core';

import { Observable } from "rxjs";
import { UserService } from "../user.service";
import { User } from "../user";
import { Router } from '@angular/router';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css']
})
export class UserListComponent implements OnInit {

  users: Observable<User[]>;

  constructor(private userService: UserService,
              private router: Router) {
    this.users = new Observable();
  }

  ngOnInit() {
    this.reloadData();
  }

  reloadData() {
    this.users = this.userService.getUsersList();
  }

  userDetails(email: string){
    console.log("Executing userDetails("+email+")")
    this.router.navigate(['user-details', email]);
  }
}