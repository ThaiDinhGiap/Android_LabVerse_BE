package com.mss.prm_project.controller;


import com.mss.prm_project.service.CollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/collections")
public class CollectionController {

    private final CollectionService collectionService;


}
