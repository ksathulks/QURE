import { Component } from '@angular/core';
import { WebSocketAPI } from './WebSocketAPI';
import * as $ from "jquery";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'angular8-springboot-websocket';

  webSocketAPI: WebSocketAPI;
  name: string;
  convo:string;
  msg:string;

   date: string = new Date().toLocaleString();
  parser:string[];
  ngOnInit() {
    this.webSocketAPI = new WebSocketAPI(new AppComponent());
  }

  connect(){
    this.webSocketAPI._connect();
    
  }

  disconnect(){
    this.webSocketAPI._disconnect();
  }

  sendMessage(){
    this.webSocketAPI._send(this.name);
  }
  recievedMessage(message)
  {
    this.convo=JSON.parse(message);
    console.log(message.class);
    this.msg = this.convo.slice(13, this.convo.length-2);
    $("#convo").append("<tr><td>" + "<h5>"+this.msg +"</h5>"+ "<span class='time-left'>"+this.date+"</span>"+"</td></tr>"); 
  }


  
  
}

//js of ui

