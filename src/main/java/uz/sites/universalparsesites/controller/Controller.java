package uz.sites.universalparsesites.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.sites.universalparsesites.service.ServicePars;

import java.io.IOException;

@RestController
@RequestMapping("/api/sites")
@CrossOrigin("*")
public class Controller {

    @Autowired
    private ServicePars service;

    @GetMapping()
    public HttpEntity<?> getAllSite() throws InterruptedException, IOException {
        service.getAllSite();
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/save")
    public ResponseEntity<Resource> downloadNewInf() {
        ResponseEntity<Resource> resourceResponseEntity = service.downloadNewInf();
        return resourceResponseEntity;
    }

}
