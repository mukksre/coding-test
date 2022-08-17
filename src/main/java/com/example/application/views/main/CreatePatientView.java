package com.example.application.views.main;

import com.example.application.dto.Patient;
import com.example.application.service.RegistrationService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;

@PageTitle("Create Patient")
@Route(value = "create")
public class CreatePatientView extends HorizontalLayout {

    private RegistrationService registrationService = RegistrationService.getInstance();

    private TextField nameField;
    private TextField dobField;
    private TextField phoneNumberField;
    private TextField emailField;
    private TextField addressLine1Field;
    private TextField addressLine2Field;
    private TextField cityField;
    private TextField stateField;
    private TextField zipCodeField;
    private DateTimePicker appointmentTime;
    private Label imageLabel;
    private byte[] imageByteArray;
    private Upload imageUploadComponent;

    private Button registerButton;

    Pattern phoneNumberPattern = Pattern.compile("^\\d{10}$");
    Pattern zipCodePattern = Pattern.compile("^\\d{5}$");
    Pattern emailPattern = Pattern.compile("^(.+)@(.+)$");
    Pattern dobPattern = Pattern.compile("^[0-3]?[0-9]/[0-3]?[0-9]/(?:[0-9]{2})?[0-9]{2}$");
    String usaStateList = "AK, AL, AR, AS, AZ, CA, CO, CT, DC, DE, FL, GA, GU, HI, IA, ID, IL, IN, KS, KY, LA, MA, MD, ME, MI, MN, MO, MP, MS, MT, NC, ND, NE, NH, NJ, NM, NV, NY, OH, OK, OR, PA, PR, RI, SC, SD, TN, TX, UM, UT, VA, VI, VT, WA, WI, WV, WY";
    Set<String> usaStates = new HashSet<>();

    {
        Arrays.stream(usaStateList.split(",")).forEach(x -> {
            usaStates.add(x.trim());
        });
    }

    public CreatePatientView() {
        setWidth(100, Unit.PERCENTAGE);
        setHeight(100, Unit.PERCENTAGE);

        VerticalLayout spacerLayout = new VerticalLayout();
        spacerLayout.setWidth(33, Unit.PERCENTAGE);
        spacerLayout.setHeight(100, Unit.PERCENTAGE);


        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidth(67, Unit.PERCENTAGE);
        verticalLayout.setHeight(100, Unit.PERCENTAGE);

        add(spacerLayout, verticalLayout);

        nameField = new TextField("Name");
        dobField = new TextField("Date of Birth");
        HorizontalLayout nameDoBLayout = new HorizontalLayout();
        nameDoBLayout.add(nameField, dobField);
        phoneNumberField = new TextField("Phone #");
        emailField = new TextField("Email");
        HorizontalLayout phoneNumberEmailLayout = new HorizontalLayout();
        phoneNumberEmailLayout.add(phoneNumberField, emailField);
        addressLine1Field = new TextField("Address Line 1");
        addressLine2Field = new TextField("Address Line 2");
        HorizontalLayout addressLineLayout = new HorizontalLayout();
        addressLineLayout.add(addressLine1Field);
        addressLineLayout.add(addressLine2Field);
        cityField = new TextField("City");
        stateField = new TextField("State");
        zipCodeField = new TextField("Zip Code");
        HorizontalLayout cityStateZipLayout = new HorizontalLayout();
        cityStateZipLayout.add(cityField);
        cityStateZipLayout.add(stateField);
        cityStateZipLayout.add(zipCodeField);
        appointmentTime = new DateTimePicker("Appointment Time");
        MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
        HorizontalLayout uploadLayout = new HorizontalLayout();
        imageLabel = new Label("Upload Driver License");
        imageUploadComponent = new Upload(buffer);
        imageUploadComponent.addSucceededListener(event -> {
            String fileName = event.getFileName();
            InputStream inputStream = buffer.getInputStream(fileName);
            try {
                imageByteArray = IOUtils.toByteArray(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        uploadLayout.add(imageLabel, imageUploadComponent);

        registerButton = new Button("Register");
        registerButton.addClickListener(e -> {
            resetErrorMessages();
            int errors = validateInputs();
            if(errors == 0) {
                Patient patient = getPatientFromForm();
                registrationService.create(patient);
                Notification successRegistrationNotification =
                    Notification.show("Patient registered", 3000, Notification.Position.TOP_CENTER);
                successRegistrationNotification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
        });
        registerButton.addClickShortcut(Key.ENTER);
        HorizontalLayout registerShowAllLayout = new HorizontalLayout();
        registerShowAllLayout.add(registerButton);

        setMargin(true);
        Label heading = new Label("Patient Registration");
        setVerticalComponentAlignment(Alignment.CENTER, verticalLayout);
        verticalLayout.add(heading, nameDoBLayout, phoneNumberEmailLayout,
            addressLineLayout, cityStateZipLayout, appointmentTime, uploadLayout, registerButton);
    }

    private Patient getPatientFromForm() {
        Patient patient = new Patient();
        patient.setAddressLine2(addressLine2Field.getValue().trim());
        patient.setAddressLine1(addressLine1Field.getValue().trim());
        patient.setCity(cityField.getValue().trim());
        patient.setEmail(emailField.getValue().trim());
        patient.setName(nameField.getValue().trim());
        patient.setState(stateField.getValue().trim());
        patient.setPhoneNumber(phoneNumberField.getValue().trim());
        patient.setState(stateField.getValue().trim());
        patient.setZipCode(zipCodeField.getValue().trim());
        patient.setImageBytes(imageByteArray);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            patient.setDateOfBirth(sdf.parse(dobField.getValue().trim()));
            Instant instant = appointmentTime.getValue().atZone(ZoneId.systemDefault()).toInstant();
            Date appointmentDate = Date.from(instant);
            patient.setAppointmentTime(appointmentDate);
        }catch (Exception ex){
            //This really should not happen as the input has already been sanitized..
            ex.printStackTrace();
            Notification uploadFailedNotification =
                Notification.show("Upload of License failed", 3000, Notification.Position.MIDDLE);
            uploadFailedNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
        return patient;
    }

    private int validateInputs() {
        int errorsCount = 0;
        if (nameField == null || nameField.isEmpty()) {
            nameField.setErrorMessage("Name is required");
            nameField.setInvalid(true);
            errorsCount++;
        }
        if(dobField == null || dobField.isEmpty()) {
            dobField.setErrorMessage("Dob Field is required");
            dobField.setInvalid(true);
            errorsCount++;
        } else {
            Matcher dobMatcher = dobPattern.matcher(dobField.getValue().trim());
            if(!dobMatcher.matches()) {
                dobField.setErrorMessage("Date of Birth should match MM/DD/YYYY pattern");
                dobField.setInvalid(true);
                errorsCount++;
            }
        }
        if(phoneNumberField == null || phoneNumberField.isEmpty()) {
            phoneNumberField.setErrorMessage("Phone number field is required");
            phoneNumberField.setInvalid(true);
            errorsCount++;
        } else {
            Matcher phoneNumberMatcher = phoneNumberPattern.matcher(phoneNumberField.getValue().trim());
            if (!phoneNumberMatcher.matches()) {
                phoneNumberField.setErrorMessage("Phone number has to match 1234567890");
                phoneNumberField.setInvalid(true);
                errorsCount++;
            }
        }
        if(emailField == null || emailField.isEmpty()) {
            emailField.setErrorMessage("Email field is required");
            emailField.setInvalid(true);
            errorsCount++;
        }else {
            Matcher emailMatcher = emailPattern.matcher(emailField.getValue().trim());
            if(!emailMatcher.matches()) {
                emailField.setErrorMessage("Email field should match this pattern mike@abc.com");
                emailField.setInvalid(true);
                errorsCount++;
            }
        }
        if(addressLine1Field == null || addressLine1Field.isEmpty()) {
            addressLine1Field.setErrorMessage("Address Line 1 is required");
            addressLine1Field.setInvalid(true);
            errorsCount++;
        }
        if(cityField == null || cityField.isEmpty()) {
            cityField.setErrorMessage("City is required");
            cityField.setInvalid(true);
            errorsCount++;
        }
        if(stateField == null || stateField.isEmpty()) {
            stateField.setErrorMessage("State is required");
            stateField.setInvalid(true);
            errorsCount++;
        } else {
            if (!usaStates.contains(stateField.getValue().trim().toUpperCase(Locale.ROOT))) {
                stateField.setErrorMessage("State field must be one of 50 USA state 2 digit codes...Example TX");
                stateField.setInvalid(true);
                errorsCount++;
            }
        }
        if(zipCodeField == null || zipCodeField.isEmpty()) {
            zipCodeField.setErrorMessage("Zipcode is required");
            zipCodeField.setInvalid(true);
            errorsCount++;
        }else {
            Matcher zipCodeMatcher = zipCodePattern.matcher(zipCodeField.getValue().trim());
            if(!zipCodeMatcher.matches()) {
                zipCodeField.setErrorMessage("Zipcode should match the pattern 12345");
                zipCodeField.setInvalid(true);
                errorsCount++;
            }
        }
        if(appointmentTime == null || appointmentTime.getValue() == null) {
            appointmentTime.setErrorMessage("Appointment Time is required");
            appointmentTime.setInvalid(true);
            errorsCount++;
        }
        if(imageByteArray == null) {
            Notification missingImageNotification =
                Notification.show("Missing driver license file", 3000, Notification.Position.MIDDLE);
            missingImageNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            errorsCount++;
        }
        if(imageByteArray != null && imageByteArray.length > 1000000000) {
            Notification imageTooLargeNotification =
                Notification.show("Driver License File is too large to upload", 3000, Notification.Position.MIDDLE);
            imageTooLargeNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            errorsCount++;
        }
        return errorsCount;
    }

    private void resetErrorMessages() {
        nameField.setErrorMessage(null);
        nameField.setInvalid(false);
        dobField.setErrorMessage(null);
        dobField.setInvalid(false);
        phoneNumberField.setErrorMessage(null);
        phoneNumberField.setInvalid(false);
        emailField.setErrorMessage(null);
        emailField.setInvalid(false);
        addressLine1Field.setErrorMessage(null);
        addressLine1Field.setInvalid(false);
        addressLine2Field.setErrorMessage(null);
        addressLine2Field.setInvalid(false);
        cityField.setErrorMessage(null);
        cityField.setInvalid(false);
        stateField.setErrorMessage(null);
        stateField.setInvalid(false);
        zipCodeField.setErrorMessage(null);
        zipCodeField.setInvalid(false);
        appointmentTime.setErrorMessage(null);
        appointmentTime.setInvalid(false);
        addressLine1Field.setErrorMessage(null);
        addressLine1Field.setInvalid(false);
        cityField.setErrorMessage(null);
        cityField.setInvalid(false);
        stateField.setErrorMessage(null);
        stateField.setInvalid(false);
        zipCodeField.setErrorMessage(null);
        zipCodeField.setInvalid(false);
        appointmentTime.setErrorMessage(null);
        appointmentTime.setInvalid(false);
    }


}
