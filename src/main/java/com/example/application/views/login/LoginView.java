package com.example.application.views.login;

import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route("")
public class LoginView extends HorizontalLayout {

  public LoginView() {
    String user = (String)VaadinSession.getCurrent().getAttribute("user");
    System.out.println(user);
    LoginForm loginForm = new LoginForm();
    loginForm.addLoginListener(x -> {
      if((x.getUsername().equals("patient") || x.getUsername().equals("admin")) && (x.getUsername()+"100").equals(x.getPassword())) {
        VaadinSession.getCurrent().setAttribute("user", x.getUsername());
        if(x.getUsername().equals("patient")){
          getUI().get().navigate("create");
        }else{
          getUI().get().navigate("listall");
        }
      }else {
        Notification loginFailureNotification = Notification.show("Login Failed", 1000, Notification.Position.MIDDLE);
        loginFailureNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        getUI().get().getPage().reload();
      }
    });
    add(loginForm);
  }
}
