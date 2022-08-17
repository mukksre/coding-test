package com.example.application.views.login;

import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;

@Route("")
public class LoginView extends HorizontalLayout {

  public LoginView() {
    LoginForm loginForm = new LoginForm();
    loginForm.addLoginListener(x -> {
      if((x.getUsername().equals("patient") || x.getUsername().equals("admin")) && (x.getUsername()+"100").equals(x.getPassword())) {
        if(x.getUsername().equals("patient")){
          getUI().get().navigate("create");
        }else{
          getUI().get().navigate("listall");
        }
      }else {
        Notification.show("Login Failed", 1000, Notification.Position.MIDDLE);
      }
    });
    add(loginForm);
  }
}
