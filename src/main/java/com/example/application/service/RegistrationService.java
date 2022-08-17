package com.example.application.service;

import com.example.application.dto.Patient;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class RegistrationService {

     private static RegistrationService registrationService = new RegistrationService();

     private RegistrationService(){}

     public static RegistrationService getInstance() {
       return registrationService;
     }

     static AtomicInteger counter = new AtomicInteger(0);

     private Map<Integer, Patient> patientMap = new HashMap<>();

     public void create(Patient patient) {
       int patientPK = counter.incrementAndGet();
       patient.setId(patientPK);
       patientMap.put(patientPK, patient);
     }

     public List<Patient> getAllPatients() {
       return patientMap.values().stream().collect(Collectors.toList());
     }

}
