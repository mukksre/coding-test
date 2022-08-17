package com.example.application.views.listall;

import com.example.application.dto.Patient;
import com.example.application.service.RegistrationService;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import java.util.Base64;
import java.util.List;

@PageTitle("List All View")
@Route(value = "listall")
public class ListAllView extends HorizontalLayout {

    private RegistrationService registrationService = RegistrationService.getInstance();

    private static String TRANSPARENT_GIF_1PX =
        "data:image/gif;base64,R0lGODlhAQABAIAAAP///wAAACwAAAAAAQABAAACAkQBADs=";

    public ListAllView() {
        String user = (String)VaadinSession.getCurrent().getAttribute("user");
        if(user.equals("patient")) {
            Notification inPermissibleNotification =
                Notification.show("You are not permitted to see this", 3000, Notification.Position.MIDDLE);
            inPermissibleNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }else if (user.equals("admin")){
            setWidth(100, Unit.PERCENTAGE);
            setHeight(100, Unit.PERCENTAGE);
            VerticalLayout verticalLayout = new VerticalLayout();
            verticalLayout.setWidth(100, Unit.PERCENTAGE);
            verticalLayout.setHeight(100, Unit.PERCENTAGE);
            add(verticalLayout);
            verticalLayout.add(new Label("Patient Registration List"));
            Grid<Patient> grid = new Grid<>();
            List<Patient> allPatients = registrationService.getAllPatients();
            grid.setItems(allPatients);
            grid.addColumn(Patient::getId).setHeader("ID").setResizable(true);
            grid.addColumn(Patient::getName).setHeader("Name").setResizable(true).setWidth("100px");
            grid.addColumn(Patient::getDateOfBirth).setHeader("Date of birth").setResizable(true);
            grid.addColumn(Patient::getEmail).setHeader("Email").setResizable(true).setWidth("100px");
            grid.addColumn(Patient::getAddressLine1).setHeader("Address Line 1").setResizable(true).setWidth("100px");
            grid.addColumn(Patient::getAddressLine2).setHeader("Address Line 2").setResizable(true).setWidth("100px");
            grid.addColumn((Patient::getCity)).setHeader("City").setResizable(true);
            grid.addColumn(Patient::getState).setHeader("State").setResizable(true);
            grid.addColumn(Patient::getZipCode).setHeader("Zip Code").setResizable(true);
            grid.addColumn(Patient::getAppointmentTime).setHeader("Appointment Time").setResizable(true).setWidth("150px");
            grid.addColumn(
                TemplateRenderer
                    .<Patient>of(
                        "<div><img style='height: 80px; width: 80px;' src='[[item.imageBytes]]' alt='[[item.name]]'/></div>"
                    )
                    .withProperty("imageBytes", item -> getImageAsBase64(item.getImageBytes()))
                    .withProperty("name", item -> item.getName())
            ).setHeader("Driver License");
            verticalLayout.add(grid);
        }
    }

    private String getImageAsBase64(byte[] string) {
        String mimeType = "image/png";
        String htmlValue = null;
        if (string == null) htmlValue = TRANSPARENT_GIF_1PX; else htmlValue =
            "data:" + mimeType + ";base64," + Base64.getEncoder().encodeToString(string);
        return htmlValue;
    }
}
